package com.bmessenger.bmessenger.Activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.bmessenger.bmessenger.Fragments.DisabledDialog;
import com.bmessenger.bmessenger.Manager.MessageControl;
import com.bmessenger.bmessenger.R;
import com.bmessenger.bmessenger.Services.LocationProvider;


/**
 * Created by uli on 11/14/2016.
 */


public abstract class SingleFragmentActivity extends AppCompatActivity implements LocationProvider.LocationCallback {
    //TODO: make sure they cant just turn on locaiton settings and still work
    //TODO: Fix the location to work on Palmyra
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

    }


    @Override
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        Toast.makeText(getBaseContext(), "Long: " + location.getLongitude() + " Lat: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
//        if(location.getLatitude() > 33.783877 && location.getLatitude() < 33.785
//                && location.getLongitude() < -117.856743 && location.getLongitude() > -117.8569) {
        if(true) {

            Log.d(TAG, "should check if available frag is loaded");
            FragmentManager fragmentManager = getSupportFragmentManager();
            if(fragmentManager.findFragmentByTag("available")  == null) {
                //Toast.makeText(getApplicationContext(), "entered location", Toast.LENGTH_SHORT).show();
//                Fragment fragment = createFragment();
//                loadFragment(fragment, "available");
//                Log.d(TAG, "load normal fragment here");

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
        else {
            Log.d(TAG, "should check if unavilable frag is loaded");
            FragmentManager fragmentManager = getSupportFragmentManager();
            if(fragmentManager.findFragmentByTag("unavailable")  == null) {
               Log.d(TAG, "Fragmanager says frag with that tag is not here");
                Log.d(TAG, "load service unavail here");

                FragmentManager fm = getSupportFragmentManager();
                DisabledDialog alertDialog = new DisabledDialog();
                alertDialog.setCancelable(false);
                alertDialog.show(fm, "unavailable");

            }
            else {
                Log.d(TAG, "fragmanager found that tagged frag");

            }

        }
    }


//
//    public  boolean locationEnabled() {
//        LocationManager lm = (LocationManager)getApplication().getSystemService(Context.LOCATION_SERVICE);
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//            //TODO: fix this so it works
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            Log.d(TAG, gps_enabled + " gps enabled");
//        } catch(Exception ex) {}
//
//        return gps_enabled;
//
//
//    }
//
//    private void loadFragment(Fragment fragment, String tag) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        fragmentTransaction.add(R.id.fragmentContainer, fragment, tag);
//        fragmentTransaction.commit();
//    }

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
