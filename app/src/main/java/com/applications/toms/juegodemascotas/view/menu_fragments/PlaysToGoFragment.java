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
import com.applications.toms.juegodemascotas.util.FragmentTitles;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.view.PlayDateDetail;
import com.applications.toms.juegodemascotas.view.adapter.MyPlaysMapAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaysToGoFragment extends Fragment implements MyPlaysMapAdapter.MapAdapterInterface, FragmentTitles {

    public static final String TAG = "PlaysToGoFragment";

    private static FirebaseUser currentUser;
    private static Context context;
    private PlayController playController;

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

        playController = new PlayController();
        myPlaysMapAdapter = new MyPlaysMapAdapter(new ArrayList<>(),this);

        //Recycler View
        RecyclerView recyclerView = view.findViewById(R.id.recyclerMyPlays);
        recyclerView.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(llm);
        //adaptador
        recyclerView.setAdapter(myPlaysMapAdapter);

        refreshPlays();

        return view;
    }

    private void refreshPlays(){
        playController.giveOwnerPlayDateList(currentUser.getUid(), context, result -> {
            Collections.sort(result, (o1, o2) -> o1.getDateTime().compareTo(o2.getDateTime()));
            myPlaysMapAdapter.setMyPlays(result);
        });
    }

    @Override
    public void goToPlayDetail(String juegoId) {
        Intent intent = new Intent(context, PlayDateDetail.class);
        Bundle bundle = new Bundle();
        bundle.putString(PlayDateDetail.KEY_PLAY_DETAIL,juegoId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public int getFragmentTitle() {
        return R.string.plays;
    }
}
