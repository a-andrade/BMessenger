package com.bmessenger.bmessenger.Activities;

import android.support.v4.app.Fragment;

import com.bmessenger.bmessenger.Fragments.LoginFragment;

/**
 * Created by uli on 11/14/2016.
 */

public class ChannelActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new LoginFragment();
    }
}
