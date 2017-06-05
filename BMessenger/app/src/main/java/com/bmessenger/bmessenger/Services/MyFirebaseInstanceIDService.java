package com.bmessenger.bmessenger.Services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by uli on 11/19/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService implements IRequestListener {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }
    // [END refresh_token]


    public void onComplete() {
        Log.d(TAG, "Token Registration Complete");
    }

    public void onError(String message) {
        Log.d(TAG, "Error trying to register the token in the DB: " + message);
    }
}