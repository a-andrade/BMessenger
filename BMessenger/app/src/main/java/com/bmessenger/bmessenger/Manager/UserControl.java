package com.bmessenger.bmessenger.Manager;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.bmessenger.bmessenger.Models.User;

import java.util.Random;

/**
 * Created by uli on 11/14/2016.
 */

public class UserControl {
    private static UserControl sUserControl;
    private Context mAppContext;

    private int mUserColor = -1;
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
        return mUsername;
    }

    public String getmChannelName() {
        return mChannelName;
    }

    public void setmChannelName(String mChannelName) {
        this.mChannelName = mChannelName;
    }

    public int getUserColor() {
        if(mUserColor == -1) {
            setRandomColor();
        }
        return mUserColor;
    }

    public void setRandomColor() {
        Random rnd = new Random();
        mUserColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        //Toast.makeText(mAppContext, mUserColor + " ", Toast.LENGTH_SHORT).show();

    }
}
