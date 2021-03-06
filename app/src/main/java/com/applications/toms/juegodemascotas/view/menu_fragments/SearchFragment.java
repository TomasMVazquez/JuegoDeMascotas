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
import android.widget.TextView;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PetController;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.FragmentTitles;
import com.applications.toms.juegodemascotas.util.Keys;
import com.applications.toms.juegodemascotas.view.adapter.PetsAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements PetsAdapter.PetsAdapterInterface, FragmentTitles {

    public static final String TAG = "SearchFragment";

    private static PetsAdapter petsAdapter;
    private PetController petController;
    private Context context;
    private FrameLayout flSearch;
    private List<Pet> petList = new ArrayList<>();

    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private SearchInterface searchInterface;

    private TextView emptyStateSearch;

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

        emptyStateSearch = view.findViewById(R.id.emptyStateSearch);

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
            petController.givePetListDup(petList,context,result -> {
                petList.addAll(result);
                petsAdapter.setPetList(result);
                if (petList.size()>0){
                    emptyStateSearch.setVisibility(View.GONE);
                }else {
                    emptyStateSearch.setVisibility(View.VISIBLE);
                }
            });
        }else {
            emptyStateSearch.setVisibility(View.VISIBLE);
        }

        //For search Logic while writing or when enter
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchResult(query.toLowerCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: search: " + newText);
                searchResult(newText.toLowerCase());
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


    //Add it as a friend when clicking the heart icon.
    @Override
    public void addFriend(Pet pet) {
        Snackbar.make(flSearch,"Agregando a " + pet.getName() + " a mi lista de amigos",Snackbar.LENGTH_SHORT).show();

        //Create on the current user a document with firend list
        CollectionReference myFriendCol = db.collection(getString(R.string.collection_users))
                .document(currentUser.getUid()).collection(getString(R.string.collection_my_friends));

        myFriendCol.document(pet.getIdPet()).set(pet).addOnSuccessListener(aVoid -> {
            Snackbar.make(flSearch,"¡Agregado!",Snackbar.LENGTH_SHORT).show();
        });
    }
}
