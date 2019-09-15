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
import com.applications.toms.juegodemascotas.model.Owner;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CirculeOwnerAdapter extends RecyclerView.Adapter {

    private FirebaseStorage mStorage;
    private static FirebaseUser currentUser;
    //Atributos
    private List<Owner> owner;
    private Context context;
    private AdapterInterfaceCirculeOwner adapterInterfaceCirculeOwner;

    public CirculeOwnerAdapter(List<Owner> owner, Context context, AdapterInterfaceCirculeOwner adapterInterfaceCirculeOwner) {
        this.owner = owner;
        this.context = context;
        this.adapterInterfaceCirculeOwner = adapterInterfaceCirculeOwner;
    }

    public void setOwner(List<Owner> owner) {
        this.owner = owner;
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
        Owner newOwner = owner.get(i);
        //casteamos
        CirculeOwnerViewHolder ownerViewHolder = (CirculeOwnerViewHolder) viewHolder;
        //cargamos
        ownerViewHolder.cargar(newOwner);
    }

    @Override
    public int getItemCount() {
        return owner.size();
    }

    public interface AdapterInterfaceCirculeOwner{
        void goToUserProfile(String idOwner);
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
            tvPetId = itemView.findViewById(R.id.tvPetId); //TODO SACAR SI FUNCIONA

            itemView.setOnClickListener(v ->
                    adapterInterfaceCirculeOwner.goToUserProfile(tvUid.getText().toString())
            );

        }

        //metodos
        public void cargar(final Owner owner){
            tvUid.setText(owner.getUserId());
            tvPetId.setText("");
            if (owner.getAvatar().equals("")){
                Glide.with(context).load(context.getDrawable(R.drawable.shadow_profile)).into(ivCardViewProfile);
            }else {
                StorageReference storageReference = mStorage.getReference().child(owner.getUserId()).child(owner.getAvatar());
                storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> Glide.with(context).load(uri).into(ivCardViewProfile))
                        .addOnFailureListener(e -> Glide.with(context).load(owner.getAvatar()).into(ivCardViewProfile));
            }
        }
    }

}
