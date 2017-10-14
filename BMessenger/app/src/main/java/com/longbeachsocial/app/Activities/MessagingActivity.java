package com.longbeachsocial.app.Activities;

import android.support.v4.app.Fragment;

import com.longbeachsocial.app.Fragments.MessagingFragment;

/**
 * Created by uli on 12/5/2016.
 */

public class MessagingActivity  extends SingleFragmentActivity {


    @Override
    public Fragment createFragment() {
        return new MessagingFragment();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }



}
