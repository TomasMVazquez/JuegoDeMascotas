package com.applications.toms.juegodemascotas.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminStorage {

    private static final String TAG = "AdminStorage";

    public static void deleteOldPlayDates(Context context){
        Log.d(TAG, "deleteOldPlayDates: ");
        //Data Base Instance
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

        CollectionReference playRef = mDatabase.collection(context.getString(R.string.collection_play));

        playRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Task Success");
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        Log.d(TAG, "onComplete: " + documentSnapshot.getId() + " => " + documentSnapshot.getData());
                        PlayDate date = documentSnapshot.toObject(PlayDate.class);
                        Log.d(TAG, "onComplete: " + date.getDatePlay());
                    }
                }
            }
        });

    }

    public static void deleteMyOldPlayDates(String userId){

    }

}
