package com.bmessenger.bmessenger;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.GregorianCalendar;

/**
 * Created by uli on 12/7/2016.
 */

public class UsernameDialog extends DialogFragment {


    private TextView noticeTextView;
    private EditText mDialogEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_signin, null);
        noticeTextView = (TextView)v.findViewById(R.id.dialog_TextView);
        mDialogEditText = (EditText)v.findViewById(R.id.username_EditText);

        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/WireOne.ttf");

        noticeTextView.setTypeface(custom_font);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(mDialogEditText.getText() != null) {
                                    UserControl.get(getContext())
                                            .setUsername(mDialogEditText.getText().toString());
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
                .create();

//        LayoutInflater inflater = getActivity().getLayoutInflater();

//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_signin, null))
//
//                // Add action buttons
//                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        UsernameDialog.this.getDialog().cancel();
//                    }
//                });
//        return builder.create();
    }
}