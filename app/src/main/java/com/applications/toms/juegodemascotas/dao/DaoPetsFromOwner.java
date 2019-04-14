package com.applications.toms.juegodemascotas.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import com.applications.toms.juegodemascotas.model.Duenio;
import com.applications.toms.juegodemascotas.model.Mascota;
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
    private List<Mascota> mascotaList = new ArrayList<>();

    //Constructor
    public void giveMyPets(final String idDuenio, Context context, final ResultListener<List<Mascota>> listResultListener){
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference  = mDatabase.getReference();

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                    Duenio duenio = childSnapShot.getValue(Duenio.class);
                    if (duenio.getUserId().equals(idDuenio)){
                        if (duenio.getMisMascotas()!=null){
                            listResultListener.finish(duenio.getMisMascotas());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
