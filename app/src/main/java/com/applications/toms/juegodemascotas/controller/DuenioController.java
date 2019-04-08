package com.applications.toms.juegodemascotas.controller;

import android.content.Context;

import com.applications.toms.juegodemascotas.dao.DaoOwner;
import com.applications.toms.juegodemascotas.model.Duenio;
import com.applications.toms.juegodemascotas.util.ResultListener;

import java.util.ArrayList;
import java.util.List;

public class DuenioController {

    private List<Duenio> duenioList = new ArrayList<>();

    public void giveDuenios(Context context, final ResultListener<List<Duenio>> listResultListener){

        DaoOwner daoOwner = new DaoOwner();
        daoOwner.giveDuenios(context, new ResultListener<List<Duenio>>() {
            @Override
            public void finish(List<Duenio> resultado) {
                listResultListener.finish(resultado);
            }
        });

    }

}
