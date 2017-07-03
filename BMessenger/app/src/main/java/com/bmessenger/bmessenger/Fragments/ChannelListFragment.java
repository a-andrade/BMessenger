package com.bmessenger.bmessenger.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.bmessenger.bmessenger.Adapters.ChannelAdapter;
import com.bmessenger.bmessenger.Models.ChannelItem;
import com.bmessenger.bmessenger.R;
import com.bmessenger.bmessenger.Services.RegisterChannelService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import java.util.ArrayList;
import java.util.regex.*;



/**
 * Created by uli on 11/14/2016.
 */

public class ChannelListFragment extends Fragment{
    //TODO: Fix username dialog
    private static String TAG = "ChannelListFragment";

    ArrayList<ChannelItem> basicChannelList = new ArrayList<>();

    private ChannelItem add_channel;
    Toolbar toolbar;
    RecyclerView recyclerView;
    private Query basicChannelQuery;
    private ChannelAdapter adapter;
    private String recentlyAddedChannel;
    private boolean dialogInput1 = false;
    private boolean dialogInput2 = false;
    private FloatingActionButton floatingActionButton;
    private ProgressBar progressBar;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(TAG, String.valueOf(hasOptionsMenu()));
        basicChannelQuery = FirebaseDatabase.getInstance().getReference("channels").orderByChild("users");

        //TODO: Change channel order only if refresh all of recycleview
        basicChannelQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded " + dataSnapshot.getValue(Chanel.class).toString());
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.VISIBLE);
                toolbar.setEnabled(true);
                if(dataSnapshot.getValue(Chanel.class).getUsers() == 0) {
                    basicChannelList.add(new ChannelItem(dataSnapshot.getKey(), dataSnapshot.getValue(Chanel.class).getSummary(), dataSnapshot.getValue(Chanel.class).getUsers()));
                    adapter.notifyItemInserted(basicChannelList.size() - 1);

                    if(recentlyAddedChannel != null) {
                        recyclerView.scrollToPosition(basicChannelList.size() -1);
                        recentlyAddedChannel = null;
                    }


                }
                else if(dataSnapshot.getKey().toString().equalsIgnoreCase("General")) {
                    basicChannelList.add(0, new ChannelItem(dataSnapshot.getKey(), dataSnapshot.getValue(Chanel.class).getSummary(), dataSnapshot.getValue(Chanel.class).getUsers()));
                    adapter.notifyItemInserted(0);
                }
                else {

                    basicChannelList.add(1, new ChannelItem(dataSnapshot.getKey(), dataSnapshot.getValue(Chanel.class).getSummary(), dataSnapshot.getValue(Chanel.class).getUsers()));
                    adapter.notifyItemInserted(1);

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged");
                basicChannelList.get( basicChannelList.indexOf(new ChannelItem(dataSnapshot.getKey(), "", 0))).setUsers(dataSnapshot.getValue(Chanel.class).getUsers());
                adapter.notifyItemChanged(basicChannelList.indexOf(new ChannelItem(dataSnapshot.getKey(), "", 0)));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ChannelItem channelItem = new ChannelItem(dataSnapshot.getKey(), "", 0);
                int index = basicChannelList.indexOf(channelItem);
                basicChannelList.remove(index);
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

        recyclerView  = (RecyclerView) v.findViewById(R.id.channelRecycleView);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
//        toolbar.setTitle("Channels");
        toolbar.setEnabled(false);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //toolbar.inflateMenu(R.menu.menu_main);

        floatingActionButton = (FloatingActionButton) v.findViewById(R.id.channels_floatingButton);
        progressBar = (ProgressBar) v.findViewById(R.id.channelList_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setIndeterminate(true);



        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        //stickyChannelAdapter = new ChannelAdapter(getContext(), stickyChannelList);
        //stickyChannelRecyclerView.setAdapter(stickyChannelAdapter);
        //stickyChannelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChannelAdapter(getContext(), basicChannelList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/WireOne.ttf");



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                //TODO: fix channel add diaglog UI
                builder.setTitle(R.string.channel_dialog_notice);
                // I'm using fragment here so I'm using getView() to provide ViewGroup
                // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_channnel_create, (ViewGroup) getView(), false);
                // Set up the input
                final EditText input = (EditText) viewInflated.findViewById(R.id.dialog_editText);
                final EditText input2 = (EditText) viewInflated.findViewById(R.id.dialog_summary);

                input.setHint(R.string.channel);
                input2.setHint(R.string.summary);


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
                        if(!basicChannelList.contains(add_channel)) {
                            Intent intent  = new Intent(getActivity(), RegisterChannelService.class);
                            if(input.getText().length() < 2) { //channel name must be longer than 2 letters
                                intent.putExtra(RegisterChannelService.EXTRA_CHANNEL, "filler channel");
                            }
                            if(input2.getText().length() < 1) {
                                intent.putExtra(RegisterChannelService.EXTRA_SUMMARY, "");
                            }
                            recentlyAddedChannel = input.getText().toString();
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
                final AlertDialog alertDialog = builder.create();
//                        ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
//                                .setEnabled(false);


                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d(TAG, s.toString() + " " + s.length());
                        if(!Pattern.matches( "^[a-z0-9_-]{3,16}$", s.toString())) {
                            input.setError("Channel name must be one word of 3-15 alphanumeric characters");
                            dialogInput1 = false;
                        }
                        else
                            dialogInput1 = true;

                        if(dialogInput1 && dialogInput2) {
                            ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setEnabled(true);
                        }
                        else
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setEnabled(false);
                    }
                });

                input2.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d(TAG, s.toString() + " " + s.length());
                        if(s.length() < 4 || s.length() > 31) {
                            input2.setError("Channel summary must be 4-30 characters");
                            dialogInput2 = false;
                        }
                        else
                            dialogInput2 = true;

                        Log.d(TAG, "input1 is " + dialogInput1 + " iput2 is " + dialogInput2);

                        if(dialogInput2 && dialogInput1) {
                            ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setEnabled(true);
                        }
                        else {
                            Log.d(TAG, "Should set ok to false");
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setEnabled(false);
                        }
                    }
                });

                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

            }
        });

        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        //TODO: hide keyboard after search enter
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);

                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);
                    if(basicChannelList.contains(new ChannelItem(query, "", 0))) {
                        Log.d(TAG, "index i s" +  basicChannelList.indexOf(new ChannelItem(query, "", 0)));
                        recyclerView.scrollToPosition(basicChannelList.indexOf(new ChannelItem(query, "", 0)));
                        //TODO: Scroll to one off on end
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }
                    else {
                        Toast.makeText(getContext(), "That channel does not exists", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
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
