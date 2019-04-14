package com.applications.toms.juegodemascotas.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PetsFromOwnerController;
import com.applications.toms.juegodemascotas.model.Duenio;
import com.applications.toms.juegodemascotas.model.Mascota;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.view.adapter.MyPetsAdapter;
import com.applications.toms.juegodemascotas.view.fragment.AddPetFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class MyPetsActivity extends AppCompatActivity implements MyPetsAdapter.AdapterInterface {

    public static final String KEY_DUENIO_ID = "duenio";

    private FirebaseDatabase mDatabase;
    private static DatabaseReference mReference;
    private static FirebaseUser currentUser;


    //Atributos
    private MyPetsAdapter myPetsAdapter;
    private List<Mascota> misMascotas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pets);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        FloatingActionButton fabAddPet = findViewById(R.id.fabAddPet);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String idDuenio = bundle.getString(KEY_DUENIO_ID);

        //Traigo Mascotas Duenio
        PetsFromOwnerController petsFromOwnerController = new PetsFromOwnerController();
        petsFromOwnerController.giveOwnerPets(idDuenio, this, new ResultListener<List<Mascota>>() {
            @Override
            public void finish(List<Mascota> resultado) {
                misMascotas.addAll(resultado);
            }
        });

        myPetsAdapter = new MyPetsAdapter(misMascotas,this,this);
        //Recycler View
        RecyclerView recyclerViewPets = findViewById(R.id.recyclerMyPets);
        recyclerViewPets.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewPets.setLayoutManager(llm);
        //adaptador
        recyclerViewPets.setAdapter(myPetsAdapter);

        fabAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPetFragment addPetFragment = new AddPetFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerPets,addPetFragment);
                fragmentTransaction.commit();
            }
        });

    }

    @Override
    public void goToProfile(String idOwner) {
        //TODO ir al profile
    }

    public static void addPetToDataBase(final String name, final String raza, final String size, final String birth, final String sex, final String photo, final String info){
        final List<Mascota> mascotaList = new ArrayList<>();
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                    Duenio duenio = childSnapShot.getValue(Duenio.class);
                    if (duenio.getUserId().equals(currentUser.getUid())){
                        if (duenio.getMisMascotas()!=null){
                            mascotaList.addAll(duenio.getMisMascotas());
                        }

                        String idPet = childSnapShot.getKey();
                        Mascota newMascota = new Mascota(idPet,name,raza,size,sex,birth,photo,info,idPet);
                        mascotaList.add(newMascota);
                        mReference.child(idPet).child("misMascotas").setValue(mascotaList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
