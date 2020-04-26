package com.applications.toms.juegodemascotas.view.menu_fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.view.adapter.UserAdapter;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.util.Keys;
import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.util.FragmentTitles;
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

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment implements FragmentTitles {

    public static final String TAG = "UsersFragment";

    public static final String NAME = "fragment_title_users";

    private UserAdapter userAdapter;
    private List<Owner> mUser;

    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private Context context;

    //Componente
    private SearchView search_users;

    //Constructor
    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        context = getContext();
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mUser = new ArrayList<>();

        //Componentes
        search_users = view.findViewById(R.id.search_users);
        search_users.setQueryHint(getString(R.string.search));

        TextView emptyStateSearch = view.findViewById(R.id.emptyStateSearch);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(getContext(),mUser, false);
        recyclerView.setAdapter(userAdapter);

        //Método para buscar todos los usuarios de la app
        if (currentUser != null){
            emptyStateSearch.setVisibility(View.GONE);
            readUsers();
        }else {
            emptyStateSearch.setVisibility(View.VISIBLE);
        }


        //For search Logic while writing or when enter
        search_users.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query.toLowerCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: search: " + newText);
                searchUsers(newText.toLowerCase());
                return false;
            }
        });


        return view;
    }

    //Métodos
    private void searchUsers(String s) {
        //Buscar usuario en la base de datos
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseFirestore.getInstance().collection(Keys.KEY_OWNER)
                .orderBy(Keys.KEY_OWNER_SEARCH)
                .startAt(s)
                .endAt(s+"\uf0ff");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                mUser.clear();
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Owner user = snapshot.toObject(Owner.class);

                    assert fuser != null;
                    if (!user.getUserId().equals(fuser.getUid())) {
                        mUser.add(user);
                    }
                }
                userAdapter.setmUsers(mUser);

            }
        });

    }

    private void readUsers(){

        //Buscar todos los usuarios de la app en la DB
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore reference = FirebaseFirestore.getInstance();

        CollectionReference listUserRef = reference.collection(Keys.KEY_OWNER);
        listUserRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (search_users.getQuery().toString().equals("")) {
                    mUser.clear();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        Owner user = queryDocumentSnapshot.toObject(Owner.class);
                        if (!user.getUserId().equals(firebaseUser.getUid())) {
                            mUser.add(user);
                        }
                    }

                    userAdapter.setmUsers(mUser);
                }
            }
        });

    }

    @Override
    public int getFragmentTitle() {
        return R.string.newchat;
    }
}
