package com.bmessenger.bmessenger.Activities;

import android.support.v4.app.Fragment;

import com.bmessenger.bmessenger.Fragments.LoginFragment;

public class LoginActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new LoginFragment();
    }
}
