package com.bmessenger.bmessenger.Fragments;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.location.Location;
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
import com.bmessenger.bmessenger.Services.LocationProvider;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;


/**
 * Created by uli on 11/14/2016.
 */

public class ChannelListFragment extends Fragment{

    private static String TAG = "ChannelListFragment";

    ArrayList<ChannelItem> listChannelItems = new ArrayList<>();

    private ChannelItem add_channel;
    private String scroll_to;

    Toolbar toolbar;
    RecyclerView recyclerView;


//    private GoogleApiClient mGoogleApiClient;
//
//    private LocationRequest mLocationRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChannelItem item = new ChannelItem("General", "general room for all");
        listChannelItems. add(item);
        listChannelItems. add(new ChannelItem("CECS 491", "join us for 491 content"));
        listChannelItems. add(new ChannelItem("CSULB", "talk with other students about campus"));
        listChannelItems. add(new ChannelItem("BM Dev", "talk with the developers of BM"));
        listChannelItems. add(new ChannelItem("News", "for all your CSULB news"));
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


        final ChannelAdapter adapter = new ChannelAdapter(getContext(), listChannelItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final String token = FirebaseInstanceId.getInstance().getToken();
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/WireOne.ttf");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menuAdd:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                        input2.getText().toString());
                                if(!listChannelItems.contains(add_channel)) {
                                    listChannelItems.add(add_channel);
                                    //add toast if needed
                                    adapter.notifyItemInserted(listChannelItems.size() - 1);
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
}
