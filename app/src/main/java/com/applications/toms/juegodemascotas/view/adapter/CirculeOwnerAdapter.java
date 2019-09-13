package com.applications.toms.juegodemascotas.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Duenio;
import com.applications.toms.juegodemascotas.model.Mascota;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CirculeOwnerAdapter extends RecyclerView.Adapter {

    private FirebaseStorage mStorage;
    private static FirebaseUser currentUser;
    //Atributos
    private List<Duenio> duenio;
    private Context context;
    private AdapterInterfaceCirculeOwner adapterInterfaceCirculeOwner;

    public CirculeOwnerAdapter(List<Duenio> duenio, Context context, AdapterInterfaceCirculeOwner adapterInterfaceCirculeOwner) {
        this.duenio = duenio;
        this.context = context;
        this.adapterInterfaceCirculeOwner = adapterInterfaceCirculeOwner;
    }

    public void setDuenio(List<Duenio> duenio) {
        this.duenio = duenio;
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
        CirculeOwnerViewHolder ownerViewHolder = new CirculeOwnerViewHolder(view);

        return ownerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        //buscamos datos
        Duenio newDuenio = duenio.get(i);
        //casteamos
        CirculeOwnerViewHolder ownerViewHolder = (CirculeOwnerViewHolder) viewHolder;
        //cargamos
        ownerViewHolder.cargar(newDuenio);
    }

    @Override
    public int getItemCount() {
        return duenio.size();
    }

    public interface AdapterInterfaceCirculeOwner{
        void goToProfile(String idOwner,String idPet);
    }

    public class CirculeOwnerViewHolder extends RecyclerView.ViewHolder{

        //Atributos
        private ImageView ivCardViewProfile;
        private TextView tvUid;
        private TextView tvPetId;

        //Constructor
        public CirculeOwnerViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCardViewProfile = itemView.findViewById(R.id.ivCardViewProfile);
            tvUid = itemView.findViewById(R.id.tvUid);
            tvPetId = itemView.findViewById(R.id.tvPetId);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterInterfaceCirculeOwner.goToProfile(tvUid.getText().toString(),tvPetId.getText().toString());
                }
            });

        }

        //metodos
        public void cargar(final Duenio duenio){
            tvUid.setText(duenio.getUserId());
            tvPetId.setText("");
            if (duenio.getFotoDuenio().equals("")){
                Glide.with(context).load(context.getDrawable(R.drawable.shadow_profile)).into(ivCardViewProfile);
            }else {
                StorageReference storageReference = mStorage.getReference().child(duenio.getUserId()).child(duenio.getFotoDuenio());
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(ivCardViewProfile)).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Glide.with(context).load(duenio.getFotoDuenio()).into(ivCardViewProfile);
                    }
                });
            }
        }
    }

}
