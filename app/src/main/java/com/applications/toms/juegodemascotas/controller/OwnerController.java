package com.applications.toms.juegodemascotas.controller;

import android.content.Context;

import com.applications.toms.juegodemascotas.dao.DaoOwner;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.util.Util;

import java.util.ArrayList;
import java.util.List;

public class OwnerController {

    public void giveOwners(Context context, ResultListener<List<Owner>> resultListener){
        DaoOwner daoOwner = new DaoOwner();
        if (Util.isOnline(context)){
            daoOwner.fetchOwnerList(context, resultado -> resultListener.finish(resultado));
        }else {
            resultListener.finish(new ArrayList<>());
        }

    }

    public void giveOwnerData(String ownerId, Context context, ResultListener<Owner> resultListener){
        DaoOwner daoOwner = new DaoOwner();
        if (Util.isOnline(context)){
            daoOwner.fetchOwner(ownerId, context, resultado -> resultListener.finish(resultado));
        }else {
            resultListener.finish(null);
        }

    }


}
