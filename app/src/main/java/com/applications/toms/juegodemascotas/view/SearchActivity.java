package com.applications.toms.juegodemascotas.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PetController;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.view.adapter.PetsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity implements PetsAdapter.PetsAdapterInterface {

    private static final String TAG = "SearchActivity";

    private static PetsAdapter petsAdapter;
    private PetController petController;

    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Get Firebase instances
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint(getString(R.string.search));

        //Toolbar with the search
        Toolbar myToolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(myToolbar);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //intent with bundle //TODO SearchActivity bundle with nothing
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //Get Firebase User instance
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Get Pet controller
        petController = new PetController();

        //Get Pet Adapter for recycler
        petsAdapter = new PetsAdapter(new ArrayList<>(),this,this);

        //Recycler View
        RecyclerView recyclerViewPets = findViewById(R.id.recyclerPets);
        recyclerViewPets.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewPets.setLayoutManager(llm);
        //adaptador
        recyclerViewPets.setAdapter(petsAdapter);

        //If the user is logged then get all pets from DataBase
        if (currentUser != null){
            petController.givePetList(this,result -> petsAdapter.setPetList(result));
        }

        //For search Logic while writing or when enter
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchResult(query.toUpperCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: search: " + newText);
                searchResult(newText.toUpperCase());
                return false;
            }
        });

    }

    //Get results from search
    private void searchResult(String searchText){
        Log.d(TAG, "searchResult: search: " + searchText);
        petController.giveResultSearch(searchText, this, result -> {
            petsAdapter.setPetList(result);
            Log.d(TAG, "searchResult: result: " + result);
        });
    }

    //Go to a profile when clicking the card of a pet.
    @Override
    public void goToProfileFromPets(String idOwner, Pet pet) {
        Intent intent = new Intent(SearchActivity.this,ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ProfileActivity.KEY_TYPE,"2");
        bundle.putString(ProfileActivity.KEY_USER_ID,idOwner);
        bundle.putString(ProfileActivity.KEY_PET_ID, pet.getIdPet());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void goToChat(String userToChat) {
        //TODO CHANGE TO FRAGMENTS 1
//        Intent intent = new Intent(SearchActivity.this,ChatActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString(ChatActivity.KEY_CHAT,"2");
//        bundle.putString(ChatActivity.KEY_USER_TO_CHAT, userToChat);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

    @Override
    public void addFriend(Pet pet) {
        Toast.makeText(this, "Agregando a " + pet.getNombre() + " a mi lista de amigos", Toast.LENGTH_SHORT).show();

        //Create on the current user a document with firend list
        CollectionReference myFriendCol = db.collection(getString(R.string.collection_users))
                .document(currentUser.getUid()).collection(getString(R.string.collection_my_friends));

        myFriendCol.document(pet.getIdPet()).set(pet).addOnSuccessListener(aVoid -> {
            //TODO si ya existia te lo reemplaza... Deberia no realizar todo esto o si?
            Toast.makeText(SearchActivity.this, "Agregado!", Toast.LENGTH_SHORT).show();
        });

    }
}
