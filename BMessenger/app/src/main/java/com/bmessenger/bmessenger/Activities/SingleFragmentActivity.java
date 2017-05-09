package com.bmessenger.bmessenger.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.bmessenger.bmessenger.R;
import com.bmessenger.bmessenger.Services.LocationProvider;

/**
 * Created by uli on 11/14/2016.
 */


public abstract class SingleFragmentActivity extends AppCompatActivity {

    private LocationProvider locationProvider;

    private LocationProvider mService;
    private boolean inEnv = false;

    
    protected abstract Fragment createFragment();
    //used to return the fragment you want to place into container

    //use to be able to override later in subclass to return a twopane fragment
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
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
}
