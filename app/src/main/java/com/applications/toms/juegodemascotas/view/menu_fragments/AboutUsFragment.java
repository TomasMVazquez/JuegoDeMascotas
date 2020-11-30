package com.applications.toms.juegodemascotas.view.menu_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.util.FragmentTitles;

public class AboutUsFragment extends Fragment implements FragmentTitles {

    public static final String TAG = "AboutUsFragment";

    public AboutUsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public int getFragmentTitle() {
        return R.string.about_us;
    }
}