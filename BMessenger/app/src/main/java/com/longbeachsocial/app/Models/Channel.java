package com.longbeachsocial.app.Models;

/**
 * Created by uli on 11/14/2016.
 */

public class Channel {

    public int users;
    public String summary;

    @Override
    public String toString() {
        return users + " " + summary;
    }

    public int getUsers() {
        return users;
    }

    public String getSummary() {
        return summary;
    }

}
