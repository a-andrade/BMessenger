package com.bmessenger.bmessenger.Activities;

import android.support.v4.app.Fragment;

import com.bmessenger.bmessenger.Fragments.ChannelListFragment;
import com.bmessenger.bmessenger.Services.LocationProvider;

/**
 * Created by uli on 11/14/2016.
 */

public class ChannelListActivity extends SingleFragmentActivity implements LocationProvider.LocationCallback {

    @Override
    public Fragment createFragment() {
        return new ChannelListFragment();
    }
}
