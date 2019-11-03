package com.applications.toms.juegodemascotas.view;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PetController;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.view.adapter.MyPetsAdapter;
import com.applications.toms.juegodemascotas.view.fragment.AddPetFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyPetsActivity extends AppCompatActivity implements MyPetsAdapter.AdapterInterface {

    public static final String KEY_DUENIO_ID = "duenio";

    private static String userFirestore;
    private static String myPetsFirestore;

    private static FirebaseUser currentUser;
    private static Context context;
    private static PetController petController;

    //Atributos
    private static MyPetsAdapter myPetsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pets);

        //Get application context
        context = getApplicationContext();

        //Get Firebase instances
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //String text to get to an specific database
        userFirestore = getResources().getString(R.string.collection_users);
        myPetsFirestore = getResources().getString(R.string.collection_my_pets);

        //view FAB
        FloatingActionButton fabAddPet = findViewById(R.id.fabAddPet);

        //Intent and bundle
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //Adapter
        myPetsAdapter = new MyPetsAdapter(new ArrayList<>(),this,this);

        //Controller of Pet
        petController = new PetController();

        //Recycler View
        RecyclerView recyclerViewPets = findViewById(R.id.recyclerMyPets);
        recyclerViewPets.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewPets.setLayoutManager(llm);
        //adaptador
        recyclerViewPets.setAdapter(myPetsAdapter);

        refreshPets();

        //add pet btn FAB
        fabAddPet.setOnClickListener(v -> {
            AddPetFragment addPetFragment = new AddPetFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerPets,addPetFragment);
            fragmentTransaction.commit();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //Refresh Recycler
    private static void refreshPets(){
        petController.giveOwnerPets(currentUser.getUid(),context,resultado -> myPetsAdapter.setPetList(resultado));
    }

    //Go to pet profile when clicking the card
    @Override
    public void goToProfile(String idOwner, Pet petProfile) {
        Intent intent = new Intent(MyPetsActivity.this,ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ProfileActivity.KEY_TYPE,"2");
        bundle.putString(ProfileActivity.KEY_USER_ID,idOwner);
        bundle.putString(ProfileActivity.KEY_PET_ID, petProfile.getIdPet());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //Add pet to database
    public static void addPetToDataBase(final String name, final String raza, final String size, final String birth, final String sex, final String photo, final String info){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference userRefMasc = db.collection(userFirestore)
                .document(currentUser.getUid()).collection(myPetsFirestore);

        final String idPet = userRefMasc.document().getId();
        Pet newPet = new Pet(idPet,name,raza,size,sex,birth,photo,info,currentUser.getUid());

        userRefMasc.document(idPet).set(newPet)
                .addOnSuccessListener(aVoid -> {
                    //TODO
                })
                .addOnFailureListener(e -> {
                    //TODO
                });

        DocumentReference petsRef = db.collection(context.getResources().getString(R.string.collection_pets))
                .document(idPet);

        petsRef.set(newPet).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                //TODO
            }else{
                //TODO
            }
        });
    }

}
