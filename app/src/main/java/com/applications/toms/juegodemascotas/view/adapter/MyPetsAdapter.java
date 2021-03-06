package com.applications.toms.juegodemascotas.view.adapter;

import android.content.Context;
import android.net.Uri;

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

import java.util.List;

public class MyPetsAdapter extends RecyclerView.Adapter {

    //Atributos
    private List<Pet> petList;
    private Context context;
    private AdapterInterface adapterInterface;

    //Constructor
    public MyPetsAdapter(List<Pet> petList, Context context, AdapterInterface adapterInterface) {
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
        View view = inflater.inflate(R.layout.card_view_my_pet,viewGroup,false);
        //pasamos holder

        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        //buscamos datos
        Pet pet = petList.get(i);
        //casteamos
        PetViewHolder petViewHolder = (PetViewHolder) viewHolder;
        //cargamos
        petViewHolder.cargar(pet);
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public interface AdapterInterface{
        void goToProfile(String idOwner, String petId);
    }

    private class PetViewHolder extends RecyclerView.ViewHolder{

        //Atributos
        private ImageView ivCVMyPet;
        private TextView tvCVNameMyPet;
        private TextView tvCVIdMyPet;
        private TextView tvCVIdMyOwner;

        //constructor
        private PetViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCVMyPet = itemView.findViewById(R.id.ivCVMyPet);
            tvCVNameMyPet = itemView.findViewById(R.id.tvCVNameMyPet);
            tvCVIdMyPet = itemView.findViewById(R.id.tvCVIdMyPet);
            tvCVIdMyOwner = itemView.findViewById(R.id.tvCVIdMyOwner);

            itemView.setOnClickListener(v -> {
                Pet petProfile = petList.get(getAdapterPosition());
                adapterInterface.goToProfile(tvCVIdMyOwner.getText().toString(), petProfile.getIdPet());
            });

        }

        //metodo cargar tarjeta
        private void cargar(Pet pet){
            tvCVNameMyPet.setText(pet.getName());
            tvCVIdMyPet.setText(pet.getIdPet());
            tvCVIdMyOwner.setText(pet.getOwnerId());

            if (pet.getPhoto().equals(context.getString(R.string.image_default))){
                ivCVMyPet.setImageResource(R.drawable.dog_48);
            }else {
                Glide.with(context).load(pet.getPhoto()).into(ivCVMyPet);
            }
        }

    }
}
