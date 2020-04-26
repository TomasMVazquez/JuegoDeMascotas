package com.applications.toms.juegodemascotas.view.menu_fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.OwnerController;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.view.adapter.FriendsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.shape.CircleShape;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements FriendsAdapter.FriendAdapterInterface {

    private static final String TAG = "FriendsFragment";
    private static final String SHOWCASE_ID = "simple friends";


    private List<Pet> petFriendList = new ArrayList<>();

    private FriendsAdapter friendsAdapter;

    private TextView emptyStateFirends;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        Context context = getApplicationContext();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Adapter
        friendsAdapter = new FriendsAdapter(petFriendList, context,this);

        emptyStateFirends = view.findViewById(R.id.emptyStateFirends);

        //Recycler View
        RecyclerView recyclerPlayDates = view.findViewById(R.id.recyclerFriends);
        recyclerPlayDates.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerPlayDates.setLayoutManager(llm);
        //adaptador
        recyclerPlayDates.setAdapter(friendsAdapter);

        if (currentUser != null){
            OwnerController ownerController = new OwnerController();
            ownerController.giveFriends(petFriendList, currentUser.getUid(), context, result -> {
                petFriendList.addAll(result);
                friendsAdapter.setPetList(petFriendList);
                if (petFriendList.size()>0){
                    emptyStateFirends.setVisibility(View.GONE);
                }else {
                    emptyStateFirends.setVisibility(View.VISIBLE);
                }
            });
        }else {
            emptyStateFirends.setVisibility(View.VISIBLE);
        }

        return view;
    }


    @Override
    public void update(int index) {
        petFriendList.remove(index);
        friendsAdapter.setPetList(petFriendList);
    }

}
