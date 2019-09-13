package com.applications.toms.juegodemascotas.view.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Juego;
import com.applications.toms.juegodemascotas.model.Mascota;
import com.applications.toms.juegodemascotas.view.MainActivity;
import com.applications.toms.juegodemascotas.view.NewPlayDate;
import com.applications.toms.juegodemascotas.view.adapter.PlayDateAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayDateFragment extends Fragment {

    private static Context context;
    private static FirebaseUser currentUser;
    private PlayDateAdapter playDateAdapter;
    private static FirebaseFirestore db;

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
            db = FirebaseFirestore.getInstance();
            playDateAdapter = new PlayDateAdapter(context,new ArrayList<Juego>(),getFragmentManager());
            //Traigo Juegos de la base
            CollectionReference playRef = db.collection(context.getString(R.string.collection_play));
            playRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
                List<Juego> juegos = new ArrayList<>();
                juegos.addAll(queryDocumentSnapshots.toObjects(Juego.class));
                playDateAdapter.setPlayDates(juegos);
            });

            FloatingActionButton fabNewPlayDate = view.findViewById(R.id.fabNewPlayDate);

            //Recycler View
            RecyclerView recyclerPlayDates = view.findViewById(R.id.recyclerPlayDates);
            recyclerPlayDates.hasFixedSize();
            //LayoutManager
            LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
            recyclerPlayDates.setLayoutManager(llm);
            //adaptador
            recyclerPlayDates.setAdapter(playDateAdapter);

            fabNewPlayDate.setOnClickListener(v -> {
                Intent intentMap = new Intent(context, NewPlayDate.class);
                startActivity(intentMap);
            });

            return view;
        }else {
            return viewNologin;
        }

    }

}
