package com.bmessenger.bmessenger.Activities;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.bmessenger.bmessenger.Fragments.LoginFragment;
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


    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }


}
