package com.bmessenger.bmessenger.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewGroupCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bmessenger.bmessenger.Fragments.LoginFragment;
import com.bmessenger.bmessenger.Fragments.UsernameDialog;
import com.bmessenger.bmessenger.Manager.UserControl;
import com.bmessenger.bmessenger.R;
import com.bmessenger.bmessenger.Services.IRequestListener;
import com.bmessenger.bmessenger.Utilities.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity implements IRequestListener {
    //TODO: log usernames to keep them unique
    //TODO: dont enable ok unless input is more than 3 character on dialog
    private final String TAG = "bmessenger.Login";
    private Button createUsernameButton;
    private Button getRandomUsernameButton;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        createUsernameButton = (Button)findViewById(R.id.createUsernameButton);
        getRandomUsernameButton = (Button)findViewById(R.id.anonButton);

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
                Intent i = new Intent(getApplicationContext(), ChannelListActivity.class);
                startActivity(i);

            }
        });





        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Log.d(TAG, "attempting to sign in ");
                    signInAnonymously();

                }
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]
    }

    private void signInAnonymously() {
        //callNextActivity();
        // [START signin_anonymously]
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
                        }

                        // [START_EXCLUDE]
                        //callNextActivity();
                        // [END_EXCLUDE]
                    }
                });
        // [END signin_anonymously]

    }


    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]

    public void onComplete() {
        Log.d(TAG, "Token registration Complete");
    }

    public void onError(String message) {
        Log.d(TAG, "Error trying to register the token in the DB: " + message);
    }



}
