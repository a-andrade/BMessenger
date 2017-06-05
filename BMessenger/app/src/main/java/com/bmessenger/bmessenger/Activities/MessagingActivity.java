package com.bmessenger.bmessenger.Activities;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.bmessenger.bmessenger.Fragments.MessagingFragment;
import com.bmessenger.bmessenger.Manager.MessageControl;

/**
 * Created by uli on 12/5/2016.
 */

public class MessagingActivity  extends SingleFragmentActivity {


    private final String TAG = "MessagingActivity";

    @Override
    public Fragment createFragment() {
        return new MessagingFragment();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
        MessageControl leagueManager = MessageControl.get(getApplicationContext());
        leagueManager.removeCallback();
    }
}
