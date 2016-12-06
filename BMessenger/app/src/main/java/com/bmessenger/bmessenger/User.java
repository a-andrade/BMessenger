package com.bmessenger.bmessenger;

/**
 * Created by uli on 11/14/2016.
 */

public class User {
    String mUsername;
    String mUUID;

    public User() {}

    public User(String UUID, String username) {
        mUUID = UUID;
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }
}
