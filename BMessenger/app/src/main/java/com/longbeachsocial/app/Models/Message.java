package com.longbeachsocial.app.Models;

/**
 * Created by uli on 11/14/2016.
 */

public class Message {

    public String message;
    public String user;
    public String type;
    public String color;
    public long timestamp;


    public Message() {

    }

    public Message(String message, String user, String color, String type, long timestamp) {
        this.message = message;
        this.user    = user;
        this.color = color;
        this.type = type;
        this.timestamp = timestamp;
    }

}
