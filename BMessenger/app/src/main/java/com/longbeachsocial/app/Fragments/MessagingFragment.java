package com.longbeachsocial.app.Fragments;

import android.content.Context;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.longbeachsocial.app.Activities.SettingsActivity;
import com.longbeachsocial.app.Manager.MessageControl;
import com.longbeachsocial.app.Models.Message;
import com.longbeachsocial.app.R;
import com.longbeachsocial.app.Manager.UserControl;
import com.longbeachsocial.app.Services.ChannelAddUserService;
import com.longbeachsocial.app.Services.ChannelRemoveUserService;
import com.longbeachsocial.app.Utilities.Util;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

import android.support.v13.view.inputmethod.EditorInfoCompat;
import android.support.v13.view.inputmethod.InputConnectionCompat;
import android.support.v13.view.inputmethod.InputContentInfoCompat;

import static com.longbeachsocial.app.Utilities.Util.BACKEND_ACTION_TOPIC_MESSAGE;
import static com.longbeachsocial.app.Utilities.Util.FCM_SERVER_CONNECTION;
import static com.longbeachsocial.app.Utilities.Util.MESSAGE_MULTIMEDIA;
import static com.longbeachsocial.app.Utilities.Util.MESSAGE_TEXT;
import static com.longbeachsocial.app.Utilities.Util.PAYLOAD_ATTRIBUTE_RECIPIENT;
import static com.longbeachsocial.app.Utilities.Util.RANDOM;

/**
 * Created by uli on 12/5/2016.
 */

public class MessagingFragment extends Fragment  implements MessageControl.Callbacks, InputConnectionCompat.OnCommitContentListener {
    public static  final String TAG = "MessagingFragment";
    private EditText mEditText;
    private Button mSendButton;
    private LinearLayout mLinearLayout;
    private LinearLayout mLayoutChatbox;
    private ScrollView mScrollView;
    private String mChannel;
    private String mUser;
    private int mUserColor;
    private SharedPreferences mSharedPreferences;
    private Toolbar toolbar;
    private Query liveMessagesQuery;
    private Query loadMoreQuery;
    private Query loadFirstQuery;
    private Query lastMessageQuery;
    private Query loadMessageQuery;
    private ChildEventListener loadLiveListener;
    private ChildEventListener loadMoreListener;
    private ChildEventListener loadFirstListener;
    private ChildEventListener loadMessagesListener;
    private LinearLayout progressLinearLayout;
    private TextView loadMessagesTextView;
    private int loaded = 0;
    private int loadedCounter = 0;
    private String lastMessageReceived;
    private String lastMessage;
    private String firstMessageReceived;
    private LinkedBlockingDeque<Message> messagesList;
    private int queryCounter;
    private long messagesToLoad = 0;
    private final int LOAD_MORE = 15;
    private final int LOAD_INITIAL = 20;
    private int messagesFirst;

    private int previousMessagesAvail;
    private int previousMessagesLeft;
    private boolean atLeast20;
    private boolean moreMessages;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MessageControl leagueManager = MessageControl.get(getActivity());
        leagueManager.setCallback(this);
        queryCounter = 0;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mChannel = sharedPref.getString(getString(R.string.preference_channel), null);


        messagesList = new LinkedBlockingDeque<>();

