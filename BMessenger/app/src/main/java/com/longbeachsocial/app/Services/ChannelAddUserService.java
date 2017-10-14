package com.longbeachsocial.app.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

/**
 * Created by uli on 5/29/2017.
 */

public class ChannelAddUserService extends IntentService {

    private static final String TAG = "RegisterChannelService";
    public static final String EXTRA_CHANNEL = "channelExtra";
    public static final String EXTRA_SERVICE_TYPE = "ServiceTypeExtra";

    private final String channelNode = "channels";
    private final String usersNode = "users";
    private final String summaryNode = "summary";

    public ChannelAddUserService() {
        super("ChannelAddUserService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(channelNode).child(intent.getStringExtra(EXTRA_CHANNEL)).child(usersNode);
            addHelper(databaseReference);
    }

    private void addHelper(DatabaseReference reference) {
        reference.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(mutableData.getValue() == null) {
                    mutableData.setValue(1);
                }
                else {
                    mutableData.setValue((Long)mutableData.getValue() + 1);
                }
                return Transaction.success(mutableData);

            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(TAG, "database udpdate successful");
            }
        });
    }

}
