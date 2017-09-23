package com.bmessenger.bmessenger.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bmessenger.bmessenger.Activities.LoginActivity;
import com.bmessenger.bmessenger.Manager.MessageControl;
import com.bmessenger.bmessenger.R;
import com.google.firebase.messaging.RemoteMessage;


import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMMessagingService";

    private Callbacks mCallbacks;

    public interface Callbacks {
        void messageReceived(String user, String message, String color);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            // In this case the XMPP Server sends a payload data
            String message = remoteMessage.getData().get("message");
            String user = remoteMessage.getData().get("user");
            String color = remoteMessage.getData().get("color");
            Log.d(TAG, "Message received: " + message);
            Log.d(TAG, "User was: " + user);
            Log.d(TAG, "color was: " + color);

            MessageControl messageControl = MessageControl.get(getApplicationContext());
            messageControl.onMessageReceived(user, message, color);
            //showBasicNotification(message);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

//    private void showBasicNotification(String message) {
//        Intent i = new Intent(this,LoginActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setAutoCancel(true)
//                .setContentTitle("Basic Notification")
//                .setContentText(message)
//                .setSmallIcon(R.mipmap.bmessenger_logo)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        manager.notify(0,builder.build());
//
//    }

//    public void messageReceived(String user, String message) {
//        if(mCallbacks == null) {
//            Log.d(TAG, "mCallbacks is null, fragment was removed");
//        }
//        else {
//            mCallbacks.messageReceived(user, message);
//        }
//    }


}