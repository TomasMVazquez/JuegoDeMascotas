package com.applications.toms.juegodemascotas.dao;

import android.content.Context;
import android.util.Log;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Chat;
import com.applications.toms.juegodemascotas.model.Message;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DaoChat {

    private static final String TAG = "DaoChat";
    private List<Chat> chatList = new ArrayList<>();
    private FirebaseFirestore mDatabase;

    public DaoChat() {
        mDatabase = FirebaseFirestore.getInstance();
    }

}