        FirebaseDatabase.getInstance().getReference("messageCounters").child(mChannel).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Log.d(TAG, "value is null");
                    progressLinearLayout.setVisibility(View.GONE);
                    mScrollView.setVisibility(View.VISIBLE);

                } else {
                    long val = (long) dataSnapshot.getValue();
                    previousMessagesAvail = (int) val;
                    previousMessagesLeft = previousMessagesAvail;
                    Log.d(TAG, "we have " + previousMessagesAvail + " messages available");

                    if (previousMessagesAvail > 0)
                        loadInitialMessages(previousMessagesAvail);
                    else {
                        progressLinearLayout.setVisibility(View.GONE);
                        mScrollView.setVisibility(View.VISIBLE);
                    }

                    if (previousMessagesAvail >= 20)
                        atLeast20 = true;
                    else
                        atLeast20 = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        loadMoreListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (s == null) {
//                    Log.d(TAG, "First is not taken " + dataSnapshot.getKey());
                    firstMessageReceived = dataSnapshot.getKey();
                } else {
                    previousMessagesLeft--;
                    queryCounter++;
                    Log.d(TAG, "LoadMORELIStner adding to list " + dataSnapshot.getKey() + " counter is at " + queryCounter + " previousmessagesLeft at " + previousMessagesLeft);

                    Message m = dataSnapshot.getValue(Message.class);
                    messagesList.push(m);

                    if (atLeast20 && queryCounter == LOAD_MORE) {
                        previousMessagesAvail = previousMessagesLeft;
                        Log.d(TAG, "loaded more to list we have " + previousMessagesLeft + " messages left");
                        loadMoreQuery.removeEventListener(loadMoreListener);
                        loadMessagesTextView.setVisibility(View.VISIBLE);

                    } else if (!atLeast20 && queryCounter == previousMessagesAvail) {
                        previousMessagesAvail = previousMessagesLeft;
                        Log.d(TAG, "loaded more to list we have " + previousMessagesLeft + " messages left");
                        loadMoreQuery.removeEventListener(loadMoreListener);
                        loadMessagesTextView.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        loadFirstListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (s == null)
                    firstMessageReceived = dataSnapshot.getKey();
                queryCounter++;
                previousMessagesLeft--;
                Log.d(TAG, "LoadFirstListener adding to list " + dataSnapshot.getKey() + " counter is at " + queryCounter + " previousmessagesLeft at " + previousMessagesLeft);

                Message m = dataSnapshot.getValue(Message.class);
                addPreviousMessageView(m.user, m.message, m.type, m.color, queryCounter, true);
                lastMessage = dataSnapshot.getKey();
                if (atLeast20 && queryCounter == 20 || !atLeast20 && queryCounter == previousMessagesAvail) {
                    Log.d(TAG, "after initla load we have " + previousMessagesLeft + " messages left");
                    queryCounter = 0;
                    loadFirstQuery.removeEventListener(loadFirstListener);
                    //loadmore if there are more
                    if (previousMessagesLeft > 0) {
                        previousMessagesAvail = previousMessagesLeft;
                        if (previousMessagesLeft - 15 > 0) {
                            Log.d(TAG, "querying for 15 more messages");
                            atLeast20 = true;
                            loadMoreQuery = FirebaseDatabase.getInstance().getReference("messages").child(mChannel).orderByKey().limitToLast(LOAD_MORE).endAt(firstMessageReceived);
                            loadMoreQuery.addChildEventListener(loadMoreListener);
                        } else {
                            Log.d(TAG, "querying for the remaining messages");
                            atLeast20 = false;
                            loadMoreQuery = FirebaseDatabase.getInstance().getReference("messages").child(mChannel).orderByKey().limitToLast(previousMessagesLeft).endAt(firstMessageReceived);
                            loadMoreQuery.addChildEventListener(loadMoreListener);
                        }
                    }
                    mScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(View.FOCUS_DOWN);

                            progressLinearLayout.setVisibility(View.GONE);
                            mScrollView.setVisibility(View.VISIBLE);

                        }
                    });
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messaging, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mSendButton = (Button) view.findViewById(R.id.sendMessage_Button);
        mSendButton.setVisibility(View.INVISIBLE);
        mScrollView = (ScrollView)view.findViewById(R.id.message_ScrollView);
        mLinearLayout = view.findViewById(R.id.message_LinearLayout);
        mLayoutChatbox = view.findViewById(R.id.layout_chatbox);
        progressLinearLayout = view.findViewById(R.id.progress_LinearLayout);
        loadMessagesTextView = view.findViewById(R.id.loadMessagesTextView);

        mEditText = createEditTextWithContentMimeTypes(
                new String[]{"image/png", "image/gif", "image/jpeg"}); //TODO: Do we need web to or does it break some function
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20,0,20,0);
        mEditText.setBackgroundColor(getResources().getColor(R.color.transparent));
        mEditText.setGravity(Gravity.LEFT);
        mEditText.setMaxLines(5);

        params.weight = 1;
        mLayoutChatbox.addView(mEditText, 0, params);

        toolbar = (Toolbar) view.findViewById(R.id.messagesToolbar);
        toolbar.setTitle(mChannel);

        toolbar.setEnabled(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(mEditText.getText().toString(), Util.MESSAGE_TEXT);
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

        messagesFirst = 0;







        loadLiveListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(lastMessage)) {
                } else {
                    Message message = dataSnapshot.getValue(Message.class);
                    addLiveMessageView(message.user, message.message, message.type, message.color);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        loadMessagesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!messagesList.isEmpty()) {
                    Log.d(TAG, "onClick:  " + messagesList.size());
                    int count = messagesList.size();

                    for (int i = 0; i < count; i++) {
                        Message m = messagesList.pollFirst();
                        addPreviousMessageView(m.user, m.message, m.type, m.color, 1, true);
                    }
                    loadMessagesTextView.setVisibility(View.GONE);
                    Log.d(TAG, "preiousMessagesLeft is: " + previousMessagesLeft);
                    if (previousMessagesLeft > 1) {
                        if (previousMessagesLeft - 15 > 0) {
                            Log.d(TAG, "querying for 15 more messages");
                            atLeast20 = true;
                            loadMoreQuery = FirebaseDatabase.getInstance().getReference("messages").child(mChannel).orderByKey().limitToLast(LOAD_MORE).endAt(firstMessageReceived);
                            loadMoreQuery.addChildEventListener(loadMoreListener);
                        } else {
                            Log.d(TAG, "querying for the remaining messages");
                            atLeast20 = false;
                            loadMoreQuery = FirebaseDatabase.getInstance().getReference("messages").child(mChannel).orderByKey().limitToLast(previousMessagesLeft).endAt(firstMessageReceived);
                            loadMoreQuery.addChildEventListener(loadMoreListener);
                        }
                    }



                }
            }
        });
    }


    public void loadInitialMessages(int messagesInChannel) {
        if(messagesInChannel > 20) {
            Log.d(TAG, "initial load is at least 20");
            loadFirstQuery = FirebaseDatabase.getInstance().getReference("messages").child(mChannel).limitToLast(20);
            loadFirstQuery.addChildEventListener(loadFirstListener);

        } else if(messagesInChannel <= 20) {
            Log.d(TAG, "initial load is less than 20");
            loadFirstQuery = FirebaseDatabase.getInstance().getReference("messages").child(mChannel).limitToLast(messagesInChannel);
            loadFirstQuery.addChildEventListener(loadFirstListener);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_messages, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.message_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(String message, String type) {
        if(mUser == null) {
            String name = Util.getAnonString();
            UserControl.get(getActivity()).setUsername(name);
            mUser = name;
        }

        if(type.equals(MESSAGE_MULTIMEDIA)) {
            FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(Util.FCM_PROJECT_SENDER_ID + FCM_SERVER_CONNECTION)
                    .setMessageId(Integer.toString(RANDOM.nextInt()))
                    .addData(PAYLOAD_ATTRIBUTE_RECIPIENT, "/topics/" + mChannel)
                    .addData("user", mUser)
                    .addData("message", message)
                    .addData("color", String.valueOf(mUserColor))
                    .addData("action", BACKEND_ACTION_TOPIC_MESSAGE)
                    .addData(Util.TOPIC_MESSAGE_TYPE, Util.MESSAGE_MULTIMEDIA)
                    .build());

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference =  firebaseDatabase.getReference("messages").child(mChannel).push();
            databaseReference.setValue(new Message(message, mUser, String.valueOf(mUserColor), Util.MESSAGE_MULTIMEDIA, System.currentTimeMillis()));
        }
        else if(type.equals(MESSAGE_TEXT)) {
            mEditText.setText("");
            FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(Util.FCM_PROJECT_SENDER_ID + FCM_SERVER_CONNECTION)
                    .setMessageId(Integer.toString(RANDOM.nextInt()))
                    .addData(PAYLOAD_ATTRIBUTE_RECIPIENT, "/topics/" + mChannel)
                    .addData("user", mUser)
                    .addData("message", message)
                    .addData("color", String.valueOf(mUserColor))
                    .addData("action", BACKEND_ACTION_TOPIC_MESSAGE)
                    .addData(Util.TOPIC_MESSAGE_TYPE, Util.MESSAGE_TEXT)
                    .build());

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference =  firebaseDatabase.getReference("messages").child(mChannel).push();
            databaseReference.setValue(new Message(message, mUser, String.valueOf(mUserColor), Util.MESSAGE_TEXT,  System.currentTimeMillis()));
        }

//        firebaseDatabase.child("m1").child("name").setValue(mUser);
//        firebaseDatabase.getReference("messages").child(mChannel).child("m1").child("message").setValue(message);
//        firebaseDatabase.getReference("messages").child(mChannel).child("m1").child("timestamp").setValue(System.currentTimeMillis());

    }

    public void messageReceived(final String user, final String message, final String color, final String type) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                addPreviousMessageView(user, message, type, color);
