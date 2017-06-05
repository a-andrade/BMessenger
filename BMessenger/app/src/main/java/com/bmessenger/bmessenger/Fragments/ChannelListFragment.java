package com.bmessenger.bmessenger.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


import com.bmessenger.bmessenger.Adapters.ChannelAdapter;
import com.bmessenger.bmessenger.Models.ChannelItem;
import com.bmessenger.bmessenger.R;
import com.bmessenger.bmessenger.Services.RegisterChannelService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;


/**
 * Created by uli on 11/14/2016.
 */

public class ChannelListFragment extends Fragment{
    //TODO: add spinner until channels show

    private static String TAG = "ChannelListFragment";

    ArrayList<ChannelItem> listChannelItems = new ArrayList<>();

    private ChannelItem add_channel;
    private String scroll_to;
    Toolbar toolbar;
    RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ChannelAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference("channels");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded " + dataSnapshot.getValue(Chanel.class).toString());
                listChannelItems.add(new ChannelItem(dataSnapshot.getKey(), dataSnapshot.getValue(Chanel.class).getSummary(), dataSnapshot.getValue(Chanel.class).getUsers()));
                adapter.notifyItemInserted(listChannelItems.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged");
                listChannelItems.get( listChannelItems.indexOf(new ChannelItem(dataSnapshot.getKey(), "", 0))).setUsers(dataSnapshot.getValue(Chanel.class).getUsers());
                adapter.notifyItemChanged(listChannelItems.indexOf(new ChannelItem(dataSnapshot.getKey(), "", 0)));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ChannelItem channelItem = new ChannelItem(dataSnapshot.getKey(), "", 0);
                int index = listChannelItems.indexOf(channelItem);
                listChannelItems.remove(index);
                adapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_channel_list, container, false);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        recyclerView  = (RecyclerView) v.findViewById(R.id.channelRecycleView);
        toolbar.setTitle("Channels");
        toolbar.inflateMenu(R.menu.menu_main);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        adapter = new ChannelAdapter(getContext(), listChannelItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//
//        final String token = FirebaseInstanceId.getInstance().getToken();
//        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/WireOne.ttf");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menuAdd:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        //TODO: fix diaglog UI
                        builder.setTitle("Add Channel");
                        // I'm using fragment here so I'm using getView() to provide ViewGroup
                        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_channnel_create, (ViewGroup) getView(), false);
                        // Set up the input
                        final EditText input = (EditText) viewInflated.findViewById(R.id.dialog_editText);

                        final EditText input2 = (EditText) viewInflated.findViewById(R.id.dialog_summary);
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        builder.setView(viewInflated);

                        // Set up the buttons
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                add_channel = new ChannelItem(
                                        input.getText().toString(),
                                        input2.getText().toString(),
                                        0);
                                if(!listChannelItems.contains(add_channel)) {
                                    Intent intent  = new Intent(getActivity(), RegisterChannelService.class);
                                    intent.putExtra(RegisterChannelService.EXTRA_CHANNEL, input.getText().toString());
                                    intent.putExtra(RegisterChannelService.EXTRA_SUMMARY, input2.getText().toString());
                                    getActivity().startService(intent);
                                    //add toast if needed
                                }
                                else {
                                    Toast.makeText(getContext(), "Channel already exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();

                        return true;

                    case R.id.menu_search:
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                        builder1.setTitle("Search");
                        // I'm using fragment here so I'm using getView() to provide ViewGroup
                        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                        View viewInflated1 = LayoutInflater.from(getContext()).inflate(R.layout.dialog_simple, (ViewGroup) getView(), false);
                        // Set up the input
                        final EditText input1 = (EditText) viewInflated1.findViewById(R.id.dialog_editText);

                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        builder1.setView(viewInflated1);

                        // Set up the buttons
                        builder1.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                scroll_to = input1.getText().toString();

                                if(listChannelItems.contains(scroll_to)) {
                                    //listView.smoothScrollToPosition(listItems.indexOf(scroll_to));
                                    recyclerView.scrollToPosition(listChannelItems.indexOf(scroll_to));
                                }
                                else {
                                    Toast toast = Toast.makeText(getContext(),"That channel does not exist", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }

                            }
                        });
                        builder1.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder1.show();

                        return true;

                }
                return false;
            }
        });

        return v;
    }

    public static class Chanel {
        //TODO: Fix this
        public int users;
        public String summary;

        @Override
        public String toString() {
            return users + " " + summary;
        }

        public int getUsers() {
            return users;
        }

        public String getSummary() {
            return summary;
        }
    }
}
