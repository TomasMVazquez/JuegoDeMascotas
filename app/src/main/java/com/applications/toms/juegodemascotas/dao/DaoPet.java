package com.applications.toms.juegodemascotas.dao;

import android.content.Context;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DaoPet {

    private List<Pet> petList = new ArrayList<>();

    public void fetchPetList(Context context, ResultListener<List<Pet>> listResultListener){
        //Data Base Instance
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        //DataBase Collection of owners/users
        CollectionReference petRef = mDatabase.collection(context.getString(R.string.collection_pets));
        //extract list of owners
        petRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            petList.addAll(queryDocumentSnapshots.toObjects(Pet.class));
            listResultListener.finish(petList);
        });
    }

    public void fetchPet(String petId, Context context, ResultListener<Pet> petResultListener){
        //DataBase instance
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        //extract single Pet data
        DocumentReference petRef = mDatabase.collection(context.getString(R.string.collection_pets))
                .document(petId);

        petRef.get().addOnSuccessListener(documentSnapshot -> {
            Pet pet = documentSnapshot.toObject(Pet.class);
            petResultListener.finish(pet);
        });
    }

    public void fetchOwnerPets(String ownerId, Context context, ResultListener<List<Pet>> listResultListener){
        //DataBase instance
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        //extract single owner pet data
        CollectionReference ownerRef = mDatabase.collection(context.getString(R.string.collection_users))
                .document(ownerId).collection(context.getString(R.string.collection_my_pets));

        ownerRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            petList.addAll(queryDocumentSnapshots.toObjects(Pet.class));
            listResultListener.finish(petList);
        });
    }

}
