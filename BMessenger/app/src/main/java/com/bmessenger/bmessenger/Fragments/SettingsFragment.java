package com.bmessenger.bmessenger.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.bmessenger.bmessenger.R;

import java.util.regex.Pattern;

/**
 * Created by One on 9/29/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    private EditText mEditText;
    private EditTextPreference mEditTextPreference;
    private Dialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        mEditTextPreference = ((EditTextPreference)getPreferenceManager()
                .findPreference(getString(R.string.preference_username)));

        mEditText = mEditTextPreference .getEditText();



        mEditTextPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mDialog = mEditTextPreference.getDialog();
                ((android.app.AlertDialog) mDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(false);

                mEditText.setSingleLine(true);

                mEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(!Pattern.matches("^[A-Za-z0-9_-]{3,15}$", s.toString())) {
                            mEditText.setError("Username must be one word containing only " +
                                    "letters or numbers");
                            ((android.app.AlertDialog) mDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setEnabled(false);
                        }
                        else {
                            ((android.app.AlertDialog) mDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s.length() > 2) {
//                            ((android.app.AlertDialog) mDialog).getButton(AlertDialog.BUTTON_POSITIVE)
//                                    .setEnabled(true);
                        }
                    }
                });



                return true;
            }
        });



    }


}