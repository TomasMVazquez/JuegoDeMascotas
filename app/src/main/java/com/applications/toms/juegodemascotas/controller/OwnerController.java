package com.applications.toms.juegodemascotas.controller;

import android.content.Context;
import android.net.Uri;

import com.applications.toms.juegodemascotas.dao.DaoOwner;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.util.Util;

import java.util.ArrayList;
import java.util.List;

public class OwnerController {

    private DaoOwner daoOwner;

    //Provide Owners data from DataBase
    public OwnerController() {
        daoOwner = new DaoOwner();
    }

    //return all owners
    public void giveOwners(Context context, ResultListener<List<Owner>> resultListener) {
        if (Util.isOnline(context)) {
            daoOwner.fetchOwnerList(context, resultListener);
        } else {
            resultListener.finish(new ArrayList<>());
        }

    }

    //return one owner
    public void giveOwnerData(String ownerId, Context context, ResultListener<Owner> resultListener) {
        if (Util.isOnline(context)) {
            daoOwner.fetchOwner(ownerId, context, resultListener);
        } else {
            resultListener.finish(null);
        }
    }

    //return owners avatar
    public void giveOwnerAvatar(String userId, String avatar, Context context, ResultListener<Uri> resultListener) {
        daoOwner.fetchOwnerAvatar(userId, avatar, context, resultListener);
    }

    public void giveFriends(List<Pet> currenPetList, String ownerId, Context context, ResultListener<List<Pet>> friendsListener) {
        if (Util.isOnline(context)) {
            daoOwner.fetchFriends(ownerId, context, result -> {
                List<Pet> duplicatedPetFriendsList = new ArrayList<>();
                for(Pet petFromCloud : result){
                    if(currenPetList.contains(petFromCloud))
                        duplicatedPetFriendsList.add(petFromCloud);
                }
                result.removeAll(duplicatedPetFriendsList);
                if(result.isEmpty())
                    result = null;
                friendsListener.finish(result);
            });
        } else {
            friendsListener.finish(new ArrayList<>());
        }
    }

    public void giveFriendsToCheck(String ownerId, Context context, ResultListener<List<Pet>> friendsListener){
        if (Util.isOnline(context)) {
            daoOwner.fetchFriends(ownerId,context, friendsListener::finish);
        } else {
            friendsListener.finish(new ArrayList<>());
        }
    }
}
