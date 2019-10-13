package com.applications.toms.juegodemascotas.view.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PlayController;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.applications.toms.juegodemascotas.view.NewPlayDate;
import com.applications.toms.juegodemascotas.view.PlayDateDetail;
import com.applications.toms.juegodemascotas.view.adapter.MapAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
            playController.givePlayDateList(context,result -> {
                Collections.sort(result, (o1, o2) -> o1.getDateTime().compareTo(o2.getDateTime()));
                mapAdapter.setPlayDates(result);
            });

            fabNewPlayDate.setOnClickListener(v -> {
                Intent intentMap = new Intent(context, NewPlayDate.class);
                startActivity(intentMap);
                getActivity().finish();
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

    @Override
    public void updatePlayDate(String juegoId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference playRef = db.collection(context.getString(R.string.collection_play))
                .document(juegoId);

        playRef.get().addOnSuccessListener(documentSnapshot -> {
            PlayDate playClicked = documentSnapshot.toObject(PlayDate.class);
            if (!playClicked.getParticipants().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                playRef.update(context.getString(R.string.collection_participants), FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                Toast.makeText(context, "Se agrego la cita a tus juegos", Toast.LENGTH_SHORT).show();
                joinToCreatorPlayDate(playClicked.getCreator().getUserId(),juegoId);
            }else {
                Toast.makeText(context, "Ya te encuentras unido a este juego", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void joinToCreatorPlayDate(String creatorId, String juegoId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference playRefMasc = db.collection(context.getString(R.string.collection_users))
                .document(creatorId)
                .collection(context.getString(R.string.collection_my_plays)).document(juegoId);

        playRefMasc.get().addOnSuccessListener(documentSnapshotTwo -> {
            PlayDate ownerPlay = documentSnapshotTwo.toObject(PlayDate.class);
            if (!ownerPlay.getParticipants().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                playRefMasc.update(context.getString(R.string.collection_participants), FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()));
            }
        });
    }
}
