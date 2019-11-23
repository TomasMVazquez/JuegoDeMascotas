package com.applications.toms.juegodemascotas.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Chat;
import com.applications.toms.juegodemascotas.view.fragment.ChatFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.ChatRoomFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class    ChatActivity extends AppCompatActivity implements ChatRoomFragment.onChatRoomNotify{

    private static final String TAG = "ChatActivity";
    public static final String KEY_USER_TO_CHAT = "user_to_chat";
    public static final String KEY_CHAT = "chat";

    private static FirebaseUser currentUser;
    private FirebaseFirestore db;

    private ChatRoomFragment chatRoomFragment;

    private String userToChat;
    private String chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Get Firebase instances
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        //Toolbar with the search
        Toolbar myToolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(myToolbar);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //intent and bundle
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        chat = bundle.getString(KEY_CHAT);

        if (!chat.equals("1")){

            Log.d(TAG, "onCreate: Get type 2 -> Exists or create");
            
            userToChat = bundle.getString(KEY_USER_TO_CHAT);
            //TODO name of the chat
            actionBar.setTitle(" |*.*| ");

            checkChatExists(userToChat);
        }else {
            
            Log.d(TAG, "onCreate: Get type 1 -> showRoom");
            actionBar.setTitle(getString(R.string.collection_chats));

            chatRoomFragment = new ChatRoomFragment();
            showChatRoom();
        }

    }

    //Check Database to see if the chat alrady exists
    private void checkChatExists(String userToChat){

        CollectionReference myChatCol = db.collection(getString(R.string.collection_users))
                .document(currentUser.getUid()).collection(getString(R.string.collection_my_chats));

        Log.d(TAG, "checkChatExists: " + userToChat);

        myChatCol.document(userToChat).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                Log.d(TAG, "checkChatExists: result=yes");
//                    DocumentSnapshot documentSnapshot = task.getResult();
                String idChat = (String) documentSnapshot.getData().get(getString(R.string.id_chat));
                showChat(idChat);
            }else {
                Log.d(TAG, "checkChatExists: result=no -> create");
                createChat(userToChat);
            }
        });

    }

    //If doesnt exists create one
    private void createChat(String userToChar){
        Log.d(TAG, "createChat: Creating ...");
        //Room Chat collections create new Chat and get the ID
        CollectionReference chatCollection = db.collection(getString(R.string.collection_chats));
        DocumentReference chatRef = chatCollection.document();
        String idNewChat = chatRef.getId();

        Map<String, String> map = new HashMap<String, String>();
        map.put(getString(R.string.id_chat), idNewChat);

        Chat newChat = new Chat(idNewChat,userToChat,currentUser.getUid());

        chatRef.set(newChat);

        Log.d(TAG, "createChat: ChatRomm ID = " + idNewChat);

        //Create on the current user a document with the chatting and set the idchat
        CollectionReference myChatCol = db.collection(getString(R.string.collection_users))
                .document(currentUser.getUid()).collection(getString(R.string.collection_my_chats));
        myChatCol.document(userToChar).set(map);

        //Create the chat in the other user aswell
        CollectionReference otherChatCol = db.collection(getString(R.string.collection_users))
                .document(userToChar).collection(getString(R.string.collection_my_chats));
        otherChatCol.document(currentUser.getUid()).set(map);

        //Go To chat
        showChat(idNewChat);
    }

    //If does exists call chat
    private void showChat(String idChat){
        Log.d(TAG, "showChat: showing...");
        Bundle bundle = new Bundle();
        bundle.putString(ChatFragment.KEY_ID_CHAT,idChat);
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);
        fragments(chatFragment);
    }

    //Show Chat Room
    private void showChatRoom(){
        fragments(chatRoomFragment);
    }

    //Commit Fragment
    private void fragments(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerChat,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void enterChat(String chatId) {
        showChat(chatId);
    }
}
