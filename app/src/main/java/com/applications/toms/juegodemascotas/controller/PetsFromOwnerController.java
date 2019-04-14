package com.applications.toms.juegodemascotas.controller;

import android.content.Context;

import com.applications.toms.juegodemascotas.dao.DaoPetsFromOwner;
import com.applications.toms.juegodemascotas.model.Mascota;
import com.applications.toms.juegodemascotas.util.ResultListener;

import java.util.List;

public class PetsFromOwnerController {

    public void giveOwnerPets(String idDuenio, Context context, final ResultListener<List<Mascota>> resultListener){
        DaoPetsFromOwner daoPetsFromOwner = new DaoPetsFromOwner();
        daoPetsFromOwner.giveMyPets(idDuenio, context, new ResultListener<List<Mascota>>() {
            @Override
            public void finish(List<Mascota> resultado) {
                resultListener.finish(resultado);
            }
        });
    }

}
