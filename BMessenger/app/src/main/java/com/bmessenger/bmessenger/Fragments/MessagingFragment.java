package com.bmessenger.bmessenger.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bmessenger.bmessenger.Manager.ChannelControl;
import com.bmessenger.bmessenger.R;
import com.bmessenger.bmessenger.Models.User;
import com.bmessenger.bmessenger.Manager.UserControl;
import com.bmessenger.bmessenger.Services.MyFirebaseMessagingService;
import com.bmessenger.bmessenger.Utilities.Util;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import static com.bmessenger.bmessenger.Utilities.Util.BACKEND_ACTION_TOPIC_MESSAGE;
import static com.bmessenger.bmessenger.Utilities.Util.FCM_SERVER_CONNECTION;
import static com.bmessenger.bmessenger.Utilities.Util.PAYLOAD_ATTRIBUTE_RECIPIENT;
import static com.bmessenger.bmessenger.Utilities.Util.RANDOM;

/**
 * Created by uli on 12/5/2016.
 */

public class MessagingFragment extends Fragment  implements MyFirebaseMessagingService.Callbacks {
    public static  final String TAG = "bmessenger.MessageFrag";
    public static final String CHANNEL_NAME = "MessagingFragment.ChannelName";
    private ListView mListView;
    private TextView mTextView;
    private EditText mEditText;
    private Button mSendButton;
    private TextView mTitle;
    private ScrollView mScrollView;
    private User mUser;




    public static MessagingFragment newInstance(String data) {
        MessagingFragment f = new MessagingFragment();
        Bundle args = new Bundle();
        args.putString(MessagingFragment.CHANNEL_NAME, data);
        f.setArguments(args);
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Going o set callback");
//        ChannelControl leagueManager = ChannelControl.get(getActivity());
//        leagueManager.setCallback(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messaging, container, false);
        //mListView = (ListView) v.findViewById(R.id.message_list_ListView);
        mTextView = (TextView) v.findViewById(R.id.message_list_TextView);
        mEditText = (EditText) v.findViewById(R.id.createMessage_EditText);
        mSendButton = (Button) v.findViewById(R.id.sendMessage_Button);

        mScrollView = (ScrollView)v.findViewById(R.id.my_ScrollView);

        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/WireOne.ttf");
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle(UserControl.get(getActivity()).getmChannelName());

        //mSendButton.setTypeface(custom_font);


        mTextView.append("Welcome to " +  UserControl.get(getActivity()).getmChannelName() +  "\n");

        UserControl userControl = UserControl.get(getActivity().getApplicationContext());
        //final User mUser = userControl.getUser();

//        if (getActivity().getIntent().getExtras() != null) {
//            for (String key : getActivity().getIntent().getExtras().keySet()) {
//                Object value = getActivity().getIntent().getExtras().get(key);
//                Log.d(TAG, "Key: " + key + " Value: " + value);
//            }
//        }
        final String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("palmyra");

        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(UserControl.get(getContext()).getUserName() == null) {
//                    UsernameDialog newDialog = new UsernameDialog();
//                    newDialog.show(getFragmentManager(), "missiles");
//                }
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditText.getText().toString();
                String user = UserControl.get(getContext()).getUserName();;
                if(UserControl.get(getContext()).getUserName() == null) {
                    String name = Util.getAnonString();
                    UserControl.get(getContext()).setUsername(name);
                    user = name;
                }
                mEditText.setText("");
                //Log.d(TAG, "Message: " + message + ", recipient: " + token);
                FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(Util.FCM_PROJECT_SENDER_ID + FCM_SERVER_CONNECTION)
                        .setMessageId(Integer.toString(RANDOM.nextInt()))
                        .addData(PAYLOAD_ATTRIBUTE_RECIPIENT, "/topics/palmyra")
                        .addData("user", user)
                        .addData("message", message)
                        .addData("action", BACKEND_ACTION_TOPIC_MESSAGE)
                        .build());
                // To send a message to other device through the XMPP Server, you should add the
                // receiverId and change the action name to BACKEND_ACTION_MESSAGE in the data
            }
        });

        return v;
    }




    public void messageReceived(String user, String message) {
        final String inUser = user;
        final String inMessage = message;
        Log.d(TAG, user + " " + message);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.append(inUser + ": "+ inMessage + "\n");
                mScrollView.fullScroll(View.FOCUS_DOWN);

// your UI code here

            }
        });
    }

    @Override
    public void onPause() {
        Log.d(TAG, "on PAuse");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onSTop");
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyview");
//        ChannelControl leagueManager = ChannelControl.get(getActivity());
//        leagueManager.removeCallback();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestory");
    }
}
