package com.applications.toms.juegodemascotas.controller;

import android.content.Context;

import com.applications.toms.juegodemascotas.dao.DaoPetsFromOwner;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.ResultListener;

import java.util.List;

public class PetsFromOwnerController {

    public void giveOwnerPets(String idDuenio, Context context, final ResultListener<List<Pet>> resultListener){
        DaoPetsFromOwner daoPetsFromOwner = new DaoPetsFromOwner();
        daoPetsFromOwner.giveMyPets(idDuenio, context, new ResultListener<List<Pet>>() {
            @Override
            public void finish(List<Pet> resultado) {
                resultListener.finish(resultado);
            }
        });
    }

}
