package com.applications.toms.juegodemascotas.view.menu_fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PlayController;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.applications.toms.juegodemascotas.view.MainActivity;
import com.applications.toms.juegodemascotas.view.NewPlayDate;
import com.applications.toms.juegodemascotas.view.PlayDateDetail;
import com.applications.toms.juegodemascotas.view.adapter.MapAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.shape.CircleShape;
import uk.co.deanwild.materialshowcaseview.shape.OvalShape;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayDateFragment extends Fragment implements MapAdapter.MapAdapterInterface {

    private static final String TAG = "PlayDateFragment";
    private static final String SHOWCASE_ID = "simple play date";

    private static Context context;
    private static Activity activity;
    private FrameLayout containerlayDates;

    private MapAdapter mapAdapter;
    private FirebaseUser currentUser;
    private List<PlayDate> currentPlayDateList = new ArrayList<>();

    private static FloatingActionButton fabNewPlayDate;

    public PlayDateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_date, container, false);

        containerlayDates = view.findViewById(R.id.containerlayDates);
        //Application conext
        context = getApplicationContext();
        activity = getActivity();
        //Firebase User
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Adapter
        mapAdapter = new MapAdapter(currentPlayDateList, this);

        //Controller
        PlayController playController = new PlayController();

        //FAB view btn
        fabNewPlayDate = view.findViewById(R.id.fabNewPlayDate);

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
            fabNewPlayDate.setOnClickListener(v -> Snackbar.make(containerlayDates,getString(R.string.play_need_login),Snackbar.LENGTH_SHORT).show());
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
                Snackbar.make(containerlayDates,getString(R.string.play_added),Snackbar.LENGTH_SHORT).show();
                joinToCreatorPlayDate(playClicked.getCreator().getUserId(), juegoId);
                addPlayToMyPlays(playClicked);
            } else {
                Snackbar.make(containerlayDates,getString(R.string.play_already_joined),Snackbar.LENGTH_SHORT).show();
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

        playRefMasc.set(playJoined).addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: Creado en MyPlays"));
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

    public static void presentShowcaseView(int withDelay) {
        new MaterialShowcaseView.Builder(activity)
                .setTarget(fabNewPlayDate)
                .setShape(new CircleShape())
//                .setTitleText("BIENVENIDO")
                .setDismissText(context.getString(R.string.onboard_click))
                .setContentText(context.getString(R.string.onboard_playdate_fab))
                .setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .useFadeAnimation() // remove comment if you want to use fade animations for Lollipop & up
                .show();
    }
}
