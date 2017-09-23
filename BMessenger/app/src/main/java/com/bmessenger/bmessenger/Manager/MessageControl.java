package com.bmessenger.bmessenger.Manager;

import android.content.Context;
import android.util.Log;

import java.util.Map;

/**
 * Created by uli on 11/14/2016.
 */

public class MessageControl {
    private  static final String TAG = "bmessenger.Control";
    private Callbacks mCallbacks;

    public interface Callbacks {
        void messageReceived(String user, String message, String color);
    }

    private static MessageControl sMessageControl;
    private Context mAppContext;

    private MessageControl(Context context) {
        mAppContext = context;
    }


    public void setCallback(Callbacks c) {
        mCallbacks = c;
        if(mCallbacks != null) {
            Log.d(TAG, "callbacks set");
        }
    }

    public void removeCallback() {
        Log.d(TAG, "Removing Callback");
        mCallbacks = null;
    }

    public static MessageControl get(Context c) {
        if(sMessageControl == null) {
            sMessageControl = new MessageControl(c.getApplicationContext());
        }
        return sMessageControl;
    }

    public void onMessageReceived(String user, String message, String color) {
        if(mCallbacks == null) {
            Log.d(TAG, "mCallbacks is null");
        }
        mCallbacks.messageReceived(user, message, color);
    }
}
