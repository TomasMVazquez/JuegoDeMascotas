package com.applications.toms.juegodemascotas.view;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Mascota;
import com.applications.toms.juegodemascotas.view.adapter.MyPetsAdapter;
import com.applications.toms.juegodemascotas.view.fragment.AddPetFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class MyPetsActivity extends AppCompatActivity implements MyPetsAdapter.AdapterInterface {

    public static final String KEY_DUENIO_ID = "duenio";

    private static FirebaseFirestore db;
    private static String userFirestore;

    private static FirebaseUser currentUser;
    private static Context context;

    //Atributos
    private static MyPetsAdapter myPetsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pets);

        context = getApplicationContext();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        userFirestore = getResources().getString(R.string.collection_users);

        FloatingActionButton fabAddPet = findViewById(R.id.fabAddPet);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String idDuenio = bundle.getString(KEY_DUENIO_ID);

        myPetsAdapter = new MyPetsAdapter(new ArrayList<Mascota>(),this,this);

        //Traigo Mascotas Duenio
        final CollectionReference userRefMasc = db.collection(userFirestore)
                .document(currentUser.getUid()).collection("misMascotas");

        userRefMasc.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<Mascota> misMascotas = new ArrayList<>();
                misMascotas.addAll(queryDocumentSnapshots.toObjects(Mascota.class));
                myPetsAdapter.setMascotaList(misMascotas);
            }
        });


        //Recycler View
        RecyclerView recyclerViewPets = findViewById(R.id.recyclerMyPets);
        recyclerViewPets.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewPets.setLayoutManager(llm);
        //adaptador
        recyclerViewPets.setAdapter(myPetsAdapter);

        //Boton agregar mascota
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
    public void onBackPressed() {
        super.onBackPressed();
//        misMascotas.clear();
    }

    //ir al profile de la mascota
    @Override
    public void goToProfile(String idOwner, Mascota mascotaProfile) {
        Intent intent = new Intent(MyPetsActivity.this,ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ProfileActivity.KEY_TYPE,"2");
        bundle.putString(ProfileActivity.KEY_USER_ID,idOwner);
        bundle.putString(ProfileActivity.KEY_PET_ID,mascotaProfile.getIdPet());
        intent.putExtras(bundle);
        startActivity(intent);

    }

    //Aniadir mascota a la base de datos
    public static void addPetToDataBase(final String name, final String raza, final String size, final String birth, final String sex, final String photo, final String info){
        final CollectionReference userRefMasc = db.collection(userFirestore)
                .document(currentUser.getUid()).collection("misMascotas");

        final String idPet = userRefMasc.document().getId();
        Mascota newMascota = new Mascota(idPet,name,raza,size,sex,birth,photo,info,currentUser.getUid());

        userRefMasc.document(idPet).set(newMascota)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //TODO
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO
                    }
                });

        DocumentReference petsRef = db.collection(context.getResources().getString(R.string.collection_pets))
                .document(idPet);

        petsRef.set(newMascota).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //TODO
                }else{
                    //TODO
                }
            }
        });
    }

}
