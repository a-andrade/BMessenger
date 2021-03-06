package com.longbeachsocial.app.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.longbeachsocial.app.Activities.ChannelListActivity;
import com.longbeachsocial.app.Manager.UserControl;
import com.longbeachsocial.app.R;

import java.util.regex.Pattern;

/**
 * Created by uli on 12/7/2016.
 */

public class UsernameDialog extends DialogFragment {

    private EditText mDialogEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_signin, null);
        mDialogEditText = (EditText)v.findViewById(R.id.username_EditText);


        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.dialog_notice)
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(mDialogEditText.getText() != null) {
                                    UserControl.get(getContext())
                                            .setUsername(mDialogEditText.getText().toString());
                                    saveUsername(mDialogEditText.getText().toString());
//                                    UsernameDialog.this.getDialog().dismiss();
                                    //check parent fragment; if login start activity, else dismiss
                                    Intent i = new Intent(getContext(), ChannelListActivity.class);
                                    startActivity(i);
                                }
                            }

                        })

                .setNegativeButton(
                        android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UsernameDialog.this.getDialog().cancel();
                            }
                        }
                )
                .show();

        ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(false);


        mDialogEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Pattern.matches("^[A-Za-z0-9_-]{3,15}$", s.toString())) {
                    mDialogEditText.setError("Username must be one word containing only letters or numbers");
                }
                else
                    ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(true);


            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 2) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(true);
                }
                else
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(false);
            }
        });
       return  alertDialog;
    }

    private void saveUsername(String username) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_username), username);
        editor.apply();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mDialogEditText = null;
    }
}