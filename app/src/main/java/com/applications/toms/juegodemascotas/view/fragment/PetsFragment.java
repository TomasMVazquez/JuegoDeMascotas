package com.applications.toms.juegodemascotas.view.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Mascota;
import com.applications.toms.juegodemascotas.view.ProfileActivity;
import com.applications.toms.juegodemascotas.view.adapter.PetsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
public class PetsFragment extends Fragment implements PetsAdapter.PetsAdapterInterface {

    private static FirebaseFirestore db;
    private static String petsFirestore;
    private static Context context;
    private static FirebaseUser currentUser;

    //Atributos
    private static PetsAdapter petsAdapter;

    public PetsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pets, container, false);
        View viewNologin = inflater.inflate(R.layout.fragment_no_login, container, false);

        context = getApplicationContext();
        petsFirestore = getResources().getString(R.string.collection_pets);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){
            db = FirebaseFirestore.getInstance();

            petsAdapter = new PetsAdapter(new ArrayList<Mascota>(),context,this);

            //Traigo Mascotas Duenio
            final CollectionReference petsRef = db.collection(petsFirestore);

            petsRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
                List<Mascota> misMascotas = new ArrayList<>();
                misMascotas.addAll(queryDocumentSnapshots.toObjects(Mascota.class));
                petsAdapter.setMascotaList(misMascotas);
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
        }else {

            return viewNologin;
        }
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
