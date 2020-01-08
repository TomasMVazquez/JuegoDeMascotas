package com.applications.toms.juegodemascotas.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.PetController;
import com.applications.toms.juegodemascotas.controller.PlayController;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.util.Util;
import com.applications.toms.juegodemascotas.view.adapter.CirculePetsAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class PlayDateDetail extends AppCompatActivity implements  CirculePetsAdapter.AdapterInterfaceCircule {

    private static final String TAG = "PlayDateDetail";
    public static final String KEY_PLAY_DETAIL = "play";

    //Atributos
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    private String playId;
    private static CirculePetsAdapter circulePetsCreatorsAdapter;
    private static CirculePetsAdapter circulePetsParticipantsAdapter;

    private FirebaseStorage mStorage;
    private static FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference playRef;

    private PlayController playController;

    private SupportMapFragment mapPlayDetail;
    private TextView tvLocationPlayDetail;
    private TextView tvDateTimePlayDetail;
    private TextView tvSizePlayDetail;
    private TextView quantityParticipants;
    private ImageView ivOwnerCreator;
    private RecyclerView rvPetsCreator;
    private RecyclerView rvPetsParticipants;
    private FloatingActionButton fabExitPlay;
    private Integer checkExitAdd = null;

    private List<Pet> participantsList = new ArrayList<>();

    private PlacesClient placesClient;
    private GoogleMap mMap;

    private String creatorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_date_detail);

        //Controller
        playController = new PlayController();

        //intent
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        playId = bundle.getString(KEY_PLAY_DETAIL);

        //db para extraer el juego
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        playRef = db.collection(getString(R.string.collection_play))
                .document(playId);

        //Busco los objetos
        mapPlayDetail = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapPlayDetail);
        tvLocationPlayDetail = findViewById(R.id.tvLocationPlayDetail);
        tvDateTimePlayDetail = findViewById(R.id.tvDateTimePlayDetail);
        tvSizePlayDetail = findViewById(R.id.tvSizePlayDetail);
        ivOwnerCreator = findViewById(R.id.ivOwnerCreator);
        rvPetsCreator = findViewById(R.id.rvPetsCreator);
        rvPetsParticipants = findViewById(R.id.rvPetsParticipants);
        quantityParticipants = findViewById(R.id.quantityParticipants);
        quantityParticipants.setText("0");
        fabExitPlay = findViewById(R.id.fabExitPlay);

        //Adapter
        circulePetsCreatorsAdapter = new CirculePetsAdapter(new ArrayList<>(),this,this);
        circulePetsParticipantsAdapter = new CirculePetsAdapter(new ArrayList<>(),this,this);

        //Recycler View
        rvPetsCreator.hasFixedSize();
        rvPetsParticipants.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager llm2 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rvPetsCreator.setLayoutManager(llm);
        rvPetsParticipants.setLayoutManager(llm2);
        //Adaptador
        rvPetsCreator.setAdapter(circulePetsCreatorsAdapter);
        rvPetsParticipants.setAdapter(circulePetsParticipantsAdapter);

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), Util.googleMapsApiKey(this));
        }
        placesClient = Places.createClient(this);

        //Busco info en la base de datos
        playController.givePlayDate(playId, this, resultado -> {
            creatorId = resultado.getCreator().getUserId();
            checkExitAddBtn();
            getDetails(resultado);
        });

        //Go to profile in case they click it
        ivOwnerCreator.setOnClickListener(v -> {
            //TODO CHANGE TO FRAGMENTS 4
//            Intent intent1 = new Intent(PlayDateDetail.this,ProfileActivity.class);
//            Bundle bundle1 = new Bundle();
//            bundle1.putString(ProfileActivity.KEY_TYPE,"1");
//            bundle1.putString(ProfileActivity.KEY_USER_ID,creatorId);
//            bundle1.putString(ProfileActivity.KEY_PET_ID,"0");
//            intent1.putExtras(bundle1);
//            startActivity(intent1);
        });

        //Collapsing Toolbar
        appBarLayout = findViewById(R.id.appBarLayout);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                // SCROLL TOP

                if (scrollRange + verticalOffset == 0){
//                    collapsingToolbarLayout.setTitle("Collapsed");
                    collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
//                    toolbar.setTitleTextColor(getColor(R.color.colorWhite));
                    toolbar.setBackgroundColor(getColor(R.color.colorPrimary));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    isShow = true;
                }

                //SCROLL DOWN

                else if (isShow){
//                    collapsingToolbarLayout.setTitle("Expanded");
                    collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
//                    collapsingToolbarLayout.setExpandedTitleColor(getColor(R.color.colorWhite));
                    toolbar.setBackgroundColor(Color.TRANSPARENT);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    isShow = false;
                }

            }
        });

        fabExitPlay.setOnClickListener(v -> exitAddClick());
    }

    private void checkExitAddBtn(){
        if (!currentUser.getUid().equals(creatorId)){
            playRef.get().addOnSuccessListener(documentSnapshot -> {
                PlayDate playClicked = documentSnapshot.toObject(PlayDate.class);
                if (!playClicked.getParticipants().contains(currentUser.getUid())) {
                    fabExitPlay.setImageDrawable(getDrawable(R.drawable.ic_add_location_black_24dp));
                    checkExitAdd = 1;
                } else {
                    fabExitPlay.setImageDrawable(getDrawable(R.drawable.ic_location_off_black_24dp));
                    checkExitAdd = 2;
                }
            });
            fabExitPlay.show();
        }
    }

    private void exitAddClick(){

        if (checkExitAdd == 1){
            //Logic to add participant
            playRef.update(getString(R.string.collection_participants), FieldValue.arrayUnion(currentUser.getUid()));
        }
        else if (checkExitAdd == 2){
            //Logic to exit from play date
            playRef.update(getString(R.string.collection_participants), FieldValue.arrayRemove(currentUser.getUid()));
        }
        joinToCreatorPlayDate(creatorId, playId);
    }

    //Add Plays to MyPlays DataBase
    private void addPlayToMyPlays(PlayDate playJoined) {
        DocumentReference playRefMasc = db.collection(getString(R.string.collection_users))
                .document(currentUser.getUid())
                .collection(getString(R.string.collection_my_plays))
                .document(playJoined.getIdPlay());

        if (checkExitAdd == 1) {
            playRefMasc.set(playJoined).addOnSuccessListener(aVoid ->{
                fabExitPlay.setImageDrawable(getDrawable(R.drawable.ic_location_off_black_24dp));
                checkExitAdd = 2;
            });
        }
        else if (checkExitAdd == 2){
            playRefMasc.delete().addOnCompleteListener(task -> {
                fabExitPlay.setImageDrawable(getDrawable(R.drawable.ic_add_location_black_24dp));
                checkExitAdd = 1;
            });
        }
    }

    //Join user to play
    private void joinToCreatorPlayDate(String creatorId, String juegoId) {
        DocumentReference playRefMasc = db.collection(getString(R.string.collection_users))
                .document(creatorId)
                .collection(getString(R.string.collection_my_plays)).document(juegoId);

        if (checkExitAdd == 1) {
            playRefMasc.get().addOnSuccessListener(documentSnapshotTwo -> {
                PlayDate ownerPlay = documentSnapshotTwo.toObject(PlayDate.class);
                playRefMasc.update(getString(R.string.collection_participants), FieldValue.arrayUnion(currentUser.getUid()));
                addPlayToMyPlays(ownerPlay);
            });
        }
        else if (checkExitAdd == 2){
            playRefMasc.get().addOnSuccessListener(documentSnapshotTwo -> {
                PlayDate ownerPlay = documentSnapshotTwo.toObject(PlayDate.class);
                playRefMasc.update(getString(R.string.collection_participants), FieldValue.arrayRemove(currentUser.getUid()));
                addPlayToMyPlays(ownerPlay);
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    private void getDetails(PlayDate playDateDetail){
        initMap(playDateDetail.getIdPlace());
        String dateTime = playDateDetail.getDatePlay() + " - " + playDateDetail.getTimePlay();
        tvDateTimePlayDetail.setText(dateTime);
        tvSizePlayDetail.setText(playDateDetail.getSize());
        circulePetsCreatorsAdapter.setPetList(playDateDetail.getCreatorPets());

        PetController petController = new PetController();
        if (playDateDetail.getParticipants().size()>0) {
            for (String participant : playDateDetail.getParticipants()) {
                petController.giveOwnerPets(participant, this, result -> {
                    participantsList.addAll(result);
                    circulePetsParticipantsAdapter.setPetList(participantsList);
                    quantityParticipants.setText(String.valueOf(participantsList.size()));
                });
            }
        }

        //Traer Foto del Owner
        StorageReference storageReference = mStorage.getReference()
                .child(playDateDetail.getCreator().getUserId())
                .child(playDateDetail.getCreator().getAvatar());

        storageReference.getDownloadUrl()
                .addOnSuccessListener(uri -> Glide.with(this).load(uri).into(ivOwnerCreator))
                .addOnFailureListener(e -> Glide.with(this).load(playDateDetail.getCreator().getAvatar()).into(ivOwnerCreator));
    }

    private void initMap(String placeId){
        //Get location Details
        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS);
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .setSessionToken(token)
                .build();
        // Add a listener to handle the response.
        placesClient.fetchPlace(request).addOnSuccessListener(response -> {
            Place mPlace = response.getPlace();

            tvLocationPlayDetail.setText(mPlace.getAddress());
            collapsingToolbarLayout.setTitle(mPlace.getName());

            mapPlayDetail.getMapAsync(googleMap -> {
                mMap = googleMap;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mPlace.getLatLng(),15f));
                mMap.addMarker(new MarkerOptions().position(mPlace.getLatLng()).title(mPlace.getName()));
            });
        }).addOnFailureListener(exception -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
            }
        });
    }


    @Override
    public void goToPetProfile(String keyType, String idOwner, String idPet) {
        //TODO Go To Profile
    }
}
