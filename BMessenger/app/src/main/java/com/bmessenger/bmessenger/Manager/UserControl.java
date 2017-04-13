package com.bmessenger.bmessenger.Manager;

import android.content.Context;

import com.bmessenger.bmessenger.Models.User;

/**
 * Created by uli on 11/14/2016.
 */

public class UserControl {
    private static UserControl sUserControl;
    private Context mAppContext;

    private User mUser;
    String mUsername;


    String mChannelName;

    private UserControl(Context context) {
        mAppContext = context;
    }

    public static UserControl get(Context c) {
        if(sUserControl == null) {
            sUserControl = new UserControl(c.getApplicationContext());
        }
        return sUserControl;

    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getUserName() {
        return mUsername;
    }

    public String getmChannelName() {
        return mChannelName;
    }

    public void setmChannelName(String mChannelName) {
        this.mChannelName = mChannelName;
    }
}
