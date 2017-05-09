package com.bmessenger.bmessenger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.bmessenger.bmessenger.Services.IRequestListener;
import com.bmessenger.bmessenger.R;
import com.bmessenger.bmessenger.Services.TokenService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by uli on 12/7/2016.
 */

public class AuthenticateActivity extends Activity implements IRequestListener {
    private static final String TAG = "AuthenticateActivity";

    private TextView mTextView;


    private TokenService tokenService;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_intro);
        mTextView = (TextView) findViewById(R.id.splash_TextView);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/WireOne.ttf");

        mTextView.setTypeface(custom_font);



        final String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token: " + token);

        //Call the token service to save the token in the database
        tokenService = new TokenService(this, this);
        tokenService.registerTokenInDB(token);



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
                    callNextActivity();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Log.d(TAG, "attempting to sign in ");
                    signInAnonymously();

                }
                // [START_EXCLUDE]
                updateUI(user);
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]
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
                            callNextActivity();
                        }

                        // [START_EXCLUDE]
                        //callNextActivity();
                        // [END_EXCLUDE]
                    }
                });
        // [END signin_anonymously]

    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

//    private void linkAccount() {
//        // Make sure form is valid
//        if (!validateLinkForm()) {
//            return;
//        }
//
//        // Get email and password from form
//        String email = mUsernameEditText.getText().toString();
//        String password = mPasswordField.getText().toString();
//
//        // Create EmailAuthCredential with email and password
//        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
//
//        // Link the anonymous user to the email credential
//        showProgressDialog();
//        // [START link_credential]
//        mAuth.getCurrentUser().linkWithCredential(credential)
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "linkWithCredential:onComplete:" + task.isSuccessful());
//
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        if (!task.isSuccessful()) {
//                            Toast.makeText(getContext(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//                        // [START_EXCLUDE]
//                        hideProgressDialog();
//                        // [END_EXCLUDE]
//                    }
//                });
//        // [END link_credential]
//    }

    private void callNextActivity() {
        Intent i = new Intent(this, ChannelListActivity.class);
        startActivity(i);
        finish();
    }

//    private boolean validateLinkForm() {
//        boolean valid = true;
//
//        String email = mUsernameEditText.getText().toString();
//        if (TextUtils.isEmpty(email)) {
//            mUsernameEditText.setError("Required.");
//            valid = false;
//        } else {
//            mUsernameEditText.setError(null);
//        }
//
//        String password = mPasswordField.getText().toString();
//        if (TextUtils.isEmpty(password)) {
//            mPasswordField.setError("Required.");
//            valid = false;
//        } else {
//            mPasswordField.setError(null);
//        }
//
//        return valid;
//    }

    private void updateUI(FirebaseUser user) {
        //callNextActivity();
        boolean isSignedIn = (user != null);

        // Status text
        if (isSignedIn) {
            Log.d(TAG, "Singed In");
        } else {
            Log.d(TAG, "Signed Out");
        }

    }

    public void onComplete() {
        Log.d(TAG, "Token registration Complete");
    }

    public void onError(String message) {
        Log.d(TAG, "Error trying to register the token in the DB: " + message);
    }


}
