package com.applications.toms.juegodemascotas.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applications.toms.juegodemascotas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayDateFragment extends Fragment {

    private static Context context;
    private static FirebaseUser currentUser;

    public PlayDateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_date, container, false);
        View viewNologin = inflater.inflate(R.layout.fragment_no_login, container, false);

        context = getApplicationContext();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){
            return view;
        }else {
            return viewNologin;
        }

    }

}
