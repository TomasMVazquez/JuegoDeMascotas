package com.applications.toms.juegodemascotas.controller;

import android.content.Context;

import com.applications.toms.juegodemascotas.dao.DaoPet;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PetController {

    public void givePetList(Context context, ResultListener<List<Pet>> resultListener){
        DaoPet daoPet = new DaoPet();
        if (Util.isOnline(context)){
            daoPet.fetchPetList(context,resultado -> resultListener.finish(resultado));
        }else {
            resultListener.finish(new ArrayList<>());
        }
    }

    public void givePet(String petId, Context context, ResultListener<Pet> resultListener){
        DaoPet daoPet = new DaoPet();
        if (Util.isOnline(context)){
            daoPet.fetchPet(petId,context,resultado -> resultListener.finish(resultado));
        }else {
            resultListener.finish(null);
        }

    }

    public void giveOwnerPets(String ownerId, Context context, ResultListener<List<Pet>> resultListener){
        DaoPet daoPet = new DaoPet();
        if (Util.isOnline(context)){
            daoPet.fetchOwnerPets(ownerId,context,resultado -> resultListener.finish(resultado));
        }else {
            resultListener.finish(new ArrayList<>());
        }

    }

}
