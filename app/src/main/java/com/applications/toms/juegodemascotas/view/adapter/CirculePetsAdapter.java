package com.applications.toms.juegodemascotas.view.adapter;

import android.content.Context;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CirculePetsAdapter extends RecyclerView.Adapter {

    //Atributos
    private List<Pet> petList;
    private Context context;
    private AdapterInterfaceCircule adapterInterfaceCircule;

    //constructor
    public CirculePetsAdapter(List<Pet> petList, Context context, AdapterInterfaceCircule adapterInterfaceCircule) {
        this.petList = petList;
        this.context = context;
        this.adapterInterfaceCircule = adapterInterfaceCircule;
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
        View view = inflater.inflate(R.layout.card_view_profile,viewGroup,false);
        //pasamos holder
        CirculePetViewHolder petViewHolder = new CirculePetViewHolder(view);

        return petViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        //buscamos datos
        Pet pet = petList.get(i);
        //casteamos
        CirculePetViewHolder petViewHolder = (CirculePetViewHolder) viewHolder;
        //cargamos
        petViewHolder.cargar(pet);
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public interface AdapterInterfaceCircule{
        void goToPetProfile(String idOwner,String idPet);
    }

    public class CirculePetViewHolder extends RecyclerView.ViewHolder{

        //Atributos
        private ImageView ivCardViewProfile;
        private TextView tvUid;
        private TextView tvPetId;

        //Constructor
        public CirculePetViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCardViewProfile = itemView.findViewById(R.id.ivCardViewProfile);
            tvUid = itemView.findViewById(R.id.tvUid);
            tvPetId = itemView.findViewById(R.id.tvPetId);

            itemView.setOnClickListener(v ->
                    adapterInterfaceCircule.goToPetProfile(tvUid.getText().toString(),tvPetId.getText().toString())
            );

        }

        //metodos
        public void cargar(Pet pet){
            tvUid.setText(pet.getMiDuenioId());
            tvPetId.setText(pet.getIdPet());
            PetController petController = new PetController();
            petController.givePetAvatar(pet.getMiDuenioId(),pet.getFotoMascota(),context,result -> Glide.with(context).load(result).into(ivCardViewProfile));
        }

    }

}
