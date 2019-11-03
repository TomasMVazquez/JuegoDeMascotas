package com.applications.toms.juegodemascotas.dao;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Chat;
import com.applications.toms.juegodemascotas.model.Message;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DaoChat {

    private static final String TAG = "DaoChat";
    private List<Chat> chatList = new ArrayList<>();
    private FirebaseFirestore mDatabase;

    public DaoChat() {
        mDatabase = FirebaseFirestore.getInstance();
    }

    public void fetchMyChatList(String userId, Context context, ResultListener<List<Chat>> listResultListener){

        CollectionReference chatRef = mDatabase.collection(context.getString(R.string.collection_users))
                .document(userId).collection(context.getString(R.string.collection_my_chats));

        chatRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    fetchChat(document.getData().get("idChat").toString(),context,result -> {
                        chatList.add(result);
                        listResultListener.finish(chatList);
                    });
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }

        });

    }

    private void fetchChat(String chatId, Context context, ResultListener<Chat> chatResultListener){
        CollectionReference chatRef = mDatabase.collection(context.getString(R.string.collection_chats));

        chatRef.document(chatId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                Chat chat = document.toObject(Chat.class);
                chatResultListener.finish(chat);
            }
        });
    }

    public void fetchLastMessage(String chatId, Context context, ResultListener<List<Message>> resultListener){
        CollectionReference chatRef = mDatabase.collection(context.getString(R.string.collection_chats))
                .document(chatId).collection(context.getString(R.string.collection_messages));

        chatRef.orderBy("time", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    QuerySnapshot document = task.getResult();
                    List<Message> message = document.toObjects(Message.class);
                    resultListener.finish(message);
                });
    }

}
