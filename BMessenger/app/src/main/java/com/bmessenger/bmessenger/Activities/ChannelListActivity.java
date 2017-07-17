package com.bmessenger.bmessenger.Activities;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.bmessenger.bmessenger.Fragments.ChannelListFragment;
import com.bmessenger.bmessenger.Fragments.LoginFragment;
import com.bmessenger.bmessenger.Services.LocationProvider;

/**
 * Created by uli on 11/14/2016.
 */

public class ChannelListActivity extends SingleFragmentActivity {


    private final String TAG = "ChannelListActivity";

    @Override
    public Fragment createFragment() {

        return new ChannelListFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

    }

    //TODO: understand this
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }



}
