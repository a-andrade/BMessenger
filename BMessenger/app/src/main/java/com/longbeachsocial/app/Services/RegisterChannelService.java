package com.longbeachsocial.app.Services;

import android.app.IntentService;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by uli on 5/20/2017.
 */

public class RegisterChannelService extends IntentService {

    private static final String TAG = "RegisterChannelService";
    public static final String EXTRA_CHANNEL = "channelExtra";
    public static final String EXTRA_SUMMARY = "ServiceTypeExtra";

    private final String channelNode = "channels";
    private final String usersNode = "users";
    private final String summaryNode = "summary";

    public RegisterChannelService() {
        super("RegisterChannelService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FirebaseDatabase.getInstance().getReference(channelNode).child(intent.getStringExtra(EXTRA_CHANNEL)).child(summaryNode).setValue(intent.getStringExtra(EXTRA_SUMMARY));
        FirebaseDatabase.getInstance().getReference(channelNode).child(intent.getStringExtra(EXTRA_CHANNEL)).child(usersNode).setValue(0);

    }

}
