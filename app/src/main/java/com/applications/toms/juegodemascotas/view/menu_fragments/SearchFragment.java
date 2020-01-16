package com.applications.toms.juegodemascotas.view.menu_fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PetController;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.FragmentTitles;
import com.applications.toms.juegodemascotas.view.adapter.PetsAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements PetsAdapter.PetsAdapterInterface, FragmentTitles {

    public static final String TAG = "SearchFragment";

    private static PetsAdapter petsAdapter;
    private PetController petController;
    private Context context;
    private FrameLayout flSearch;

    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private SearchInterface searchInterface;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        searchInterface = (SearchInterface) context;
    }

    public void setSearchInterface(SearchInterface searchInterface) {
        this.searchInterface = searchInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        context = getContext();
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        flSearch = view.findViewById(R.id.flSearch);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint(getString(R.string.search));

        //Get Firebase User instance
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Get Pet controller
        petController = new PetController();

        //Get Pet Adapter for recycler
        petsAdapter = new PetsAdapter(new ArrayList<>(),context,this);

        //Recycler View
        RecyclerView recyclerViewPets = view.findViewById(R.id.recyclerPets);
        recyclerViewPets.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerViewPets.setLayoutManager(llm);
        //adaptador
        recyclerViewPets.setAdapter(petsAdapter);

        //If the user is logged then get all pets from DataBase
        if (currentUser != null){
            petController.givePetList(context,result -> petsAdapter.setPetList(result));
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

        return view;
    }

    @Override
    public int getFragmentTitle() {
        return R.string.search;
    }

    public interface SearchInterface{
        void chatFromSearch(String userToChat);
        void goToPetProfile(String idOwner, Pet choosenPet);
    }

    //Get results from search
    private void searchResult(String searchText){
        Log.d(TAG, "searchResult: search: " + searchText);
        petController.giveResultSearch(searchText, context, result -> {
            petsAdapter.setPetList(result);
            Log.d(TAG, "searchResult: result: " + result);
        });
    }

    //Go to a profile when clicking the card of a pet.
    @Override
    public void goToProfileFromPets(String idOwner, Pet choosenPet) {
        searchInterface.goToPetProfile(idOwner,choosenPet);
    }

    //Go to a chat when clicking the chat icon.
    @Override
    public void goToChat(String userToChat) {
        searchInterface.chatFromSearch(userToChat);
    }

    //Add it as a friend when clicking the heart icon.
    @Override
    public void addFriend(Pet pet) {
        Snackbar.make(flSearch,"Agregando a " + pet.getNombre() + " a mi lista de amigos",Snackbar.LENGTH_SHORT).show();

        //Create on the current user a document with firend list
        CollectionReference myFriendCol = db.collection(getString(R.string.collection_users))
                .document(currentUser.getUid()).collection(getString(R.string.collection_my_friends));

        myFriendCol.document(pet.getIdPet()).set(pet).addOnSuccessListener(aVoid -> {
            Snackbar.make(flSearch,"¡Agregado!",Snackbar.LENGTH_SHORT).show();
        });
    }
}
