package com.applications.toms.juegodemascotas.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Mascota;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MyPetsAdapter extends RecyclerView.Adapter {

    private FirebaseStorage mStorage;
    private static FirebaseUser currentUser;
    //Atributos
    private List<Mascota> mascotaList;
    private Context context;
    private AdapterInterface adapterInterface;

    //Constructor
    public MyPetsAdapter(List<Mascota> mascotaList, Context context, AdapterInterface adapterInterface) {
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        context = viewGroup.getContext();
        //pasamos contexto a inflador
        LayoutInflater inflater = LayoutInflater.from(context);
        //inflamos view
        View view = inflater.inflate(R.layout.card_view_my_pet,viewGroup,false);
        //pasamos holder
        PetViewHolder petViewHolder = new PetViewHolder(view);

        return petViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        //buscamos datos
        Mascota mascota = mascotaList.get(i);
        //casteamos
        PetViewHolder petViewHolder = (PetViewHolder) viewHolder;
        //cargamos
        petViewHolder.cargar(mascota);
    }

    @Override
    public int getItemCount() {
        return mascotaList.size();
    }

    public interface AdapterInterface{
        void goToProfile(String idOwner, Mascota mascota);
    }

    public class PetViewHolder extends RecyclerView.ViewHolder{

        //Atributos
        private ImageView ivCVMyPet;
        private TextView tvCVNameMyPet;
        private TextView tvCVIdMyPet;
        private TextView tvCVIdMyOwner;

        //constructor
        public PetViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCVMyPet = itemView.findViewById(R.id.ivCVMyPet);
            tvCVNameMyPet = itemView.findViewById(R.id.tvCVNameMyPet);
            tvCVIdMyPet = itemView.findViewById(R.id.tvCVIdMyPet);
            tvCVIdMyOwner = itemView.findViewById(R.id.tvCVIdMyOwner);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Mascota mascotaProfile = mascotaList.get(getAdapterPosition());
                    adapterInterface.goToProfile(tvCVIdMyOwner.getText().toString(), mascotaProfile);
                }
            });

        }

        //metodo cargar tarjeta
        public void cargar(Mascota mascota){
            tvCVNameMyPet.setText(mascota.getNombre());
            tvCVIdMyPet.setText(mascota.getIdPet());
            tvCVIdMyOwner.setText(currentUser.getUid());

            StorageReference storageReference = mStorage.getReference().child(currentUser.getUid()).child(mascota.getFotoMascota());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(ivCVMyPet);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Glide.with(context).load(context.getDrawable(R.drawable.shadow_dog)).into(ivCVMyPet);
                }
            });
        }

    }
}
