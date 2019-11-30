package com.applications.toms.juegodemascotas.view.menu_fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.ChatController;
import com.applications.toms.juegodemascotas.view.adapter.ChatRoomAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomFragment extends Fragment implements ChatRoomAdapter.AdapterInterfaceChatRoom {

    public static final String TAG = "ChatRoomFragment";
    //Atributos
    private ChatRoomAdapter chatRoomAdapter;

    public ChatRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        chatRoomAdapter = new ChatRoomAdapter(new ArrayList<>(),getContext(),this);

        ChatController chatController = new ChatController();

        //Recycler View
        RecyclerView recyclerViewPets = view.findViewById(R.id.recyclerChats);
        recyclerViewPets.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerViewPets.setLayoutManager(llm);
        //adaptador
        recyclerViewPets.setAdapter(chatRoomAdapter);

        chatController.giveMyChatsList(currentUser.getUid(), getContext(), result -> {
            Log.d(TAG, "onCreateView: chat list = " + result);
            chatRoomAdapter.setChats(result);
        });

        return view;
    }


    @Override
    public void goToChat(String chatId) {
        onChatRoomNotify chatRoomAdapter = (onChatRoomNotify) getContext();
        chatRoomAdapter.enterChat(chatId);
        getActivity().getSupportFragmentManager().beginTransaction().remove(ChatRoomFragment.this).commit();
    }

    public interface onChatRoomNotify{
        public void enterChat(String chatId);
    }

}
