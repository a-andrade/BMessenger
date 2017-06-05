package com.bmessenger.bmessenger.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bmessenger.bmessenger.Manager.UserControl;
import com.bmessenger.bmessenger.R;

/**
 * Created by uli on 12/7/2016.
 */

public class SimpleDialog extends DialogFragment {
    //TODO: Remove this class


    private TextView noticeTextView;
    private EditText mDialogEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_simple, null);
        mDialogEditText = (EditText)v.findViewById(R.id.dialog_editText);

        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/WireOne.ttf");


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
                                SimpleDialog.this.getDialog().cancel();
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