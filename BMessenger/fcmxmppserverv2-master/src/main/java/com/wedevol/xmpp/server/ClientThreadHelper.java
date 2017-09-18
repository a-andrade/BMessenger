package com.wedevol.xmpp.server;

import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.jivesoftware.smack.packet.Stanza;


import com.wedevol.xmpp.bean.CcsInMessage;
import com.wedevol.xmpp.bean.CcsOutMessage;
import com.wedevol.xmpp.service.PayloadProcessor;
import com.wedevol.xmpp.util.Util;

public class ClientThreadHelper implements Runnable {
	
	private XMPPTCPConnection mConnection;
	private Stanza mPacket;
	
	public ClientThreadHelper(XMPPTCPConnection conn, Stanza packet) {
		mConnection = conn;
		mPacket = packet;
	}
	
	
	public void run() {
		//System.out.println("Thread # " + Thread.currentThread().getId());
		processStanza();
	}
	
	
	public void processStanza() {
//		logger.log(Level.INFO, "Received: " + packet.toXML());
		GcmPacketExtension gcmPacket = (GcmPacketExtension) mPacket.getExtension(Util.FCM_NAMESPACE);
		String json = gcmPacket.getJson();
		try {
			Map<String, Object> jsonMap = (Map<String, Object>) JSONValue.parseWithException(json);
			Object messageType = jsonMap.get("message_type");

			if (messageType == null) {
				CcsInMessage inMessage = MessageHelper.createCcsInMessage(jsonMap);
				handleUpstreamMessage(inMessage); // normal upstream message
				return;
			}

			switch (messageType.toString()) {
			case "ack":
				handleAckReceipt(jsonMap);
				break;
			case "nack":
				handleNackReceipt(jsonMap);
				break;
			case "receipt":
				handleDeliveryReceipt(jsonMap);
				break;
			case "control":
				handleControlMessage(jsonMap);
				break;
			default:
//				logger.log(Level.INFO, "Received unknown FCM message type: " + messageType.toString());
			}
		} catch (ParseException e) {
//			logger.log(Level.INFO, "Error parsing JSON: " + json, e.getMessage());
		}
	}
	
	/**
	 * Handles an upstream message from a device client through FCM
	 */
	private void handleUpstreamMessage(CcsInMessage inMessage) {
		final String action = inMessage.getDataPayload().get(Util.PAYLOAD_ATTRIBUTE_ACTION);
		if (action != null) {
			
		        String messageId = Util.getUniqueMessageId();
		        String to = inMessage.getDataPayload().get(Util.PAYLOAD_ATTRIBUTE_RECIPIENT);

		        // TODO: handle the data payload sent to the client device. Here, I just
		        // resend the incoming message.
		        CcsOutMessage outMessage = new CcsOutMessage(to, messageId, inMessage.getDataPayload());
		        String jsonRequest = MessageHelper.createJsonOutMessage(outMessage);
		        send(jsonRequest);
		}

		// Send ACK to FCM
		String ack = MessageHelper.createJsonAck(inMessage.getFrom(), inMessage.getMessageId());
		send(ack);
	}

	/**
	 * Handles an ACK message from FCM
	 */
	private void handleAckReceipt(Map<String, Object> jsonMap) {
		// TODO: handle the ACK in the proper way
	}

	/**
	 * Handles a NACK message from FCM
	 */
	private void handleNackReceipt(Map<String, Object> jsonMap) {
		String errorCode = (String) jsonMap.get("error");

		if (errorCode == null) {
//			logger.log(Level.INFO, "Received null FCM Error Code");
			return;
		}

		switch (errorCode) {
		case "INVALID_JSON":
			handleUnrecoverableFailure(jsonMap);
			break;
		case "BAD_REGISTRATION":
			handleUnrecoverableFailure(jsonMap);
			break;
		case "DEVICE_UNREGISTERED":
			handleUnrecoverableFailure(jsonMap);
			break;
		case "BAD_ACK":
			handleUnrecoverableFailure(jsonMap);
			break;
		case "SERVICE_UNAVAILABLE":
			handleServerFailure(jsonMap);
			break;
		case "INTERNAL_SERVER_ERROR":
			handleServerFailure(jsonMap);
			break;
		case "DEVICE_MESSAGE_RATE_EXCEEDED":
			handleUnrecoverableFailure(jsonMap);
			break;
		case "TOPICS_MESSAGE_RATE_EXCEEDED":
			handleUnrecoverableFailure(jsonMap);
			break;
		case "CONNECTION_DRAINING":
			handleConnectionDrainingFailure();
			break;
		default:
//			logger.log(Level.INFO, "Received unknown FCM Error Code: " + errorCode);
		}
	}

	/**
	 * Handles a Delivery Receipt message from FCM (when a device confirms that
	 * it received a particular message)
	 */
	private void handleDeliveryReceipt(Map<String, Object> jsonMap) {
		// TODO: handle the delivery receipt
	}

	/**
	 * Handles a Control message from FCM
	 */
	private void handleControlMessage(Map<String, Object> jsonMap) {
		// TODO: handle the control message
		String controlType = (String) jsonMap.get("control_type");

		if (controlType.equals("CONNECTION_DRAINING")) {
			handleConnectionDrainingFailure();
		} else {
//			logger.log(Level.INFO, "Received unknown FCM Control message: " + controlType);
		}
	}

	private void handleServerFailure(Map<String, Object> jsonMap) {
		// TODO: Resend the message
//		logger.log(Level.INFO, "Server error: " + jsonMap.get("error") + " -> " + jsonMap.get("error_description"));

	}

	private void handleUnrecoverableFailure(Map<String, Object> jsonMap) {
		// TODO: handle the unrecoverable failure
//		logger.log(Level.INFO,
//				"Unrecoverable error: " + jsonMap.get("error") + " -> " + jsonMap.get("error_description"));
	}

	private void handleConnectionDrainingFailure() {
		// TODO: handle the connection draining failure. Force reconnect?
//		logger.log(Level.INFO, "FCM Connection is draining! Initiating reconnection ...");
	}
	
	
    private void handleMessage(CcsInMessage inMessage) {
        CcsClient client = CcsClient.getInstance();
        String messageId = Util.getUniqueMessageId();
        String to = inMessage.getDataPayload().get(Util.PAYLOAD_ATTRIBUTE_RECIPIENT);

        // TODO: handle the data payload sent to the client device. Here, I just
        // resend the incoming message.
        CcsOutMessage outMessage = new CcsOutMessage(to, messageId, inMessage.getDataPayload());
        String jsonRequest = MessageHelper.createJsonOutMessage(outMessage);
        send(jsonRequest);
    }

	/**
	 * Sends a downstream message to FCM
	 */
	public void send(String jsonRequest) {
		// TODO: Resend the message using exponential back-off!
		Stanza request = new GcmPacketExtension(jsonRequest).toPacket();
		try {
//			logger.log(Level.INFO, "SENDING STANZA", "SEND");
			//System.out.println(request.toXML());
			synchronized(mConnection) {
				mConnection.sendStanza(request);
			}
			
		} catch (NotConnectedException | InterruptedException e) {
//			logger.log(Level.INFO, "There is no connection and the packet could not be sent: {}", request.toXML());
		}
	}
}
