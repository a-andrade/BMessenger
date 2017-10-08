package com.bmessenger.bmessenger.Activities;

import android.support.v4.app.Fragment;

import com.bmessenger.bmessenger.Fragments.MessagingFragment;

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

//        Log.d(TAG, "onDestroy");
//        MessageControl appManager = MessageControl.get(getApplicationContext());
//        appManager.removeCallback();
    }


//    @Override
//    public void onLocationChanged(Location location) {
//        Log.d(TAG, "onLocationChanged CCCCCCCCHANGED");
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
