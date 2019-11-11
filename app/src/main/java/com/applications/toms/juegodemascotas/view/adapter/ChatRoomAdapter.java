package com.applications.toms.juegodemascotas.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.ChatController;
import com.applications.toms.juegodemascotas.controller.OwnerController;
import com.applications.toms.juegodemascotas.model.Chat;
import com.applications.toms.juegodemascotas.model.Message;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter {

    private static final String TAG = "ChatRoomAdapter";
    private static FirebaseUser currentUser;

    //Atributos
    private List<Chat> chats;
    private Context context;
    private AdapterInterfaceChatRoom adapterInterfaceChatRoom;

    //constructor
    public ChatRoomAdapter(List<Chat> chats, Context context, AdapterInterfaceChatRoom adapterInterfaceChatRoom) {
        this.chats = chats;
        this.context = context;
        this.adapterInterfaceChatRoom = adapterInterfaceChatRoom;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //pasamos contexto a inflador
        LayoutInflater inflater = LayoutInflater.from(context);
        //inflamos view
        View view = inflater.inflate(R.layout.card_view_chat,parent,false);
        //pasamos holder
        ChatRoomViewHolder chatRoomViewHolder = new ChatRoomViewHolder(view);

        return chatRoomViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //buscamos datos
        Chat chat = chats.get(position);
        //casteamos
        ChatRoomViewHolder chatRoomViewHolder = (ChatRoomViewHolder) holder;
        //cargamos
        chatRoomViewHolder.cargar(chat);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    //Interface
    public interface AdapterInterfaceChatRoom{
        void goToChat(String chatId);
    }

    //Metodos
    public class ChatRoomViewHolder extends RecyclerView.ViewHolder{

        //Atributos
        private TextView tvChatId;
        private CircularImageView ivCardViewUserToChat;
        private TextView tvChatName;
        private TextView tvLastChat;
        private TextView tvLastChatTime;

        //Constructor
        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);

            tvChatId = itemView.findViewById(R.id.tvChatId);
            ivCardViewUserToChat = itemView.findViewById(R.id.ivCardViewUserToChat);
            tvChatName = itemView.findViewById(R.id.tvChatName);
            tvLastChat = itemView.findViewById(R.id.tvLastChat);
            tvLastChatTime = itemView.findViewById(R.id.tvLastChatTime);

            itemView.setOnClickListener(v -> {
                adapterInterfaceChatRoom.goToChat(tvChatId.getText().toString());
            });

        }

        //Metodos
        public void cargar(Chat chat){
            tvChatId.setText(chat.getIdChat());
            Log.d(TAG, "cargar: id chat = " + chat.getIdChat());
            OwnerController ownerController = new OwnerController();

            if (currentUser.getUid().equals(chat.getUserOne())){
                Log.d(TAG, "cargar: user 2 = " + chat.getUserTwo());
                ownerController.giveOwnerData(chat.getUserTwo(), context, result -> {
                    Log.d(TAG, "cargar: result = " + result);
                    tvChatName.setText(result.getName());
                    if (result.getAvatar() == null){
                        Glide.with(context).load(context.getDrawable(R.drawable.shadow_profile)).into(ivCardViewUserToChat);
                    }else {
                        String [] avatar = result.getAvatar().split("=");
                        if (avatar.length > 1){
                            Glide.with(context).load(result.getAvatar()).into(ivCardViewUserToChat);
                        }else {
                            ownerController.giveOwnerAvatar(result.getUserId(),result.getAvatar(),context, results -> Glide.with(context).load(results).into(ivCardViewUserToChat));
                        }
                    }
                });
            }else {
                Log.d(TAG, "cargar: user 1 = " + chat.getUserOne());
                ownerController.giveOwnerData(chat.getUserOne(), context, result -> {
                    Log.d(TAG, "cargar: result = " + result);
                    tvChatName.setText(result.getName());
                    if (result.getAvatar() == null){
                        Glide.with(context).load(context.getDrawable(R.drawable.shadow_profile)).into(ivCardViewUserToChat);
                    }else {
                        String [] avatar = result.getAvatar().split("=");
                        if (avatar.length > 1){
                            Glide.with(context).load(result.getAvatar()).into(ivCardViewUserToChat);
                        }else {
                            ownerController.giveOwnerAvatar(result.getUserId(),result.getAvatar(),context, results -> Glide.with(context).load(results).into(ivCardViewUserToChat));
                        }
                    }
                });
            }

            ChatController chatController = new ChatController();
            chatController.giveLastMessage(chat.getIdChat(),context,result -> {
                Message lastMessage = result.get(0);
                tvLastChat.setText(lastMessage.getMessage());
                tvLastChatTime.setText(lastMessage.getTime());
            });
        }

    }
}
