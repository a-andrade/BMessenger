package com.bmessenger.bmessenger.Manager;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.Map;

/**
 * Created by uli on 11/14/2016.
 */

public class ChannelControl {
    private  static final String TAG = "bmessenger.Control";
    private Callbacks mCallbacks;

    public interface Callbacks {
        void messageReceived(String user, String message);
    }

    private static ChannelControl sChannelControl;
    private Context mAppContext;

    private Map<String, Integer> mTopicRooms;



    private ChannelControl(Context context) {
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

    public static ChannelControl get(Context c) {
        if(sChannelControl == null) {
            sChannelControl = new ChannelControl(c.getApplicationContext());
        }
        return sChannelControl;
    }

    public void addChanel(String name) {
        mTopicRooms.put(name, 0);
    }

    public Array[] getChannels() {
        return (Array[])mTopicRooms.keySet().toArray();
    }

    public void onMessageReceived(String user, String message) {
        if(mCallbacks == null) {
            Log.d(TAG, "mCallbacks is null");
        }
        mCallbacks.messageReceived(user, message);
    }
}
