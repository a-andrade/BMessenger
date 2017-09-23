package com.bmessenger.bmessenger.Activities;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bmessenger.bmessenger.Fragments.DisabledDialog;
import com.bmessenger.bmessenger.Manager.MessageControl;
import com.bmessenger.bmessenger.R;
import com.bmessenger.bmessenger.Services.LocationProvider;
//import com.google.android.gms.location.LocationServices;
//import com.squareup.leakcanary.LeakCanary;


/**
 * Created by uli on 11/14/2016.
 */


public abstract class SingleFragmentActivity extends AppCompatActivity implements LocationProvider.LocationCallback{
    private final String TAG = getClass().getSimpleName().toString();
    private LocationProvider mService;
    protected abstract Fragment createFragment();
    protected LocationManager lm;
    protected LocationListener locationListener;

    //used to return the fragment you want to place into container

    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        //LeakCanary.install(getApplication());
        mService = new LocationProvider(getApplicationContext(), this);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if(fragment == null) {
            fragment = createFragment();
            //create fragment then add it to container below
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }


         lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d(TAG, "onLocationChanged from LocationListener " + location.getLatitude() + " " + location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled");
                if (savedInstanceState == null) {
                    Log.d(TAG, "savedInstanceState is null");
                    releaseUnavailableFrag();
                }
            }

            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled");
                loadUnavailableFrag();
            }
        };

//        try {
//
//
//            if (lm.getAllProviders().contains(LocationManager.GPS_PROVIDER))
//                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            loadUnavailableFrag();
        }
    }


    @Override
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        //Toast.makeText(getBaseContext(), "Long: " + location.getLongitude() + " Lat: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
        if(location.getLatitude() > 33.773743 && location.getLatitude() < 33.789134
                && location.getLongitude() > -118.124241 && location.getLongitude() < -118.106882) {
//        if(true) {
            releaseUnavailableFrag();
        }
        else {
            loadUnavailableFrag();
        }
    }

    public void releaseUnavailableFrag() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentByTag("available")  == null) {

            Fragment dialog = fragmentManager.findFragmentByTag("unavailable");
            if (dialog != null) {
                DialogFragment df = (DialogFragment) dialog;
                df.dismissAllowingStateLoss();
            }
        }
        else {
            Log.d(TAG, "fragmanager found that tagged frag");

        }
    }

    public void loadUnavailableFrag() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentByTag("unavailable")  == null) {
            Log.d(TAG, "load service unavail here");
            FragmentManager fm = getSupportFragmentManager();
            DisabledDialog alertDialog = new DisabledDialog();
            alertDialog.setCancelable(false);
            //Check if a a transaction is set already
            //check to see if we can dismiss previous transaction and just place the new one
            //if we stepped into after forefront callback then don't commit

            fm.beginTransaction().add(alertDialog, "unavailable").commitAllowingStateLoss();
            //alertDialog.show(fm, "unavailable");

            //alertDialog.show(fragmentManager, "unavailable");
        }
        else {
            Log.d(TAG, "fragmanager found that tagged frag");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mService.connect();
        try {


            if (lm.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        mService.disconnect();
        lm.removeUpdates(locationListener);

    }


    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        MessageControl leagueManager = MessageControl.get(getApplicationContext());
        leagueManager.removeCallback();

    }

}
