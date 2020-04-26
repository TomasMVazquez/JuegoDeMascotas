package com.applications.toms.juegodemascotas.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PetController;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.Keys;
import com.applications.toms.juegodemascotas.view.MessageActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter {

    //Atributos
    private List<Pet> petList;
    private Context context;
    private FriendAdapterInterface friendAdapterInterface;

    //Constructor
    public FriendsAdapter(List<Pet> petList, Context context, FriendAdapterInterface friendAdapterInterface) {
        this.petList = petList;
        this.context = context;
        this.friendAdapterInterface = friendAdapterInterface;
    }

    //Setter
    public void setPetList(List<Pet> petList) {
        this.petList = petList;
        notifyDataSetChanged();
    }

    //Methods Recycler Adapter
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        //pasamos contexto a inflador
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflamos view
        View view = inflater.inflate(R.layout.card_view_friend,parent,false);

        //holder
        FriendViewHolder friendViewHolder = new FriendViewHolder(view);

        return friendViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //buscamos datos
        Pet pet = petList.get(position);
        //casteamos
        FriendViewHolder friendViewHolder = (FriendViewHolder) holder;
        //cargamos
        friendViewHolder.cargar(pet);
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public interface FriendAdapterInterface{
        void update(int index);
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder{

        //Atributos
        private CircularImageView ivCVFriend;
        private TextView tvCVNameFriend;
        private ImageView chatCardViewFriend;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCVFriend = itemView.findViewById(R.id.ivCVFriend);
            tvCVNameFriend = itemView.findViewById(R.id.tvCVNameFriend);
            chatCardViewFriend = itemView.findViewById(R.id.chatCardViewFriend);

            chatCardViewFriend.setOnClickListener(v -> {
                Pet pet = petList.get(getAdapterPosition());
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra(Keys.KEY_MSG_USERID,pet.getMiDuenioId());
                context.startActivity(intent);
            });

            itemView.setOnLongClickListener(v -> {
                confirmDialogDemo(petList.get(getAdapterPosition()),getAdapterPosition());
                return false;
            });

        }

        private void cargar(Pet pet){
            tvCVNameFriend.setText(pet.getNombre());
            if (pet.getFotoMascota().equals(context.getString(R.string.image_default))){
                ivCVFriend.setImageResource(R.drawable.dog_48);
            }else {
                Glide.with(context).load(pet.getFotoMascota()).into(ivCVFriend);
            }

        }

        private void confirmDialogDemo(Pet pet, int index) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.dialog_confirm_title));
            builder.setMessage(context.getString(R.string.dialog_confirm_text_delete_friend));
            builder.setCancelable(false);
            builder.setIcon(context.getDrawable(R.drawable.juego_mascota));

            builder.setPositiveButton(context.getString(R.string.dialog_confirm_accept), (dialog, which) ->{
                //Create on the current user a document with firend list
                CollectionReference myFriendCol = FirebaseFirestore.getInstance().collection(context.getString(R.string.collection_users))
                        .document(FirebaseAuth.getInstance().getUid()).collection(context.getString(R.string.collection_my_friends));

                myFriendCol.document(pet.getIdPet()).delete();
                notifyDataSetChanged();
                friendAdapterInterface.update(index);
            });

            builder.setNegativeButton(context.getString(R.string.dialog_confirm_cancel), (dialog, which) -> dialog.cancel());

            builder.show();
        }
    }

}
