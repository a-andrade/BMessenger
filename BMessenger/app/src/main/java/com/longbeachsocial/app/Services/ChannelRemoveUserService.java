package com.longbeachsocial.app.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.longbeachsocial.app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;


/**
 * Created by uli on 6/4/2017.
 */

public class ChannelRemoveUserService extends Service {
    private static  final String TAG = "RemoveUserService";
    public static final String EXTRA_CHANNEL = "channelExtra";
    private String channelNode = "channels";
    private String usersNode = "users";
    private String channel;

    @Override
    public void onCreate() {
        super.onCreate();
        // Fires when a service is first initialized
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Fires when a service is started up, do work here!
        // ...
        // Return "sticky" for services that are explicitly
        // started and stopped as needed by the app.
        //channel = intent.getStringExtra(EXTRA_CHANNEL);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        channel = sharedPref.getString(getString(R.string.preference_channel), null);
        Log.d(TAG, "onStartCommand " + channel);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( final Void ... params ) {
                // something you know that will take a few seconds
                FirebaseDatabase.getInstance().getReference(channelNode).child(channel).child(usersNode).runTransaction(new Transaction.Handler() {

                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if(mutableData.getValue() == null) {
                            mutableData.setValue(0);
                        }
                        else {
                            mutableData.setValue((Long)mutableData.getValue() - 1);
                        }
                        return Transaction.success(mutableData);

                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.d(TAG, "database udpdate successful");
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute( final Void result ) {
                // continue what you are doing...
                stopSelf();
            }

        }.execute();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // Cleanup service before destruction
    }
}
