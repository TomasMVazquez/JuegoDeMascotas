package com.applications.toms.juegodemascotas.view.menu_fragments;


import android.app.Activity;
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

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.shape.CircleShape;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyPetsFragment extends Fragment  implements MyPetsAdapter.AdapterInterface, FragmentTitles {

    public static final String TAG = "MyPetsFragment";
    private static final String SHOWCASE_ID = "simple my pets";


    private static String userFirestore;
    private static String myPetsFirestore;

    private static FirebaseUser currentUser;
    private static Activity activity;
    private static Context context;
    private static PetController petController;
    private FloatingActionButton fabAddPet;

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
        activity = getActivity();

        //Get Firebase instances
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //String text to get to an specific database
        userFirestore = getResources().getString(R.string.collection_users);
        myPetsFirestore = getResources().getString(R.string.collection_my_pets);

        //view FAB
        fabAddPet = view.findViewById(R.id.fabAddPet);

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
            myPetsInterface.goToAddPet();
        });

        presentShowcaseView(800);

        return view;
    }

    @Override
    public int getFragmentTitle() {
        return R.string.my_pets;
    }

    public interface MyPetsInterface{
        void goToAddPet();
        void petSelectedListener(String idOwner, String petId);
    }

    @Override
    public void goToProfile(String idOwner, String petId) {
        myPetsInterface.petSelectedListener(idOwner,petId);
    }

    //Refresh Recycler
    private void refreshPets(){
        petController.giveOwnerPets(currentUser.getUid(),context,resultado -> myPetsAdapter.setPetList(resultado));
    }

    private void presentShowcaseView(int withDelay) {
        new MaterialShowcaseView.Builder(activity)
                .setTarget(fabAddPet)
                .setShape(new CircleShape())
                .setDismissText(context.getString(R.string.onboard_click))
                .setContentText(context.getString(R.string.onboard_mypets_fab))
                .setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .useFadeAnimation() // remove comment if you want to use fade animations for Lollipop & up
                .show();
    }
}