//            }
//        });
    }

    private void addPreviousMessageView(String user, String message, String type, String color, int viewIndex, boolean focusTop) {
        final Spannable username = new SpannableString(user);
        //final Spanned spannedString = fromHtml(username);
        username.setSpan(new ForegroundColorSpan(Integer.parseInt(color))
                , 0, user.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        username.setSpan(new StyleSpan(Typeface.BOLD),0,user.length(), 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,2,0,2);

        LinearLayout linearLayout;
        TextView textView;
        if(user.equals(mUser)) {
            if(type.equals(Util.MESSAGE_MULTIMEDIA) ) {

                //TODO:
//                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                ViewGroup parent = mLinearLayout;
//                inflater.inflate(R.layout.item_media_message_user, parent);

                if(getContext() != null) {
                    linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_media_message_user, null);
                    textView = linearLayout.findViewById(R.id.username_TextView);
                    textView.setText(username);

                    linearLayout.setLayoutParams(params);
                    mLinearLayout.addView(linearLayout,viewIndex);
                    ImageView imageView = linearLayout.findViewById(R.id.message_ImageView);

                    Glide.with(getActivity())
                            .load(message)
                            .into(imageView);


                }

            }
            else if(type.equals(Util.MESSAGE_TEXT)) {

                if(getContext() != null) {
                    linearLayout = (LinearLayout) getLayoutInflater().inflate( R.layout.item_text_message_user, null);
                    textView = linearLayout.findViewById(R.id.username_TextView);
                    textView.setText(username);

                    TextView textView1 = linearLayout.findViewById(R.id.message_TextView);
                    textView1.setText(message);

                    linearLayout.setLayoutParams(params);
                    mLinearLayout.addView(linearLayout, viewIndex);

                }

            }
            else
                return;

            params.gravity = Gravity.END;
        } else {
            if(type.equals(Util.MESSAGE_MULTIMEDIA) ) {

                if(getContext() != null) {
                    linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_media_message_friend, null);
                    textView = linearLayout.findViewById(R.id.username_TextView);
                    textView.setText(username);

                    ImageView imageView = linearLayout.findViewById(R.id.message_ImageView);

                    Glide.with(getActivity())
                            .load(message)
                            .into(imageView);


                    linearLayout.setLayoutParams(params);
                    mLinearLayout.addView(linearLayout,viewIndex);

                }

            }
            else if(type.equals(Util.MESSAGE_TEXT)) {
                if(getContext() != null) {
                    linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_text_message_friend, null);
                    textView = linearLayout.findViewById(R.id.username_TextView);
                    textView.setText(username);

                    TextView textView1 = linearLayout.findViewById(R.id.message_TextView);
                    textView1.setText(message);


                    linearLayout.setLayoutParams(params);
                    mLinearLayout.addView(linearLayout, viewIndex);

                }

            }
            else
                return;
        }

        if(focusTop) {
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    //mScrollView.fullScroll(View.FOCUS_UP);
                }
            });

        }
    }

    private void addLiveMessageView(String user, String message, String type, String color) {
        if(progressLinearLayout.getVisibility() == View.VISIBLE) {
            loadMessagesTextView.setVisibility(View.GONE);
        }
        final Spannable username = new SpannableString(user);
        //final Spanned spannedString = fromHtml(username);
        username.setSpan(new ForegroundColorSpan(Integer.parseInt(color))
                , 0, user.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        username.setSpan(new StyleSpan(Typeface.BOLD),0,user.length(), 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,2,0,2);

        LinearLayout linearLayout;
        TextView textView;
        if(user.equals(mUser)) {
            if(type.equals(Util.MESSAGE_MULTIMEDIA) ) {

                //TODO:
//                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                ViewGroup parent = mLinearLayout;
//                inflater.inflate(R.layout.item_media_message_user, parent);

                if(getContext() != null) {
                    linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_media_message_user, null);
                    textView = linearLayout.findViewById(R.id.username_TextView);
                    textView.setText(username);

                    linearLayout.setLayoutParams(params);
                    mLinearLayout.addView(linearLayout);
                    ImageView imageView = linearLayout.findViewById(R.id.message_ImageView);

                    Glide.with(getActivity())
                            .load(message)
                            .into(imageView);

                    mScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });


                }

            }
            else if(type.equals(Util.MESSAGE_TEXT)) {

                if(getContext() != null) {
                    linearLayout = (LinearLayout) getLayoutInflater().inflate( R.layout.item_text_message_user, null);
                    textView = linearLayout.findViewById(R.id.username_TextView);
                    textView.setText(username);

                    TextView textView1 = linearLayout.findViewById(R.id.message_TextView);
                    textView1.setText(message);

                    linearLayout.setLayoutParams(params);
                    mLinearLayout.addView(linearLayout);

                    mScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }

            }
            else
                return;

            params.gravity = Gravity.END;
        } else {
            if(type.equals(Util.MESSAGE_MULTIMEDIA) ) {

                if(getContext() != null) {
                    linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_media_message_friend, null);
                    textView = linearLayout.findViewById(R.id.username_TextView);
                    textView.setText(username);

                    ImageView imageView = linearLayout.findViewById(R.id.message_ImageView);

                    Glide.with(getActivity())
                            .load(message)
                            .into(imageView);


                    linearLayout.setLayoutParams(params);
                    mLinearLayout.addView(linearLayout);

                    mScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }

            }
            else if(type.equals(Util.MESSAGE_TEXT)) {
                if(getContext() != null) {
                    linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_text_message_friend, null);
                    textView = linearLayout.findViewById(R.id.username_TextView);
                    textView.setText(username);

                    TextView textView1 = linearLayout.findViewById(R.id.message_TextView);
                    textView1.setText(message);


                    linearLayout.setLayoutParams(params);
                    mLinearLayout.addView(linearLayout);

                    mScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }

            }
            else
                return;
        }

        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });


    }

    private boolean onCommitContentInternal(InputContentInfoCompat inputContentInfo, int flags) {
        if ((flags & InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
            try {
                inputContentInfo.requestPermission();

            } catch (Exception e) {
                Log.e(TAG, "InputContentInfoCompat#requestPermission() failed.", e);
                return false;
            }
        }

        if(inputContentInfo.getLinkUri() == null) {
            Toast.makeText(getActivity(), "This text field does not support that media", Toast.LENGTH_SHORT).show();
        }
        else
            sendMessage(inputContentInfo.getLinkUri().toString(), Util.MESSAGE_MULTIMEDIA);


        return true;
    }

    public boolean onCommitContent(InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {
        return onCommitContentInternal(inputContentInfo, flags);
    }

    private EditText createEditTextWithContentMimeTypes(String[] contentMimeTypes) {
        final String[] mimeTypes;  // our own copy of contentMimeTypes.
        mimeTypes = Arrays.copyOf(contentMimeTypes, contentMimeTypes.length);

        EditText exitText = new EditText(getActivity()) {
            @Override
            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                final InputConnection ic = super.onCreateInputConnection(editorInfo);
                EditorInfoCompat.setContentMimeTypes(editorInfo, mimeTypes);
                final InputConnectionCompat.OnCommitContentListener callback =
                        new InputConnectionCompat.OnCommitContentListener() {
                            @Override
                            public boolean onCommitContent(InputContentInfoCompat inputContentInfo,
                                                           int flags, Bundle opts) {
                                return MessagingFragment.this.onCommitContent(
                                        inputContentInfo, flags, opts);
                            }
                        };
                return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
            }
        };
        exitText.setHint(getResources().getString(R.string.message_hint));

        exitText.setGravity(1);
//        exitText.setHeight(mLayoutChatbox.getHeight());


        exitText.setTextColor(getResources().getColor(R.color.black));
//        params.setMargins(16,0,16,0);
//        mEditText.setLayoutParams(params);
        return exitText;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUser = mSharedPreferences.getString(getString(R.string.preference_username),
                UserControl.get(getActivity()).getUserName());

        if(mChannel == null) {
            SharedPreferences sharedPref = getActivity()
                    .getSharedPreferences(getString(R.string.preference_file_key)
                            ,Context.MODE_PRIVATE);

            mChannel = sharedPref.getString(getString(R.string.preference_channel), null);
        }

        mUserColor = Color.parseColor(mSharedPreferences
                .getString(getString(R.string.preference_user_color), UserControl.get(getActivity())
                        .getUserColor()));

        Intent intent  = new Intent(getActivity(), ChannelAddUserService.class);
        intent.putExtra(ChannelAddUserService.EXTRA_CHANNEL, mChannel);
        getActivity().startService(intent);
        FirebaseMessaging.getInstance().subscribeToTopic( mChannel);



    }

    @Override
    public void onPause() {
        Intent intent  = new Intent(getActivity(), ChannelRemoveUserService.class);
        intent.putExtra(ChannelRemoveUserService.EXTRA_CHANNEL, UserControl.get(getActivity()).getmChannelName());
        getActivity().startService(intent);

        FirebaseMessaging.getInstance().unsubscribeFromTopic(mChannel);
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
