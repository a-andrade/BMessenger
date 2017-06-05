package com.bmessenger.bmessenger.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;


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

    }

}
