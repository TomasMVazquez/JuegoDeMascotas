package com.applications.toms.juegodemascotas.view.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PetController;
import com.applications.toms.juegodemascotas.model.Pet;
import com.bumptech.glide.Glide;
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
        void goToChat(String userToChat);
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
                friendAdapterInterface.goToChat(pet.getMiDuenioId());
            });

            itemView.setOnLongClickListener(v -> {
                //TODO on long click option to delete from list of friends
                return false;
            });

        }

        public void cargar(Pet pet){
            tvCVNameFriend.setText(pet.getNombre());

            PetController petController = new PetController();
            petController.givePetAvatar(pet.getMiDuenioId(),pet.getFotoMascota(),context,result -> Glide.with(context).load(result).into(ivCVFriend));

        }
    }

}
