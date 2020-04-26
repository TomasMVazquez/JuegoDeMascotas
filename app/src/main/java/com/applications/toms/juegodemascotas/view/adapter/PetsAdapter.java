package com.applications.toms.juegodemascotas.view.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.applications.toms.juegodemascotas.util.Keys;
import com.applications.toms.juegodemascotas.view.MessageActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PetsAdapter extends RecyclerView.Adapter {

    //Atributos
    private List<Pet> petList;
    private Context context;
    private PetsAdapterInterface adapterInterface;

    //constructor
    public PetsAdapter(List<Pet> petList, Context context, PetsAdapterInterface adapterInterface) {
        this.petList = petList;
        this.context = context;
        this.adapterInterface = adapterInterface;
    }

    //Setter
    public void setPetList(List<Pet> petList) {
        this.petList = petList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        //pasamos contexto a inflador
        LayoutInflater inflater = LayoutInflater.from(context);
        //inflamos view
        View view = inflater.inflate(R.layout.card_view_pets,viewGroup,false);
        //pasamos holder
        PetsViewHolder petViewHolder = new PetsViewHolder(view);

        return petViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        //buscamos datos
        Pet pet = petList.get(i);
        //casteamos
        PetsViewHolder petViewHolder = (PetsViewHolder) viewHolder;
        //cargamos
        petViewHolder.cargar(pet);
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public interface PetsAdapterInterface{
        void goToProfileFromPets(String idOwner, Pet pet);
        void addFriend(Pet pet);
    }


    public class PetsViewHolder extends RecyclerView.ViewHolder{

        //Atributos
        private ImageView ivCVPets;
        private ImageView chatCardView;
        private ImageView friendCardView;
        private TextView tvCVNamePets;
        private TextView tvCVIdPet;
        private TextView tvCVIdOwner;

        //constructor
        public PetsViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCVPets = itemView.findViewById(R.id.ivCVPets);
            tvCVNamePets = itemView.findViewById(R.id.tvCVNamePets);
            tvCVIdPet = itemView.findViewById(R.id.tvCVIdPet);
            tvCVIdOwner = itemView.findViewById(R.id.tvCVIdOwner);
            chatCardView = itemView.findViewById(R.id.chatCardView);
            friendCardView = itemView.findViewById(R.id.friendCardView);

            //Go to Profile on Click
            itemView.setOnClickListener(v -> {
                Pet pet = petList.get(getAdapterPosition());
                adapterInterface.goToProfileFromPets(tvCVIdOwner.getText().toString(), pet);
            });

            //Go to chat on Click
            chatCardView.setOnClickListener(v -> {
                Pet pet = petList.get(getAdapterPosition());
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra(Keys.KEY_MSG_USERID,pet.getMiDuenioId());
                context.startActivity(intent);
            });

            //Click on Add Firend Heart
            friendCardView.setOnClickListener(v -> {
                Pet pet = petList.get(getAdapterPosition());
                adapterInterface.addFriend(pet);
            });

        }

        //metodo cargar tarjeta
        public void cargar(Pet pet){
            tvCVNamePets.setText(pet.getNombre());
            tvCVIdPet.setText(pet.getIdPet());
            tvCVIdOwner.setText(pet.getMiDuenioId());

            if (pet.getFotoMascota().equals(context.getString(R.string.image_default))){
                ivCVPets.setImageResource(R.drawable.dog_48);
            }else {
                Glide.with(context).load(pet.getFotoMascota()).into(ivCVPets);
            }
        }

    }
}
