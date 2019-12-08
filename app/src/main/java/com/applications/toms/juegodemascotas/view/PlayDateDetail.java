package com.applications.toms.juegodemascotas.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayDateDetail extends AppCompatActivity implements  CirculePetsAdapter.AdapterInterfaceCircule {

    private static final String TAG = "PlayDateDetail";
    public static final String KEY_PLAY_DETAIL = "play";

    //Atributos
    private String playId;
    private static CirculePetsAdapter circulePetsCreatorsAdapter;
    private static CirculePetsAdapter circulePetsParticipantsAdapter;

    private FirebaseStorage mStorage;
    private static FirebaseUser currentUser;

    private PlayController playController;

    private SupportMapFragment mapPlayDetail;
    private TextView tvLocationPlayDetail;
    private TextView tvDateTimePlayDetail;
    private TextView tvSizePlayDetail;
    private ImageView ivOwnerCreator;
    private RecyclerView rvPetsCreator;
    private RecyclerView rvPetsParticipants;
    private PlayDate playDateDetail;

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
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();

        //Busco los objetos
        mapPlayDetail = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapPlayDetail);
        tvLocationPlayDetail = findViewById(R.id.tvLocationPlayDetail);
        tvDateTimePlayDetail = findViewById(R.id.tvDateTimePlayDetail);
        tvSizePlayDetail = findViewById(R.id.tvSizePlayDetail);
        ivOwnerCreator = findViewById(R.id.ivOwnerCreator);
        rvPetsCreator = findViewById(R.id.rvPetsCreator);
        rvPetsParticipants = findViewById(R.id.rvPetsParticipants);

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

            tvLocationPlayDetail.setText(mPlace.getName());
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
    public void goToPetProfile(String idOwner, String idPet) {
        //TODO CHANGE TO FRAGMENTS 4
//        Intent intent = new Intent(PlayDateDetail.this,ProfileActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString(ProfileActivity.KEY_TYPE,"2");
//        bundle.putString(ProfileActivity.KEY_USER_ID,idOwner);
//        bundle.putString(ProfileActivity.KEY_PET_ID,idPet);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }
}
