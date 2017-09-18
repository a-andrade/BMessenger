package com.wedevol.xmpp;

import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;

import java.util.concurrent.CountDownLatch;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

//import com.wedevol.xmpp.bean.CcsOutMessage;
import com.wedevol.xmpp.server.CcsClient;
//import com.wedevol.xmpp.server.MessageHelper;
//import com.wedevol.xmpp.util.Util;

/**
 * Entry Point class for the XMPP Server in dev mode for debugging and testing
 * purposes
 */
public class EntryPoint {
	public static void main(String[] args) throws SmackException, IOException {
//		final String fcmProjectSenderId = "500096274264";
//		final String fcmServerKey = "AIzaSyByVzLJO69btipibWWrfTXzFRxBBEoJTks";
		
		final String fcmProjectSenderId = args[0].toString();
		//System.out.println("Args are " + args[0] + " " + args[1]);
		final String fcmServerKey = args[1].toString();
		
		
//		final String toRegId = "fpqFVEUAu0M:APA91bGW0mTL6daujbfOnhLvX4IUEKwV15OrGQxk04j3K_X3cgt1cvALOekbNjBvA_tDVhpQ-zBLjuCXffimrv-TDEsdVWUZnZYL-i-M-SpychnamIvhk0DNt__jElondkiopTwtmqrt";
		//final String toRegId = "deA-NNbXuqE:APA91bFNFAW2nvdRKMMXFebsJVmAtzmJuD8Kqfx5z-gl06vgQt7AiNv1umWUuHeizE8B6xLAegBEmyu0p5ZqSsWpXB26dy5p-rISQ35oNugn56x2RURgDAugRexWIFlSKezGR1dgqUWd";

		CcsClient ccsClient = CcsClient.prepareClient(fcmProjectSenderId, fcmServerKey, true);

		try {
			ccsClient.connect();
		} catch (XMPPException | InterruptedException e) {
			e.printStackTrace();
		}

//		// Send a sample downstream message to a device
//		String messageId = Util.getUniqueMessageId();
//		Map<String, String> dataPayload = new HashMap<String, String>();
//		dataPayload.put(Util.PAYLOAD_ATTRIBUTE_MESSAGE
//				, "This is the simple sample message");
//		CcsOutMessage message = new CcsOutMessage(toRegId, messageId, dataPayload);
//		String jsonRequest = MessageHelper.createJsonOutMessage(message);
//		ccsClient.send(jsonRequest);

//		while (true) {
//			// TODO: Improve this because the app closes itself after the
//			// execution of the connect method
//		}
		
		try {
			CountDownLatch latch = new CountDownLatch(1);
			latch.await();
		} catch(InterruptedException e) {
			
		}
	}
}
