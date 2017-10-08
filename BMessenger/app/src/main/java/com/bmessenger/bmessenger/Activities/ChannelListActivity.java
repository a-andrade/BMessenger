package com.bmessenger.bmessenger.Activities;

import android.support.v4.app.Fragment;

import com.bmessenger.bmessenger.Fragments.ChannelListFragment;

/**
 * Created by uli on 11/14/2016.
 */

public class ChannelListActivity extends SingleFragmentActivity {


    private final String TAG = getClass().getSimpleName();

    @Override
    public Fragment createFragment() {

        return new ChannelListFragment();
    }

//    //TODO: understand this
//    @Override
//    public void onLocationChanged(Location location) {
//        Log.d(TAG, "onLocationChanged extra methdos?");
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
