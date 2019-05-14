package com.applications.toms.juegodemascotas.view.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Mascota;
import com.applications.toms.juegodemascotas.view.MyPetsActivity;
import com.applications.toms.juegodemascotas.view.ProfileActivity;
import com.applications.toms.juegodemascotas.view.adapter.MyPetsAdapter;
import com.applications.toms.juegodemascotas.view.adapter.PetsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class MascotasFragment extends Fragment implements PetsAdapter.PetsAdapterInterface {

    private static FirebaseFirestore db;
    private static String petsFirestore;
    private static Context context;

    //Atributos
    private static PetsAdapter petsAdapter;

    public MascotasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mascotas, container, false);
        context = getApplicationContext();
        db = FirebaseFirestore.getInstance();
        petsFirestore = getResources().getString(R.string.collection_pets);

        petsAdapter = new PetsAdapter(new ArrayList<Mascota>(),context,this);

        //Traigo Mascotas Duenio
        final CollectionReference petsRef = db.collection(petsFirestore);

        petsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<Mascota> misMascotas = new ArrayList<>();
                misMascotas.addAll(queryDocumentSnapshots.toObjects(Mascota.class));
                petsAdapter.setMascotaList(misMascotas);
            }
        });


        //Recycler View
        RecyclerView recyclerViewPets = view.findViewById(R.id.recyclerPets);
        recyclerViewPets.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerViewPets.setLayoutManager(llm);
        //adaptador
        recyclerViewPets.setAdapter(petsAdapter);

        return view;
    }

    @Override
    public void goToProfileFromPets(String idOwner, Mascota mascota) {
        Intent intent = new Intent(context, ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ProfileActivity.KEY_TYPE,"3");
        bundle.putString(ProfileActivity.KEY_USER_ID,idOwner);
        bundle.putString(ProfileActivity.KEY_PET_ID,mascota.getIdPet());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
