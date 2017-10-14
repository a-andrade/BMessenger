package com.longbeachsocial.app.Activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.longbeachsocial.app.Fragments.SettingsFragment;

/**
 * Created by One on 9/29/2017.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,new SettingsFragment()).commit();
    }
}