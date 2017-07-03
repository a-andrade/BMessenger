package com.bmessenger.bmessenger.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bmessenger.bmessenger.Manager.MessageControl;
import com.bmessenger.bmessenger.R;
import com.bmessenger.bmessenger.Manager.UserControl;
import com.bmessenger.bmessenger.Services.ChannelAddUserService;
import com.bmessenger.bmessenger.Services.ChannelRemoveUserService;
import com.bmessenger.bmessenger.Utilities.Util;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import static com.bmessenger.bmessenger.Utilities.Util.BACKEND_ACTION_TOPIC_MESSAGE;
import static com.bmessenger.bmessenger.Utilities.Util.FCM_SERVER_CONNECTION;
import static com.bmessenger.bmessenger.Utilities.Util.PAYLOAD_ATTRIBUTE_RECIPIENT;
import static com.bmessenger.bmessenger.Utilities.Util.RANDOM;

/**
 * Created by uli on 12/5/2016.
 */

public class MessagingFragment extends Fragment  implements MessageControl.Callbacks {
    public static  final String TAG = "MessagingFragment";
    private TextView mTextView;
    private EditText mEditText;
    private Button mSendButton;
    private ScrollView mScrollView;

    private String mChannel;
    private String mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MessageControl leagueManager = MessageControl.get(getActivity());
        leagueManager.setCallback(this);

        mChannel = UserControl.get(getContext()).getmChannelName();
        mUser = UserControl.get(getContext()).getUserName();
        Intent intent  = new Intent(getActivity(), ChannelAddUserService.class);
        intent.putExtra(ChannelAddUserService.EXTRA_CHANNEL, mChannel);
        getActivity().startService(intent);
        FirebaseMessaging.getInstance().subscribeToTopic( mChannel);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messaging, container, false);
        mTextView = (TextView) v.findViewById(R.id.message_list_TextView);
        mEditText = (EditText) v.findViewById(R.id.createMessage_EditText);
        mSendButton = (Button) v.findViewById(R.id.sendMessage_Button);

        mScrollView = (ScrollView)v.findViewById(R.id.my_ScrollView);

        //Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/WireOne.ttf");
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle(UserControl.get(getActivity()).getmChannelName());



        mTextView.append("Welcome to " +  UserControl.get(getActivity()).getmChannelName() +  "\n");

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(mEditText.getText().toString());
            }
        });

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage(mEditText.getText().toString());
                return true;
            }
        });

//        mSendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String message = mEditText.getText().toString();
//                String user = UserControl.get(getContext()).getUserName();;
//                if(UserControl.get(getContext()).getUserName() == null) {
//                    String name = Util.getAnonString();
//                    UserControl.get(getContext()).setUsername(name);
//                    user = name;
//                }
//                mEditText.setText("");
//                //Log.d(TAG, "Message: " + message + ", recipient: " + token);
//                FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(Util.FCM_PROJECT_SENDER_ID + FCM_SERVER_CONNECTION)
//                        .setMessageId(Integer.toString(RANDOM.nextInt()))
//                        .addData(PAYLOAD_ATTRIBUTE_RECIPIENT, "/topics/palmyra")
//                        .addData("user", user)
//                        .addData("message", message)
//                        .addData("action", BACKEND_ACTION_TOPIC_MESSAGE)
//                        .build());
//                // To send a message to other device through the XMPP Server, you should add the
//                // receiverId and change the action name to BACKEND_ACTION_MESSAGE in the data
//            }
//        });

        return v;
    }

    public void sendMessage(String message) {
        if(mUser == null) {
            String name = Util.getAnonString();
            UserControl.get(getContext()).setUsername(name);
            mUser = name;
        }
        mEditText.setText("");
        //Log.d(TAG, "Message: " + message + ", recipient: " + token);
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(Util.FCM_PROJECT_SENDER_ID + FCM_SERVER_CONNECTION)
                .setMessageId(Integer.toString(RANDOM.nextInt()))
                .addData(PAYLOAD_ATTRIBUTE_RECIPIENT, "/topics/" + mChannel)
                .addData("user", mUser)
                .addData("message", message)
                .addData("action", BACKEND_ACTION_TOPIC_MESSAGE)
                .build());
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

            }
        });
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        Intent intent  = new Intent(getActivity(), ChannelRemoveUserService.class);
        intent.putExtra(ChannelRemoveUserService.EXTRA_CHANNEL, UserControl.get(getActivity()).getmChannelName());
        getActivity().startService(intent);

        FirebaseMessaging.getInstance().unsubscribeFromTopic(mChannel);
        super.onDestroy();
    }

}
