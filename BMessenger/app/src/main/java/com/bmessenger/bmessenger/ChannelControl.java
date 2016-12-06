package com.bmessenger.bmessenger;

import android.content.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by uli on 11/14/2016.
 */

public class ChannelControl {

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
    }

    public void removeCallback() {
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
        mCallbacks.messageReceived(user, message);
    }
}
