package com.bmessenger.bmessenger.Activities;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.bmessenger.bmessenger.Fragments.ChannelListFragment;
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

}
