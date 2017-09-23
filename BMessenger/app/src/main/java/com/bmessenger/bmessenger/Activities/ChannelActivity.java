package com.bmessenger.bmessenger.Activities;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.bmessenger.bmessenger.Fragments.LoginFragment;

/**
 * Created by uli on 11/14/2016.
 */

public class ChannelActivity extends SingleFragmentActivity{

    private final String TAG = getClass().getSimpleName();

    @Override
    public Fragment createFragment() {
        return new LoginFragment();
    }

//
//    @Override
//    public void onLocationChanged(Location location) {
//    }
//
//    @Override
//    public void onStatusChanged(String s, int i, Bundle bundle) {
//    }
//
//    @Override
//    public void onProviderEnabled(String s) {
//    }
//
//    @Override
//    public void onProviderDisabled(String s) {
//    }

}
