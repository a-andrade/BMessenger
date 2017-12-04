package com.longbeachsocial.app.Activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.longbeachsocial.app.Fragments.DisabledDialog;
import com.longbeachsocial.app.R;
import com.longbeachsocial.app.Services.LocationProvider;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


/**
 * Created by uli on 11/14/2016.
 */


public abstract class SingleFragmentActivity extends AppCompatActivity{
    private final String TAG = getClass().getSimpleName().toString();
    //private LocationProvider mLocationProvider;
    protected LocationManager mLocationManager;
    protected LocationListener locationListener;
    private boolean updatesRequested = false;

    private static final int FINE_LOCATION_PERMISSIONS_REQUEST = 1;

    //used to return the fragment you want to place into container

    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }
    protected abstract Fragment createFragment();

    protected abstract Context getActivityContext();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        //mLocationProvider = new LocationProvider(getActivityContext(), this);


        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if(fragment == null) {
            fragment = createFragment();
            //create fragment then add it to container below
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }




        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d(TAG, "onLocationChanged from LocationListener " + location.getLatitude() + " " + location.getLongitude());
                //Toast.makeText(SingleFragmentActivity.this, "onLocationChanged from LocationListener " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
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
//            if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
//                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }


    }


//    @Override
//    public void handleNewLocation(Location location) {
//        Log.d("lbs.location", location.toString());
//        //Toast.makeText(getBaseContext(), "Long: " + location.getLongitude() + " Lat: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
////        if(location.getLatitude() > 33.773743 && location.getLatitude() < 33.789134
////                && location.getLongitude() > -118.124241 && location.getLongitude() < -118.106882) {
//        if(true) {
//            releaseUnavailableFrag();
//        }
//        else {
//            loadUnavailableFrag();
//        }
//    }

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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                            if(!updatesRequested) {
                                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                                updatesRequested = true;
                            }
                        }

                        releaseUnavailableFrag();

                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }

                } else {
                    loadUnavailableFrag();

                    // permission denied, Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        //mLocationProvider.connect();
        mLocationManager = (LocationManager) getSystemService(getActivityContext().LOCATION_SERVICE);


        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            releaseUnavailableFrag();
        }

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            loadUnavailableFrag();
        }

        try {

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(getActivityContext(), ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{ACCESS_COARSE_LOCATION},
                        FINE_LOCATION_PERMISSIONS_REQUEST);
            }
            else {
                if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                    if(!updatesRequested) {
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 20, locationListener);
                        updatesRequested = true;
                    }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        //mLocationProvider.disconnect();
        mLocationManager.removeUpdates(locationListener);
    }

    @Override
    public void onDestroy() {
        //mLocationProvider.onDestroy();
        //mLocationProvider = null;
        mLocationManager = null;
        locationListener = null;
        super.onDestroy();
    }

}
