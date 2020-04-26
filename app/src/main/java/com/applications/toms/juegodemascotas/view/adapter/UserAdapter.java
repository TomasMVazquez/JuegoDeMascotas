package com.applications.toms.juegodemascotas.NewChat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.NewChat.model.Chat;
import com.applications.toms.juegodemascotas.NewChat.model.Message;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.util.Keys;
import com.applications.toms.juegodemascotas.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private static final String TAG = "TOM-UserAdapter";

    private Context mContext;
    private List<Owner> mUsers;
    private boolean ischat;

    private String theLastMessage;
    private String theLastMessageTime;

    public UserAdapter(Context mContext, List<Owner> mUsers, boolean ischat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.ischat = ischat;
    }

    public void setmUsers(List<Owner> mUsers) {
        this.mUsers = mUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Owner user = mUsers.get(position);
        holder.username.setText(user.getName());
        if (user.getAvatar().equals(mContext.getString(R.string.image_default))){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext.getApplicationContext()).load(user.getAvatar()).into(holder.profile_image);
        }

        if (ischat){
            lastMessage(user.getUserId(), holder.last_msg,holder.last_msg_time, holder.alert);
        }else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat){
            if (user.getStatus().equals(mContext.getString(R.string.status_on))){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        //Al clickear en el item del usuario ir al chat
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra(Keys.KEY_MSG_USERID,user.getUserId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        private ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private ImageView alert;
        private TextView last_msg;
        private TextView last_msg_time;

        public ViewHolder (View itemview){
            super(itemview);

            username = itemview.findViewById(R.id.username);
            profile_image = itemview.findViewById(R.id.profile_image);
            img_on = itemview.findViewById(R.id.img_on);
            img_off = itemview.findViewById(R.id.img_off);
            alert = itemview.findViewById(R.id.alert);
            last_msg = itemview.findViewById(R.id.last_msg);
            last_msg_time = itemview.findViewById(R.id.last_msg_time);

        }

    }

    //Revisar el último mensaje recibido/enviado
    private void lastMessage(final String userid, final TextView last_msg,final TextView last_msg_time, final ImageView alert){
        theLastMessage = "";
        theLastMessageTime = "";

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final FirebaseFirestore reference = FirebaseFirestore.getInstance();

        reference.collection(Keys.KEY_CHATS).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                String idChat = null;
                for (QueryDocumentSnapshot snapshots: queryDocumentSnapshots) {
                    Chat chat = snapshots.toObject(Chat.class);
                    if (chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(userid) ||
                            chat.getSender().equals(userid) && chat.getReceiver().equals(firebaseUser.getUid())) {
                        idChat = snapshots.getId();
                        break;
                    }
                }
                if (idChat!=null) {
                    reference.collection(Keys.KEY_CHATS).document(idChat).collection(Keys.KEY_MESSAGES)
                            .orderBy(Keys.KEY_MESSAGES_ID, Query.Direction.DESCENDING)
                            .limit(1)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (queryDocumentSnapshots.getDocuments().size() > 0) {
                                Message msg = queryDocumentSnapshots.getDocuments().get(0).toObject(Message.class);
                                theLastMessage = msg.getMessage();
                                theLastMessageTime = msg.getTime();
                                if (!msg.isIsseen() && msg.getReceiver().equals(firebaseUser.getUid())){
                                    alert.setVisibility(View.VISIBLE);
                                }else {
                                    alert.setVisibility(View.GONE);
                                }
                            }else {
                                theLastMessage = mContext.getString(R.string.no_msg);
                            }
                            last_msg.setText(theLastMessage);
                            last_msg_time.setText(theLastMessageTime);
                        }
                    });
                }else {
                    theLastMessage = mContext.getString(R.string.no_msg);
                    last_msg.setText(theLastMessage);
                }

                last_msg.setText(theLastMessage);

            }
        });
    }


}
