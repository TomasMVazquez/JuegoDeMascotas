package com.applications.toms.juegodemascotas.controller;

import android.content.Context;

import com.applications.toms.juegodemascotas.dao.DaoPlay;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PlayController {

    public void givePlayDateList(Context context, ResultListener<List<PlayDate>> resultListener){
        DaoPlay daoPlay = new DaoPlay();
        if (Util.isOnline(context)){
            daoPlay.fetchPlayDateList(context,resultado -> resultListener.finish(resultado));
        }else {
            resultListener.finish(new ArrayList<>());
        }

    }

    public void givePlayDate(String playId,Context context, ResultListener<PlayDate> resultListener){
        DaoPlay daoPlay = new DaoPlay();
        if (Util.isOnline(context)){
            daoPlay.fetchPlayDate(playId,context,resultado -> resultListener.finish(resultado));
        }else {
            resultListener.finish(null);
        }

    }

    public void giveOwnerPlayDateList(String ownerId, Context context, ResultListener<List<PlayDate>> resultListener){
        DaoPlay daoPlay = new DaoPlay();
        if (Util.isOnline(context)){
            daoPlay.fetchOwnerPlays(ownerId,context,resultado -> resultListener.finish(resultado));
        }else {
            resultListener.finish(new ArrayList<>());
        }

    }

}
