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
import com.applications.toms.juegodemascotas.controller.OwnerController;
import com.applications.toms.juegodemascotas.model.Owner;
import com.bumptech.glide.Glide;

import java.util.List;

public class CirculeOwnerAdapter extends RecyclerView.Adapter {

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

        context = viewGroup.getContext();
        //pasamos contexto a inflador
        LayoutInflater inflater = LayoutInflater.from(context);
        //inflamos view
        View view = inflater.inflate(R.layout.card_view_profile,viewGroup,false);
        //pasamos holder

        return new CirculeOwnerViewHolder(view);
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
        void goToUserProfile(String keyType, String idOwner, String idPet);
    }

    private class CirculeOwnerViewHolder extends RecyclerView.ViewHolder{

        //Atributos
        private ImageView ivCardViewProfile;
        private TextView tvUid;
        private TextView tvPetId;

        //Constructor
        private CirculeOwnerViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCardViewProfile = itemView.findViewById(R.id.ivCardViewProfile);
            tvUid = itemView.findViewById(R.id.tvUid);
            tvPetId = itemView.findViewById(R.id.tvPetId);

            itemView.setOnClickListener(v ->
                    adapterInterfaceCirculeOwner.goToUserProfile("1",tvUid.getText().toString(),null)
            );

        }

        //metodos
        private void cargar(final Owner owner){
            tvUid.setText(owner.getUserId());
            tvPetId.setText("");
            if (owner.getAvatar().equals(context.getString(R.string.image_default))){
                Glide.with(context).load(context.getDrawable(R.drawable.shadow_profile)).into(ivCardViewProfile);
            }else {
                Glide.with(context).load(owner.getAvatar()).into(ivCardViewProfile);
            }
        }
    }

}
