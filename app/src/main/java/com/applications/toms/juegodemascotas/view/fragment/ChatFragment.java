package com.applications.toms.juegodemascotas.view.fragment;


import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.ChatController;
import com.applications.toms.juegodemascotas.controller.OwnerController;
import com.applications.toms.juegodemascotas.controller.PetController;
import com.applications.toms.juegodemascotas.dao.DaoChat;
import com.applications.toms.juegodemascotas.model.Message;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.FragmentTitles;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.view.MainActivity;
import com.applications.toms.juegodemascotas.view.adapter.ChatRoomAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements FragmentTitles {

    public static final String TAG = "ChatFragment";
    public static final String KEY_ID_CHAT = "chat";

    public static final String DATE_FORMAT_1 = "yy-MM-dd HH:mm";

    private LinearLayout layout;
    private EditText messageArea;
    private ScrollView scrollView;
    private CoordinatorLayout coordinatorSnack;
    private Context context;

    private String idChat;
    private String message;
    private String userName;
    private String time;
    private Integer idLastMessage;

    private String userIdToChat;
    private String userNameToChat;

    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private CollectionReference chatCollection;

    private ArrayList<Message> messageArrayList = new ArrayList<>();
    private ArrayList<Integer> msgIdsShown = new ArrayList<>();

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        //Setting the context and Databases
        context = getContext();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        //Setting the Views
        layout = view.findViewById(R.id.layout1);
        ImageView sendButton = view.findViewById(R.id.sendButton);
        messageArea = view.findViewById(R.id.messageArea);
        scrollView = view.findViewById(R.id.scrollView);
        coordinatorSnack = view.findViewById(R.id.coordinatorSnack);

        Log.d(TAG, "onCreateView: ChatFragment");

        //Getting the ID chat form the arguments
        Bundle bundle = getArguments();
        idChat = bundle.getString(KEY_ID_CHAT);

        ChatController chatController = new ChatController();

        //Getting the Chat of Firebase
        DocumentReference chatDoc = db.collection(getString(R.string.collection_chats)).document(idChat);
        chatCollection = chatDoc.collection(getString(R.string.collection_messages));

        //Getting the name to show in the App Bar
        chatDoc.addSnapshotListener((documentSnapshot, e) -> {
            String userOne = documentSnapshot.get("userOne").toString();
            String userTwo = documentSnapshot.get("userTwo").toString();

            if (!currentUser.getUid().equals(userOne)){
                userIdToChat = userOne;
            }else {
                userIdToChat = userTwo;
            }
            //Getting the name from the controller
            chatController.giveUserNameToChat(userIdToChat, context, result -> {
                userNameToChat = result;
                MainActivity.changeActionBarTitle(userNameToChat);
            });

            //Checking if its friend
            checkIfFriend(userIdToChat);

        });

        final String user = currentUser.getDisplayName();

        addedMsg();
        
        sendButton.setOnClickListener(v -> {
            String messageText = messageArea.getText().toString();

            if(!messageText.equals("")){
                Map<String, Object> map = new HashMap<>();
                map.put("message", messageText);
                map.put("user", user);
                map.put("time", getCurrentDate());

                chatController.giveLastMessageId(idChat, context, result -> {
                    if (result != null){
                        idLastMessage = Integer.parseInt(result) + 1;
                        map.put("id", idLastMessage);
                        chatCollection.document(String.valueOf(idLastMessage)).set(map);
                    }else {
                        idLastMessage = 1;
                        map.put("id", idLastMessage);
                        chatCollection.document(String.valueOf(idLastMessage)).set(map);
                    }
                    scrollView.fullScroll(View.FOCUS_DOWN);
                });

                messageArea.setText("");
            }
        });

        scrollView.postDelayed(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN),1000);

        return view;
    }

    private void checkIfFriend(String userIdToChat){
        PetController petController = new PetController();
        OwnerController ownerController = new OwnerController();

        ownerController.giveFriendsToCheck(currentUser.getUid(),context,result -> {
            petController.giveOwnerPets(userIdToChat,context,resultToChat -> {
                if (!result.contains(resultToChat.get(0))){
                    Snackbar snackbar = Snackbar.make(coordinatorSnack,context.getString(R.string.add_friend),Snackbar.LENGTH_LONG);

                    snackbar.setAction(context.getString(R.string.add_friend_click), v -> {
                        for (Pet pet:resultToChat) {
                            //Create on the current user a document with firend list
                            CollectionReference myFriendCol = db.collection(getString(R.string.collection_users))
                                    .document(currentUser.getUid()).collection(getString(R.string.collection_my_friends));

                            myFriendCol.document(pet.getIdPet()).set(pet).addOnSuccessListener(aVoid -> {
                                //TODO agregar Snackbar confirmando
                            });
                        }
                    });
                    snackbar.show();
                }
            });
        });

    }

    private void addedMsg(){
        chatCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                if (dc.getType() == DocumentChange.Type.ADDED) {
                    Message msgAdded = dc.getDocument().toObject(Message.class);
                    messageArrayList.add(msgAdded);
                }
            }
            sortArray(messageArrayList);
        });
    }

    private void sortArray(ArrayList<Message> messages){
        Collections.sort(messages, (o1, o2) -> o1.getId() - o2.getId());

        for (Message msg: messages) {
            if (!msgIdsShown.contains(msg.getId())){
                msgIdsShown.add(msg.getId());
                showMsg(msg);
            }
        }
    }

    private void showMsg(Message message){
        final String user = currentUser.getDisplayName();
        Log.d(TAG, "onCreateView: " + message + " " + userName + " " + time);

        if(message.getUser().equals(user)){
            addMessageBox(user,message.getMessage(),message.getTime(), 1);
        }
        else{
            addMessageBox(message.getUser(),message.getMessage(),message.getTime(), 2);
        }

    }

    private void addMessageBox(String user, String message, String time, int type){
        TextView textView = new TextView(context);
        String mensaje = time + ": " + message;
        textView.setText(mensaje);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }

        textView.setLayoutParams(lp2);
        layout.addView(textView);

        showBottom();
    }

    private void showBottom(){
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    @Override
    public int getFragmentTitle() {
        return R.string.chat;
    }

}
