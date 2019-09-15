package com.applications.toms.juegodemascotas.dao;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.view.ProfileActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DaoOwner {

    private List<Owner> ownerList = new ArrayList<>();

    public void fetchOwnerList(Context context, ResultListener<List<Owner>> listResultListener){
        //Data Base Instance
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        //DataBase Collection of owners/users
        CollectionReference ownerRef = mDatabase.collection(context.getString(R.string.collection_users));
        //extract list of owners
        ownerRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            ownerList.addAll(queryDocumentSnapshots.toObjects(Owner.class));
            listResultListener.finish(ownerList);
        });
    }

    public void fetchOwner(String ownerId,Context context, ResultListener<Owner> ownerResultListener){
        //DataBase instance
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        //extract single owner data
        DocumentReference ownerRef = mDatabase.collection(context.getString(R.string.collection_users))
                .document(ownerId);

        ownerRef.get().addOnSuccessListener(documentSnapshot -> {
            Owner owner = documentSnapshot.toObject(Owner.class);
            ownerResultListener.finish(owner);
        });
    }

    public void fetchOwnerAvatar(String userId, String avatar, Context context, ResultListener<Uri> uriResultListener){
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = mStorage.getReference().child(userId).child(avatar);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> uriResultListener.finish(uri));
    }
}
