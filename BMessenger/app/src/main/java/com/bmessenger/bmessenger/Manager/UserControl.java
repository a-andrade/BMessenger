package com.bmessenger.bmessenger.Manager;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.bmessenger.bmessenger.Models.User;
import com.bmessenger.bmessenger.Utilities.Util;

import java.util.Random;

/**
 * Created by uli on 11/14/2016.
 */

public class UserControl {
    private static UserControl sUserControl;
    private Context mAppContext;

    private String mUserColor = null;
    private String mUsername;


    private String mChannelName;

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
        if(mUsername == null) {
            mUsername = Util.getAnonString();
            return mUsername;
        }
        else
            return mUsername;
    }

    public String getmChannelName() {
        return mChannelName;
    }

    public void setmChannelName(String mChannelName) {
        this.mChannelName = mChannelName;
    }

    public String getUserColor() {
        if(mUserColor == null) {
            setRandomColor();
        }
        return mUserColor;
    }

    public void setRandomColor() {
        Random rnd = new Random();
        mUserColor = String.format("#%02x%02x%02x", rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        //mUserColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        //Toast.makeText(mAppContext, mUserColor + " ", Toast.LENGTH_SHORT).show();

    }
}
