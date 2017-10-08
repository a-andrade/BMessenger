package com.bmessenger.bmessenger.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bmessenger.bmessenger.Activities.SettingsActivity;
import com.bmessenger.bmessenger.Manager.MessageControl;
import com.bmessenger.bmessenger.R;
import com.bmessenger.bmessenger.Manager.UserControl;
import com.bmessenger.bmessenger.Services.ChannelAddUserService;
import com.bmessenger.bmessenger.Services.ChannelRemoveUserService;
import com.bmessenger.bmessenger.Utilities.Util;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.lang.reflect.Field;
import java.util.Random;

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
    //private int mUserColor;
    private int mUserColor;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setHasOptionsMenu(true);
        MessageControl leagueManager = MessageControl.get(getActivity());
        leagueManager.setCallback(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        mChannel = UserControl.get(getContext()).getmChannelName();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messaging, container, false);
        Log.d(TAG, "onCreateView");

        mTextView = (TextView) v.findViewById(R.id.message_list_TextView);
        mEditText = (EditText) v.findViewById(R.id.createMessage_EditText);
        mSendButton = (Button) v.findViewById(R.id.sendMessage_Button);
        mSendButton.setVisibility(View.INVISIBLE);
        mScrollView = (ScrollView)v.findViewById(R.id.my_ScrollView);

        //Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/WireOne.ttf");
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.messagesToolbar);
        toolbar.setTitle(UserControl.get(getActivity()).getmChannelName());
        toolbar.setEnabled(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mTextView.append("Welcome to " +  UserControl.get(getActivity()).getmChannelName() +  "\n");

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(mEditText.getText().toString());
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() < 1) {
                    mSendButton.setVisibility(View.INVISIBLE);
                }
                else
                    mSendButton.setVisibility(View.VISIBLE);
            }
        });

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage(mEditText.getText().toString());
                return true;
            }
        });


        return v;
    }

//    @Override
public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // Do something that differs the Activity's menu here
    inflater.inflate(R.menu.menu_messages, menu);
    super.onCreateOptionsMenu(menu, inflater);
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.message_settings) {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                .addData("color", String.valueOf(mUserColor))
                .addData("action", BACKEND_ACTION_TOPIC_MESSAGE)
                .build());
    }

    public void messageReceived(String user, final String message, String color) {
        final Spannable inUser = new SpannableString(user + ": " + message);
        //final Spanned spannedString = fromHtml(inUser);
        inUser.setSpan(new ForegroundColorSpan(Integer.parseInt(color))
                , 0, user.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        inUser.setSpan(new StyleSpan(Typeface.BOLD),0,user.length(), 0);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mTextView.append(inUser);
                mTextView.append("\n");
                mScrollView.fullScroll(View.FOCUS_DOWN);

            }
        });
    }



    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TAG, "onSaveInstanceState");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "on RESUME ");
        mUser = mSharedPreferences.getString(getString(R.string.preference_username), UserControl.get(getActivity()).getUserName());

        mUserColor = Color.parseColor(mSharedPreferences.getString(getString(R.string.preference_user_color), UserControl.get(getContext()).getUserColor()));

        Intent intent  = new Intent(getActivity(), ChannelAddUserService.class);
        intent.putExtra(ChannelAddUserService.EXTRA_CHANNEL, mChannel);
        getActivity().startService(intent);
        FirebaseMessaging.getInstance().subscribeToTopic( mChannel);
    }


    @Override
    public void onPause() {
        Log.d(TAG, "onPAuse");
        Intent intent  = new Intent(getActivity(), ChannelRemoveUserService.class);
        intent.putExtra(ChannelRemoveUserService.EXTRA_CHANNEL, UserControl.get(getActivity()).getmChannelName());
        getActivity().startService(intent);

        FirebaseMessaging.getInstance().unsubscribeFromTopic(mChannel);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
