package com.applications.toms.juegodemascotas.dao;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.Keys;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        //DataBase Collection of owners/users
        CollectionReference petRef = mDatabase.collection(context.getString(R.string.collection_pets));
        //extract list of owners
        petRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                Pet pet = snapshot.toObject(Pet.class);
                if (!pet.getOwnerId().equals(fuser.getUid())){
                    petList.add(pet);
                }
            }
            listResultListener.finish(petList);
        });
    }

    //return a list of pet for the search logic
    public void searchPet(String s,Context context, ResultListener<List<Pet>> listResultListener){
        Query query = mDatabase.collection(Keys.KEY_PET)
                .orderBy(Keys.KEY_PET_SEARCH)
                .startAt(s)
                .endAt(s+"\uf0ff");

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                List<Pet> petList = new ArrayList<>();
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Pet pet = snapshot.toObject(Pet.class);

                    if (!pet.getOwnerId().equals(fuser.getUid())) {
                        petList.add(pet);
                    }
                }
                listResultListener.finish(petList);
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

}
