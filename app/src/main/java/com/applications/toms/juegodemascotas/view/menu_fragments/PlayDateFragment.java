package com.applications.toms.juegodemascotas.view.menu_fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayDateFragment extends Fragment implements MapAdapter.MapAdapterInterface {

    private static final String TAG = "PlayDateFragment";
    private static Context context;
    private MapAdapter mapAdapter;
    private FirebaseUser currentUser;
    private List<PlayDate> currentPlayDateList = new ArrayList<>();

    public PlayDateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_date, container, false);

        //Application conext
        context = getApplicationContext();
        //Firebase User
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Adapter
        mapAdapter = new MapAdapter(currentPlayDateList, this);

        //Controller
        PlayController playController = new PlayController();

        //FAB view btn
        FloatingActionButton fabNewPlayDate = view.findViewById(R.id.fabNewPlayDate);

        //Recycler View
        RecyclerView recyclerPlayDates = view.findViewById(R.id.recyclerPlayDates);
        recyclerPlayDates.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerPlayDates.setLayoutManager(llm);
        //adaptador
        recyclerPlayDates.setAdapter(mapAdapter);

        //If user is not loged in then not bring anything else get plays and addbtn
        if (currentUser != null) {
            playController.givePlayDateList(currentPlayDateList, context, result -> {
                Collections.sort(result, (o1, o2) -> o1.getDateTime().compareTo(o2.getDateTime()));
                currentPlayDateList = result;
                mapAdapter.setPlayDates(currentPlayDateList);
            });

            fabNewPlayDate.setOnClickListener(v -> {
                Intent intentMap = new Intent(context, NewPlayDate.class);
                startActivity(intentMap);
                getActivity().finish();
            });
        } else {
            fabNewPlayDate.setOnClickListener(v -> Toast.makeText(context, "Debes estar Logeado para crear un Juego", Toast.LENGTH_SHORT).show());
        }

        return view;
    }


    //On Click in detail go to play detail
    @Override
    public void goToPlayDetail(String juegoId) {
        Intent intent = new Intent(context, PlayDateDetail.class);
        Bundle bundle = new Bundle();
        bundle.putString(PlayDateDetail.KEY_PLAY_DETAIL, juegoId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //Add participant to play
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
                joinToCreatorPlayDate(playClicked.getCreator().getUserId(), juegoId);
                addPlayToMyPlays(playClicked);
            } else {
                Toast.makeText(context, "Ya te encuentras unido a este juego", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Add Plays to MyPlays DataBase
    private void addPlayToMyPlays(PlayDate playJoined) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference playRefMasc = db.collection(context.getString(R.string.collection_users))
                .document(currentUser.getUid())
                .collection(context.getString(R.string.collection_my_plays))
                .document(playJoined.getIdPlay());

        playRefMasc.set(playJoined).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Creado en MyPlays");
            }
        });
    }

    //Join user to play
    private void joinToCreatorPlayDate(String creatorId, String juegoId) {
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
