package com.applications.toms.juegodemascotas.dao;

import android.content.Context;
import android.util.Log;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DaoPlay {

    private List<PlayDate> playDateList = new ArrayList<>();

    public void fetchPlayDateList(Context context, ResultListener<List<PlayDate>> listResultListener){
        //Data Base Instance
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

        CollectionReference playRef = mDatabase.collection(context.getString(R.string.collection_play));

        playRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                playDateList.addAll(queryDocumentSnapshots.toObjects(PlayDate.class));
                listResultListener.finish(playDateList);
            }else {
                listResultListener.finish(new ArrayList<>());
            }
        });
    }

    public void fetchPlayDate(String playId,Context context,ResultListener<PlayDate> playDateResultListener){
        //Data Base Instance
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

        //extract single Pet data
        DocumentReference playRef = mDatabase.collection(context.getString(R.string.collection_play))
                .document(playId);

        playRef.get().addOnSuccessListener(documentSnapshot -> {
            PlayDate play = documentSnapshot.toObject(PlayDate.class);
            playDateResultListener.finish(play);
        });
    }

    public void fetchOwnerPlays(String ownerId, Context context, ResultListener<List<PlayDate>> listResultListener){
        //Data Base Instance
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

        //extract single owner play data
        CollectionReference ownerRef = mDatabase.collection(context.getString(R.string.collection_users))
                .document(ownerId).collection(context.getString(R.string.collection_my_plays));

        ownerRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            playDateList.addAll(queryDocumentSnapshots.toObjects(PlayDate.class));
            listResultListener.finish(playDateList);
        });
    }

}
