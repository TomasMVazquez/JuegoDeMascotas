package com.applications.toms.juegodemascotas.controller;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.applications.toms.juegodemascotas.dao.DaoPet;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PetController {

    private DaoPet daoPet;

    //Provide Pets data from DataBase
    public PetController() {
        daoPet = new DaoPet();
    }

    //Return pets list
    public void givePetList(Context context, ResultListener<List<Pet>> resultListener) {
        if (Util.isOnline(context)) {
            daoPet.fetchPetList(context, resultado -> resultListener.finish(resultado));
        } else {
            resultListener.finish(new ArrayList<>());
        }
    }

    //Return pets list
    public void givePetListDup(List<Pet> currentPetList,Context context, ResultListener<List<Pet>> resultListener) {
        if (Util.isOnline(context)) {
            daoPet.fetchPetList(context, resultado -> {
                List<Pet> duplicatedPetsList = new ArrayList<>();
                //Check if the data is duplicated, method Overloaded to test it
                for (Pet pet : resultado) {
                    if (currentPetList.contains(pet))
                        duplicatedPetsList.add(pet);
                }
                resultado.removeAll(duplicatedPetsList);
                if (resultado.isEmpty())
                    resultado = null;
                resultListener.finish(resultado);
            });
        } else {
            resultListener.finish(new ArrayList<>());
        }
    }

    //return one pet
    public void givePet(String petId, Context context, ResultListener<Pet> resultListener) {
        if (Util.isOnline(context)) {
            daoPet.fetchPet(petId, context, resultado -> resultListener.finish(resultado));
        } else {
            resultListener.finish(null);
        }

    }

    //return owners pets
    public void giveOwnerPets(List<Pet> currentPetList, String ownerId, Context context, ResultListener<List<Pet>> resultListener) {
        if (Util.isOnline(context)) {
            daoPet.fetchOwnerPets(ownerId, context, resultado -> {
                List<Pet> duplicatedPetsList = new ArrayList<>();
                //Check if the data is duplicated, method Overloaded to test it
                for (Pet pet : resultado) {
                    if (currentPetList.contains(pet))
                        duplicatedPetsList.add(pet);
                }
                resultado.removeAll(duplicatedPetsList);
                if (resultado.isEmpty())
                    resultado = null;
                resultListener.finish(resultado);
            });
        }
    }

    //return owners pets
    public void giveOwnerPets(String ownerId, Context context, ResultListener<List<Pet>> resultListener) {
        daoPet.fetchOwnerPets(ownerId, context, resultado -> resultListener.finish(resultado));
    }

    //Return pets avatar
    public void givePetAvatar(String ownerId, String avatar, Context context, ResultListener<Uri> resultListener) {
        if (Util.isOnline(context)) {
            daoPet.fetchPetAvatar(ownerId, avatar, context, result -> resultListener.finish(result));
        }
    }

    //Search result
    public void giveResultSearch(String search, Context context, ResultListener<List<Pet>> resultListener) {
        if (Util.isOnline(context)) {
            daoPet.searchPet(search, context, result -> resultListener.finish(result));
        } else {
            resultListener.finish(new ArrayList<>());
        }
    }
}
