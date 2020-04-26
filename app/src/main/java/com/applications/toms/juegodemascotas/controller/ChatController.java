package com.applications.toms.juegodemascotas.controller;

import android.content.Context;

import com.applications.toms.juegodemascotas.dao.DaoChat;
import com.applications.toms.juegodemascotas.model.Chat;
import com.applications.toms.juegodemascotas.model.Message;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ChatController {

    private DaoChat daoChat;

    public ChatController() {
        daoChat = new DaoChat();
    }



}
