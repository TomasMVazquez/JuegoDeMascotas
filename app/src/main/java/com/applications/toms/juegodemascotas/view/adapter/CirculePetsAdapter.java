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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CirculePetsAdapter extends RecyclerView.Adapter {

    private FirebaseStorage mStorage;
    private static FirebaseUser currentUser;
    //Atributos
    private List<Mascota> mascotaList;
    private Context context;
    private AdapterInterfaceCircule adapterInterfaceCircule;

    //constructor
    public CirculePetsAdapter(List<Mascota> mascotaList, Context context, AdapterInterfaceCircule adapterInterfaceCircule) {
        this.mascotaList = mascotaList;
        this.context = context;
        this.adapterInterfaceCircule = adapterInterfaceCircule;
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
        View view = inflater.inflate(R.layout.card_view_profile,viewGroup,false);
        //pasamos holder
        CirculePetViewHolder petViewHolder = new CirculePetViewHolder(view);

        return petViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        //buscamos datos
        Mascota mascota = mascotaList.get(i);
        //casteamos
        CirculePetViewHolder petViewHolder = (CirculePetViewHolder) viewHolder;
        //cargamos
        petViewHolder.cargar(mascota);
    }

    @Override
    public int getItemCount() {
        return mascotaList.size();
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
        public void cargar(Mascota mascota){
            tvUid.setText(mascota.getMiDuenioId());
            tvPetId.setText(mascota.getIdPet());

            StorageReference storageReference = mStorage.getReference().child(mascota.getMiDuenioId()).child(mascota.getFotoMascota());
            storageReference.getDownloadUrl()
                    .addOnSuccessListener(uri -> Glide.with(context).load(uri).into(ivCardViewProfile))
                    .addOnFailureListener(e -> Glide.with(context).load(context.getDrawable(R.drawable.shadow_dog)).into(ivCardViewProfile));
        }

    }

}
