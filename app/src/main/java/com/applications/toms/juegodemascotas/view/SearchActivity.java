package com.applications.toms.juegodemascotas.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity implements PetsAdapter.PetsAdapterInterface {

    private static final String TAG = "SearchActivity";

    private static FirebaseUser currentUser;
    private static PetsAdapter petsAdapter;
    private PetController petController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint(getString(R.string.search));

        //Toolbar with the search
        Toolbar myToolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(myToolbar);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //intenti with bundle //TODO SearchActivity bundle with nothing
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //Get Firebase User instance
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
        Intent intent = new Intent(SearchActivity.this,ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ChatActivity.KEY_CHAT,"2");
        bundle.putString(ChatActivity.KEY_USER_TO_CHAT, userToChat);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
