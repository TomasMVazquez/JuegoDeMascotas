package com.applications.toms.juegodemascotas.dao;

import android.content.Context;

import androidx.annotation.NonNull;

import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DaoOwner {

    private List<Owner> ownerList = new ArrayList<>();

    public void giveDuenios(Context context, final ResultListener<List<Owner>> listResultListener){
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference  = mDatabase.getReference();

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                    Owner owner = childSnapShot.getValue(Owner.class);
                    ownerList.add(owner);
                }
                listResultListener.finish(ownerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
