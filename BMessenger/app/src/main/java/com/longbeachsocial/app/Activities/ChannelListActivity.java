package com.longbeachsocial.app.Activities;

import android.support.v4.app.Fragment;

import com.longbeachsocial.app.Fragments.ChannelListFragment;

/**
 * Created by uli on 11/14/2016.
 */

public class ChannelListActivity extends SingleFragmentActivity {


    private final String TAG = getClass().getSimpleName();

    @Override
    public Fragment createFragment() {

        return new ChannelListFragment();
    }

}
