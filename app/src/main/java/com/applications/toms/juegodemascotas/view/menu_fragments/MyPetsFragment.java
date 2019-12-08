package com.applications.toms.juegodemascotas.view.menu_fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PetController;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.FragmentTitles;
import com.applications.toms.juegodemascotas.view.adapter.MyPetsAdapter;
import com.applications.toms.juegodemascotas.view.fragment.AddPetFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyPetsFragment extends Fragment  implements MyPetsAdapter.AdapterInterface, FragmentTitles {

    public static final String TAG = "MyPetsFragment";

    private static String userFirestore;
    private static String myPetsFirestore;

    private static FirebaseUser currentUser;
    private static Context context;
    private static PetController petController;

    //Atributos
    private MyPetsAdapter myPetsAdapter;
    private MyPetsInterface myPetsInterface;

    public MyPetsFragment() {
        // Required empty public constructor
    }

    public void setMyPetsInterface(MyPetsInterface myPetsInterface) {
        this.myPetsInterface = myPetsInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_my_pets, container, false);

        //Get application context
        context = getContext();

        //Get Firebase instances
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //String text to get to an specific database
        userFirestore = getResources().getString(R.string.collection_users);
        myPetsFirestore = getResources().getString(R.string.collection_my_pets);

        //view FAB
        FloatingActionButton fabAddPet = view.findViewById(R.id.fabAddPet);

        //Adapter
        myPetsAdapter = new MyPetsAdapter(new ArrayList<>(),context,this);

        //Controller of Pet
        petController = new PetController();

        //Recycler View
        RecyclerView recyclerViewPets = view.findViewById(R.id.recyclerMyPets);
        recyclerViewPets.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerViewPets.setLayoutManager(llm);
        //adaptador
        recyclerViewPets.setAdapter(myPetsAdapter);

        refreshPets();

        //add pet btn FAB
        fabAddPet.setOnClickListener(v -> {
            //TODO making go to add PetFragment
            myPetsInterface.goToAddPet();
//            AddPetFragment addPetFragment = new AddPetFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.containerPets,addPetFragment);
//            fragmentTransaction.commit();
        });

        return view;
    }

    @Override
    public int getFragmentTitle() {
        return R.string.my_pets;
    }

    public interface MyPetsInterface{
        void goToAddPet();
    }

    @Override
    public void goToProfile(String idOwner, Pet pet) {
        //TODO Make go to profile (after making profile fragment)
    }

    //Refresh Recycler
    private void refreshPets(){
        petController.giveOwnerPets(currentUser.getUid(),context,resultado -> myPetsAdapter.setPetList(resultado));
    }
}
