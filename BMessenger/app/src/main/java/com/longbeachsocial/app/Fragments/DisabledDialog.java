package com.longbeachsocial.app.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by uli on 7/3/2017.
 */

public class DisabledDialog extends DialogFragment {

    public DisabledDialog() {
        // Empty constructor required for DialogFragment
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = "You left Long Beach State";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Long Beach Social is for users on the CSULB campus only. To continue" +
                " using this service make sure Location is enabled for LB Social and return to campus.");

        return alertDialogBuilder.create();
    }
}