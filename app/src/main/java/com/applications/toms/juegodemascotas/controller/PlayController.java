package com.applications.toms.juegodemascotas.controller;

import android.content.Context;

import com.applications.toms.juegodemascotas.dao.DaoPlay;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PlayController {

    private DaoPlay daoPlay;

    //Provide Plays data from DataBase
    public PlayController() {
        daoPlay = new DaoPlay();
    }

    //return all plays
    public void givePlayDateList(List<PlayDate> currentPlayDateList, Context context, ResultListener<List<PlayDate>> resultListener) {
        if (Util.isOnline(context)) {
            daoPlay.fetchPlayDateList(context, result -> {
                List<PlayDate> duplicatedPlayDateList = new ArrayList<>();
                //Check if the data is duplicated
                for (PlayDate pplayDate : result) {
                    if (currentPlayDateList.contains(pplayDate))
                        duplicatedPlayDateList.add(pplayDate);
                }
                result.removeAll(duplicatedPlayDateList);
                if (result.isEmpty())
                    result = null;
                resultListener.finish(result);
            });
        } else {
            resultListener.finish(new ArrayList<>());
        }

    }

    //return one Play
    public void givePlayDate(String playId, Context context, ResultListener<PlayDate> resultListener) {
        if (Util.isOnline(context)) {
            daoPlay.fetchPlayDate(playId, context, resultListener);
        } else {
            resultListener.finish(null);
        }

    }

    //return owners plays
    public void giveOwnerPlayDateList(String ownerId, Context context, ResultListener<List<PlayDate>> resultListener) {
        if (Util.isOnline(context)) {
            daoPlay.fetchOwnerPlays(ownerId, context, resultListener);
        } else {
            resultListener.finish(new ArrayList<>());
        }

    }


}
