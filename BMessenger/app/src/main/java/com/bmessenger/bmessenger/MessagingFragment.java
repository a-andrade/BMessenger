package com.bmessenger.bmessenger;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import static com.bmessenger.bmessenger.ChannelListFragment.BACKEND_ACTION_ECHO;
import static com.bmessenger.bmessenger.ChannelListFragment.BACKEND_ACTION_MESSAGE;
import static com.bmessenger.bmessenger.ChannelListFragment.FCM_PROJECT_SENDER_ID;
import static com.bmessenger.bmessenger.ChannelListFragment.FCM_SERVER_CONNECTION;
import static com.bmessenger.bmessenger.ChannelListFragment.PAYLOAD_ATTRIBUTE_RECIPIENT;
import static com.bmessenger.bmessenger.ChannelListFragment.RANDOM;

/**
 * Created by uli on 12/5/2016.
 */

public class MessagingFragment extends Fragment  implements ChannelControl.Callbacks{
    public static  final String TAG = "Messaging Fragment";
    private ListView mListView;
    private TextView mTextView;
    private EditText mEditText;
    private Button mSendButton;
    private TextView mTitle;
    private ScrollView mScrollView;
    private User mUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messaging, container, false);
        //mListView = (ListView) v.findViewById(R.id.message_list_ListView);
        mTextView = (TextView) v.findViewById(R.id.message_list_TextView);
        mEditText = (EditText) v.findViewById(R.id.createMessage_EditText);
        mSendButton = (Button) v.findViewById(R.id.sendMessage_Button);
        mTitle = (TextView)v.findViewById(R.id.my_toolbar);
        mScrollView = (ScrollView)v.findViewById(R.id.my_ScrollView);

        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/WireOne.ttf");

        mSendButton.setTypeface(custom_font);
        mTitle.setTypeface(custom_font);


        UserControl userControl = UserControl.get(getContext());
        //final User mUser = userControl.getUser();

//        if (getActivity().getIntent().getExtras() != null) {
//            for (String key : getActivity().getIntent().getExtras().keySet()) {
//                Object value = getActivity().getIntent().getExtras().get(key);
//                Log.d(TAG, "Key: " + key + " Value: " + value);
//            }
//        }
        final String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("cecs491");

        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserControl.get(getContext()).getUserName() == null) {
                    UsernameDialog newDialog = new UsernameDialog();
                    newDialog.show(getFragmentManager(), "missiles");
                }
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Echo Upstream message logic");
                String message = mEditText.getText().toString();
                String user;
                if(UserControl.get(getContext()).getUserName() == null) {
                    user = "anon";
                }
                else {
                    user = UserControl.get(getContext()).getUserName();
                }
                mEditText.setText("");
                //Log.d(TAG, "Message: " + message + ", recipient: " + token);
                FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(FCM_PROJECT_SENDER_ID + FCM_SERVER_CONNECTION)
                        .setMessageId(Integer.toString(RANDOM.nextInt()))
                        .addData(PAYLOAD_ATTRIBUTE_RECIPIENT, "/topics/cecs491")
                        .addData("user", user)
                        .addData("message", message)
                        .addData("action", BACKEND_ACTION_MESSAGE)
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
    public void onAttach(Context context) {
        super.onAttach(context);
        ChannelControl leagueManager = ChannelControl.get(getActivity());
        leagueManager.setCallback(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ChannelControl leagueManager = ChannelControl.get(getActivity());
        leagueManager.removeCallback();
    }
}
