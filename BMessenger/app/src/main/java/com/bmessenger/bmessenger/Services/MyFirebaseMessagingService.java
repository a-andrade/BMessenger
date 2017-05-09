package com.bmessenger.bmessenger.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bmessenger.bmessenger.Activities.LoginActivity;
import com.bmessenger.bmessenger.Manager.ChannelControl;
import com.bmessenger.bmessenger.R;
import com.google.firebase.messaging.RemoteMessage;


import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            // In this case the XMPP Server sends a payload data
            String message = remoteMessage.getData().get("message");
            String user = remoteMessage.getData().get("user");
            Log.d(TAG, "Message received: " + message);
            Log.d(TAG, "User was: " + user);

            ChannelControl channelControl = ChannelControl.get(getApplication().getApplicationContext());
            channelControl.onMessageReceived(user, message);
            //showBasicNotification(message);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    private void showBasicNotification(String message) {
        Intent i = new Intent(this,LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Basic Notification")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());

    }


}