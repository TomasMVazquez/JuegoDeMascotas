package com.applications.toms.juegodemascotas.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

public class AdminStorage {

    private static final String TAG = "AdminStorage";

    public static void deleteOldPlayDates(Context context){
        Log.d(TAG, "deleteOldPlayDates");
        //Data Base Instance
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

        CollectionReference playRef = mDatabase.collection(context.getString(R.string.collection_play));

        playRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                    PlayDate playDate = documentSnapshot.toObject(PlayDate.class);
                    if (isOldPlayDate(playDate)){
                        deleteDocument(context, playDate.getIdPlay());
                    }
                }
            }
        });

    }

    public static void deleteMyOldPlayDates(Context context, String userId){
        //Data Base Instance
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

        //extract single owner play data
        CollectionReference ownerRef = mDatabase
                .collection(context.getString(R.string.collection_users))
                .document(userId)
                .collection(context.getString(R.string.collection_my_plays));

        ownerRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                    PlayDate playDate = documentSnapshot.toObject(PlayDate.class);
                    if (isOldPlayDate(playDate)){
                        deleteMyDocument(context,userId, playDate.getIdPlay());
                    }
                }
            }
        });
    }

    private static void deleteDocument(Context context,String playId){
        DocumentReference docRef = FirebaseFirestore
                .getInstance()
                .collection(context.getString(R.string.collection_play))
                .document(playId);

        docRef
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Delete Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    private static void deleteMyDocument(Context context,String userId,String playId){
        DocumentReference docRef = FirebaseFirestore
                .getInstance()
                .collection(context.getString(R.string.collection_users))
                .document(userId)
                .collection(context.getString(R.string.collection_my_plays))
                .document(playId);

        docRef
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Delete Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    private static Boolean isOldPlayDate(PlayDate playDate){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try {
            Date now = new Date();
            Date date=new SimpleDateFormat("dd/MM/yyyy hh:mm")
                    .parse(playDate.getDatePlay() + " " + playDate.getTimePlay());
            Log.d(TAG, "onComplete: Fecha " + date);

            Log.d(TAG, "onComplete: hoy " + now);
            if (date.compareTo(now) > 0) {
                //"Date1 is after Date2"
                return false;
            } else if (date.compareTo(now) < 0) {
                //Date1 is before Date2
                return true;
            } else if (date.compareTo(now) == 0) {
                //Date1 is equal to Date2
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "onComplete: " + e.toString());
        }
        return false;
    }

}
