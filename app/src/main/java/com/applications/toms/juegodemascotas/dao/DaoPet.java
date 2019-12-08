package com.applications.toms.juegodemascotas.dao;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DaoPet {
    private static final String TAG = "DaoPet";

    private List<Pet> petList = new ArrayList<>();
    private FirebaseFirestore mDatabase;

    //DAO to look for pets and pets data
    public DaoPet() {
        mDatabase = FirebaseFirestore.getInstance();
    }

    //return all pets from DataBase
    public void fetchPetList(Context context, ResultListener<List<Pet>> listResultListener){
        //Data Base Instance
//        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        //DataBase Collection of owners/users
        CollectionReference petRef = mDatabase.collection(context.getString(R.string.collection_pets));
        //extract list of owners
        petRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            petList.addAll(queryDocumentSnapshots.toObjects(Pet.class));
            listResultListener.finish(petList);
        });
    }

    //Return only one pet with it petId
    public void fetchPet(String petId, Context context, ResultListener<Pet> petResultListener){
        //extract single Pet data
        DocumentReference petRef = mDatabase.collection(context.getString(R.string.collection_pets))
                .document(petId);

        petRef.get().addOnSuccessListener(documentSnapshot -> {
            Pet pet = documentSnapshot.toObject(Pet.class);
            petResultListener.finish(pet);
        });
    }

    //return a list of pet for the search logic
    public void searchPet(String search,Context context, ResultListener<List<Pet>> listResultListener){
        mDatabase.collection(context.getString(R.string.collection_pets))
                .whereGreaterThanOrEqualTo("nombre",search.toUpperCase())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listResultListener.finish(task.getResult().toObjects(Pet.class));
                    }
                });
    }

    //Return all owners pet with their user id
    public void fetchOwnerPets(String ownerId, Context context, ResultListener<List<Pet>> listResultListener){

        CollectionReference ownerRef = mDatabase.collection(context.getString(R.string.collection_users))
                .document(ownerId).collection(context.getString(R.string.collection_my_pets));

        ownerRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            try {
                petList.addAll(queryDocumentSnapshots.toObjects(Pet.class));
                listResultListener.finish(petList);
            }catch (Exception exp){
                Thread.currentThread().interrupt();
                Log.d(TAG, "fetchOwnerPets: Error Thread interrupted");
            }
        });
    }

    //return pets avatar from storage
    public void fetchPetAvatar(String ownerId, String avatar, Context context, ResultListener<Uri> uriResultListener){
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = mStorage.getReference().child(ownerId).child(avatar);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> uriResultListener.finish(uri));
    }
}
