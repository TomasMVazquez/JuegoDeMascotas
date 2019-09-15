package com.applications.toms.juegodemascotas.controller;

import android.content.Context;

import com.applications.toms.juegodemascotas.dao.DaoOwner;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.util.ResultListener;

import java.util.ArrayList;
import java.util.List;

public class OwnerController {

    private List<Owner> ownerList = new ArrayList<>();

    public void giveDuenios(Context context, final ResultListener<List<Owner>> listResultListener){

        DaoOwner daoOwner = new DaoOwner();
        daoOwner.giveDuenios(context, new ResultListener<List<Owner>>() {
            @Override
            public void finish(List<Owner> resultado) {
                listResultListener.finish(resultado);
            }
        });

    }

}
