package com.bmessenger.bmessenger.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bmessenger.bmessenger.R;

/**
 * Created by uli on 5/7/2017.
 */

public class DisabledFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_disabled, container, false);
        return v;
    }
}
