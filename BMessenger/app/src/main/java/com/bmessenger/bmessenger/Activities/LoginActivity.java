package com.bmessenger.bmessenger.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bmessenger.bmessenger.Fragments.UsernameDialog;
import com.bmessenger.bmessenger.Manager.UserControl;
import com.bmessenger.bmessenger.R;
import com.bmessenger.bmessenger.Services.IRequestListener;
import com.bmessenger.bmessenger.Utilities.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity implements IRequestListener {
    //TODO: log usernames to keep them unique
    private final String TAG = "bmessenger.Login";
    private Button createUsernameButton;
    private Button getRandomUsernameButton;
    private boolean hasUsername = false;
    private SharedPreferences mSharedPreferences;

    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        createUsernameButton = (Button)findViewById(R.id.createUsernameButton);
        getRandomUsernameButton = (Button)findViewById(R.id.anonButton);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        createUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final UsernameDialog newDialog = new UsernameDialog();
                newDialog.show(getSupportFragmentManager(), "missiles");
            }
        });

        getRandomUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserControl.get(getApplicationContext()).setUsername(Util.getAnonString());
                startNextActivity();
            }
        });

        hasUsername = isUsernameSet();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if(hasUsername)
                        startNextActivity();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Log.d(TAG, "attempting to sign in ");
                    signInAnonymously();

                }
            }
        };

    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                        } else {
                            if(hasUsername)
                                startNextActivity();
                        }
                    }
                });
    }

    private void startNextActivity() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm aaa");
        String formattedString = simpleDateFormat.format(new Date());

        Bundle bundle = new Bundle();
        bundle.putString("time", formattedString);
        mFirebaseAnalytics.logEvent("login", bundle);

        Intent i = new Intent(getApplicationContext(), ChannelListActivity.class);
        startActivity(i);
        finish();
    }

    private boolean isUsernameSet() {
        String username = mSharedPreferences.getString(getString(R.string.preference_username), null);
        if(username == null) {
            return false;
        } else {
            UserControl.get(getApplication()).setUsername(username);
            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void onComplete() {
        Log.d(TAG, "Token registration Complete");
    }

    public void onError(String message) {
        Log.d(TAG, "Error trying to register the token in the DB: " + message);
    }



}
