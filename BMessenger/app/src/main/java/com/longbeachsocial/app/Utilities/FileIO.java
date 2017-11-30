package com.longbeachsocial.app.Utilities;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.longbeachsocial.app.Fragments.MessagingFragment.TAG;

/**
 * Created by uli on 10/25/2017.
 */

public class FileIO {

    FileOutputStream outputStream;
    private Context mContext;
    public FileIO(Context context) {
        mContext = context;
    }

    public boolean fileOutput(String filename, String string) {

        try {
            outputStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);

            outputStream.write(string.getBytes());
            outputStream.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String fileRead(String filename) {
        String temp="";
        FileInputStream fin;
        try {

            fin = mContext.openFileInput(filename);
            int c;
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            Log.d(TAG, "temp is " + temp);
            fin.close();
            return temp;
        }
        catch (IOException e) {
        }

        return null;
    }


    private String formatMessages(String messages) {


        return null;
    }

}
