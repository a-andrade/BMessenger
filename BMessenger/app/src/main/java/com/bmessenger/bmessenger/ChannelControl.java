package com.bmessenger.bmessenger;

import android.content.Context;

import java.util.Map;

/**
 * Created by uli on 11/14/2016.
 */

public class ChannelControl {

    private static ChannelControl sChannelControl;
    private Context mAppContext;

    private Map<String, Integer> TopicRooms;


    private ChannelControl(Context context) {
        mAppContext = context;
    }

    public static ChannelControl get(Context c) {
        if(sChannelControl == null) {
            sChannelControl = new ChannelControl(c.getApplicationContext());
        }
        return sChannelControl;
    }
}
