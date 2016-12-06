package com.bmessenger.bmessenger;

import android.support.v4.app.Fragment;

/**
 * Created by uli on 12/5/2016.
 */

public class MessagingActivity  extends SingleFragmentActivity{

    @Override
    public Fragment createFragment() {
        return new MessagingFragment();
    }
}
