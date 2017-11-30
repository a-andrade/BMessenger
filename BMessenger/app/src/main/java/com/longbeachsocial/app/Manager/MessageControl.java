package com.longbeachsocial.app.Manager;

import android.content.Context;
import android.util.Log;

/**
 * Created by uli on 11/14/2016.
 */

public class MessageControl {
    private  static final String TAG = "longbeachsocial.Control";
    private Callbacks mCallbacks;

    public interface Callbacks {
        void messageReceived(String user, String message, String color, String type);
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
        mCallbacks = null;
    }

    public static MessageControl get(Context c) {
        if(sMessageControl == null) {
            sMessageControl = new MessageControl(c.getApplicationContext());
        }
        return sMessageControl;
    }

    public void onMessageReceived(String user, String message, String color, String type) {
        if(mCallbacks == null) {
            Log.d(TAG, "mCallbacks is null");
        } else
            mCallbacks.messageReceived(user, message, color, type);
    }
}
