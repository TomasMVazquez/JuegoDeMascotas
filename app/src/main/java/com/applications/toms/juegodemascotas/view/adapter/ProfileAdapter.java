package com.applications.toms.juegodemascotas.view.adapter;

import android.content.Context;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.model.Pet;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter{

    private FirebaseStorage mStorage;
    private static FirebaseUser currentUser;
    //Atributos
    private List<Pet> petList;
    private Context context;
    private ProfileAdapterInterface adapterInterface;

    public ProfileAdapter(List<Pet> petList, Context context, ProfileAdapterInterface adapterInterface) {
        this.petList = petList;
        this.context = context;
        this.adapterInterface = adapterInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public interface ProfileAdapterInterface{
        void goToPetProfile(String idPet);
    }
}
