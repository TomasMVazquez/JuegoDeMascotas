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
import com.applications.toms.juegodemascotas.model.Mascota;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PetsAdapter extends RecyclerView.Adapter {

    private FirebaseStorage mStorage;

    //Atributos
    private List<Mascota> mascotaList;
    private Context context;
    private PetsAdapterInterface adapterInterface;

    //constructor
    public PetsAdapter(List<Mascota> mascotaList, Context context, PetsAdapterInterface adapterInterface) {
        this.mascotaList = mascotaList;
        this.context = context;
        this.adapterInterface = adapterInterface;
    }

    //Setter
    public void setMascotaList(List<Mascota> mascotaList) {
        this.mascotaList = mascotaList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mStorage = FirebaseStorage.getInstance();
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
        Mascota mascota = mascotaList.get(i);
        //casteamos
        PetsViewHolder petViewHolder = (PetsViewHolder) viewHolder;
        //cargamos
        petViewHolder.cargar(mascota);
    }

    @Override
    public int getItemCount() {
        return mascotaList.size();
    }

    public interface PetsAdapterInterface{
        void goToProfileFromPets(String idOwner, Mascota mascota); //TODO REVISAR
    }


    public class PetsViewHolder extends RecyclerView.ViewHolder{

        //Atributos
        private ImageView ivCVPets;
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

            itemView.setOnClickListener(v -> {
                Mascota mascotaProfile = mascotaList.get(getAdapterPosition());
                adapterInterface.goToProfileFromPets(tvCVIdOwner.getText().toString(), mascotaProfile);
            });

        }

        //metodo cargar tarjeta
        public void cargar(Mascota mascota){
            tvCVNamePets.setText(mascota.getNombre());
            tvCVIdPet.setText(mascota.getIdPet());
            tvCVIdOwner.setText(mascota.getMiDuenioId());

            StorageReference storageReference = mStorage.getReference().child(mascota.getMiDuenioId()).child(mascota.getFotoMascota());
            storageReference.getDownloadUrl()
                    .addOnSuccessListener(uri -> Glide.with(context).load(uri).into(ivCVPets))
                    .addOnFailureListener(e -> Glide.with(context).load(context.getDrawable(R.drawable.shadow_dog)).into(ivCVPets));
        }

    }
}
