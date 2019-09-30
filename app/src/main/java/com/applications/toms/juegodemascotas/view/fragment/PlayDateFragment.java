package com.applications.toms.juegodemascotas.view.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PlayController;
import com.applications.toms.juegodemascotas.view.NewPlayDate;
import com.applications.toms.juegodemascotas.view.PlayDateDetail;
import com.applications.toms.juegodemascotas.view.adapter.MapAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayDateFragment extends Fragment implements MapAdapter.MapAdapterInterface {

    private static Context context;
    private static FirebaseUser currentUser;
    private MapAdapter mapAdapter;

    public PlayDateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_date, container, false);

        context = getApplicationContext();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mapAdapter = new MapAdapter(new ArrayList<>(),this);
        PlayController playController = new PlayController();

        FloatingActionButton fabNewPlayDate = view.findViewById(R.id.fabNewPlayDate);

        //Recycler View
        RecyclerView recyclerPlayDates = view.findViewById(R.id.recyclerPlayDates);
        recyclerPlayDates.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerPlayDates.setLayoutManager(llm);
        //adaptador
        recyclerPlayDates.setAdapter(mapAdapter);

        if (currentUser != null){
            //Traigo los juegos
            playController.givePlayDateList(context,result -> mapAdapter.setPlayDates(result));

            fabNewPlayDate.setOnClickListener(v -> {
                Intent intentMap = new Intent(context, NewPlayDate.class);
                startActivity(intentMap);
            });
        }

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
