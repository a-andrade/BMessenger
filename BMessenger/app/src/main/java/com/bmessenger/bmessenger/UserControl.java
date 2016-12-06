package com.bmessenger.bmessenger;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by uli on 11/14/2016.
 */

public class UserControl {
    private static UserControl sUserControl;
    private Context mAppContext;

    private User mUser;


    private UserControl(Context context) {
        mAppContext = context;
    }

    public static UserControl get(Context c) {
        if(sUserControl == null) {
            sUserControl = new UserControl(c.getApplicationContext());
        }
        return sUserControl;

    }

    public void setUsername(String UUID, String username) {
        User user = new User(UUID, username);
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }
}
