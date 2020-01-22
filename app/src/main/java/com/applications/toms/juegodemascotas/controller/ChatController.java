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

    public void giveMyChatsList(String userId, Context context, ResultListener<List<Chat>> resultListener){
        if (Util.isOnline(context)){
            daoChat.fetchMyChatList(userId, context, resultListener);
        }else {
            resultListener.finish(new ArrayList<>());
        }
    }

    public void giveLastMessage(String chatId, Context context, ResultListener<List<Message>> resultListener){
        if (Util.isOnline(context)) {
            daoChat.fetchLastMessage(chatId, context, resultListener);
        }else {
            resultListener.finish(new ArrayList<>());
        }
    }

    public void giveLastMessageId(String chatId, Context context, ResultListener<String> resultListener){
        if (Util.isOnline(context)) {
            daoChat.fetchLastMessageID(chatId, context, resultListener);
        }else {
            resultListener.finish(null);
        }
    }

    public void giveUserNameToChat(String userId,Context context, ResultListener<String> resultListener){
        if (Util.isOnline(context)) {
            daoChat.fetchUserToChat(userId, context, resultListener);
        }else {
            resultListener.finish("");
        }
    }

    public void deleteChat(String chatID,String userTwo, String userID, Context context, ResultListener<Boolean> resultListener){
        daoChat.deleteChat(chatID,userTwo,userID,context,resultListener);
    }

}
