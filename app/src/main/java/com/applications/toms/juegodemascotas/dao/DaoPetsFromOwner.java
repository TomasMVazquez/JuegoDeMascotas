package com.applications.toms.juegodemascotas.dao;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DaoPetsFromOwner {

    //Atributos
    private List<Pet> petList = new ArrayList<>();

    //Constructor
    public void giveMyPets(final String idDuenio, Context context, final ResultListener<List<Pet>> listResultListener){
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference  = mDatabase.getReference();

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                    Owner owner = childSnapShot.getValue(Owner.class);
                    if (owner.getUserId().equals(idDuenio)){
//                        if (owner.getMisMascotas()!=null){
//                            listResultListener.finish(owner.getMisMascotas());
//                        }
                        //TODO REVISAR este DAO
                        Toast.makeText(context, "Aca deberia traer las mascotas que saque", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
