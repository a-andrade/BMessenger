package com.bmessenger.bmessenger;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.w3c.dom.Text;

import java.util.Random;


/**
 * Created by uli on 11/14/2016.
 */

public class ChannelListFragment extends Fragment {

    public static final String FCM_PROJECT_SENDER_ID = "500096274264";
    public static final String FCM_SERVER_CONNECTION = "@gcm.googleapis.com";
    public static final String BACKEND_ACTION_MESSAGE = "com.wedevol.MESSAGE";
    public static final String BACKEND_ACTION_ECHO = "com.wedevol.ECHO";
    public static final String PAYLOAD_ATTRIBUTE_RECIPIENT = "recipient";
    public static final Random RANDOM = new Random();
    private static String TAG = "ChannelListFragment";

    private TextView mChannelTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_channel_list, container, false);

        final String token = FirebaseInstanceId.getInstance().getToken();




        mChannelTextView = (TextView) v.findViewById(R.id.sampleTopic_TextView);


        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/WireOne.ttf");

        mChannelTextView.setTypeface(custom_font);

        mChannelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MessagingActivity.class);
                startActivity(i);
            }
        });

        return v;
    }





}
