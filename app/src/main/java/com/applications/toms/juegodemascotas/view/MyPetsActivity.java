package com.applications.toms.juegodemascotas.view;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PetsFromOwnerController;
import com.applications.toms.juegodemascotas.model.Mascota;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.view.adapter.MyPetsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyPetsActivity extends AppCompatActivity implements MyPetsAdapter.AdapterInterface {

    public static final String KEY_DUENIO_ID = "duenio";

    //Atributos
    private MyPetsAdapter myPetsAdapter;
    private List<Mascota> misMascotas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pets);

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
                //TODO agregar mascota
                Toast.makeText(MyPetsActivity.this, "En construccion", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void goToProfile(String idOwner) {
        //TODO ir al profile
    }
}
