package com.bmessenger.bmessenger.Activities;

import android.content.Context;
import android.location.GnssStatus;
import android.location.GpsStatus;
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


/**
 * Created by uli on 11/14/2016.
 */


public abstract class SingleFragmentActivity extends AppCompatActivity implements LocationProvider.LocationCallback, LocationListener {
    private final String TAG = getClass().getName().toString();
    private LocationProvider mService;
    protected abstract Fragment createFragment();

    //used to return the fragment you want to place into container

    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
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


        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    //Log.d(TAG, "onLocationChanged " + location.getLatitude() + " " + location.getLongitude());
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                    Log.d(TAG, "onProviderEnabled");
                    loadAvailableFrag();
                }

                public void onProviderDisabled(String provider) {
                    Log.d(TAG, "onProviderDisabled");
                    loadUnavailableFrag();
                }
            };

        // Register the listener with the Location Manager to receive location updates
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            loadUnavailableFrag();
        }
    }


    @Override
    public void handleNewLocation(Location location) {
        //Log.d(TAG, location.toString());
        //Toast.makeText(getBaseContext(), "Long: " + location.getLongitude() + " Lat: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
//        if(location.getLatitude() > 33.783877 && location.getLatitude() < 33.785
//                && location.getLongitude() < -117.856743 && location.getLongitude() > -117.8569) {
        if(true) {
            loadAvailableFrag();


        }
        else {
            loadUnavailableFrag();

        }
    }

    public void loadAvailableFrag() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentByTag("available")  == null) {

            Fragment dialog = fragmentManager.findFragmentByTag("unavailable");
            if (dialog != null) {
                DialogFragment df = (DialogFragment) dialog;
                df.dismiss();
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

            fm.beginTransaction().add(alertDialog, "unavailable").commit();
            //alertDialog.show(fm, "unavailable");
        }
        else {
            Log.d(TAG, "fragmanager found that tagged frag");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

    }


    @Override
    public void onResume() {
        super.onResume();
        mService.connect();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        mService.disconnect();
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
