package com.bmessenger.bmessenger.Utilities;

import java.util.Random;

/**
 * Created by uli on 3/28/2017.
 */

public class Util {
    public static final String FCM_PROJECT_SENDER_ID = "500096274264";
    public static final String FCM_SERVER_CONNECTION = "@gcm.googleapis.com";
    public static final String BACKEND_ACTION_TOPIC_MESSAGE = "com.wedevol.TOPIC_MESSAGE";
    public static final String PAYLOAD_ATTRIBUTE_RECIPIENT = "recipient";
    public static final Random RANDOM = new Random();

    public static String getAnonString() {
        //TODO: return "anon" + unique randomID
        return "anon";
    }
}
