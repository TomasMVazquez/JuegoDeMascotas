package com.applications.toms.juegodemascotas.view.menu_fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PlayController;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.view.PlayDateDetail;
import com.applications.toms.juegodemascotas.view.adapter.MyPlaysMapAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaysToGoFragment extends Fragment implements MyPlaysMapAdapter.MapAdapterInterface {

    public static final String TAG = "PlaysToGoFragment";

    private static FirebaseUser currentUser;
    private static Context context;

    private MyPlaysMapAdapter myPlaysMapAdapter;

    public PlaysToGoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plays_to_go, container, false);

        context = getContext();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        PlayController playController = new PlayController();
        myPlaysMapAdapter = new MyPlaysMapAdapter(new ArrayList<>(),this);

        //Recycler View
        RecyclerView recyclerViewPets = view.findViewById(R.id.recyclerMyPlays);
        recyclerViewPets.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerViewPets.setLayoutManager(llm);
        //adaptador
        recyclerViewPets.setAdapter(myPlaysMapAdapter);

        playController.giveOwnerPlayDateList(currentUser.getUid(), context, result -> myPlaysMapAdapter.setMyPlays(result));


        return view;
    }

    @Override
    public void goToPlayDetail(String juegoId) {
        Intent intent = new Intent(context, PlayDateDetail.class);
        Bundle bundle = new Bundle();
        bundle.putString(PlayDateDetail.KEY_PLAY_DETAIL,juegoId);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
