package com.bmessenger.bmessenger;

import android.support.v4.app.Fragment;

/**
 * Created by uli on 11/14/2016.
 */

public class ChannelListActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new ChannelListFragment();
    }
}
