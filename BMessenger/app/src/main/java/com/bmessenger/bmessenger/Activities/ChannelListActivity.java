package com.bmessenger.bmessenger.Activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.bmessenger.bmessenger.Fragments.ChannelListFragment;
import com.bmessenger.bmessenger.Fragments.DisabledFragment;
import com.bmessenger.bmessenger.Services.LocationProvider;
import com.google.android.gms.maps.model.LatLng;
import com.bmessenger.bmessenger.R;

/**
 * Created by uli on 11/14/2016.
 */

public class ChannelListActivity extends AppCompatActivity implements LocationProvider.LocationCallback {
    private final String TAG = "ChannelListActivity";

    private LocationProvider locationProvider;

    private LocationProvider mService;
    private boolean inEnv = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        Log.d(TAG, " on Create");
        mService = new LocationProvider(getApplicationContext(), this);

    }

    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        //Toast.makeText(getContext(), location.toString(), Toast.LENGTH_LONG).show();
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        if(location.getLatitude() > 33.783387 && location.getLatitude() < 33.784111
                && location.getLongitude() < -117.856675 && location.getLongitude() > -117.856807) {
            if(!inEnv) {
                inEnv = true;
                Toast.makeText(getApplicationContext(), "entered location", Toast.LENGTH_SHORT).show();
                ChannelListFragment fragment = new ChannelListFragment();
                loadFragment(fragment, "available");


                Log.d(TAG, "load normal fragment here");
                //Load normal fragment
            }
        }
        else {
            if(inEnv) {
                inEnv = false;
                Log.d(TAG, "load service unavail here");
                Toast.makeText(getApplicationContext(), "left location", Toast.LENGTH_SHORT).show();
                //Load service unavailable fragment
                DisabledFragment fragment = new DisabledFragment();
                loadFragment(fragment, "unavailable");
            }
        }
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragmentContainer, fragment, tag);
        fragmentTransaction.commit();
    }



    @Override
    public void onResume() {
        super.onResume();
        mService.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mService.disconnect();
    }
}
