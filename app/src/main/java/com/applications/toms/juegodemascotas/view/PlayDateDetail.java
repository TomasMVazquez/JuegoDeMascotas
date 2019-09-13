package com.applications.toms.juegodemascotas.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Duenio;
import com.applications.toms.juegodemascotas.model.Juego;
import com.applications.toms.juegodemascotas.model.Mascota;
import com.applications.toms.juegodemascotas.view.adapter.CirculeOwnerAdapter;
import com.applications.toms.juegodemascotas.view.adapter.CirculePetsAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

    public static final String KEY_PLAY_DETAIL = "play";

    //Atributos
    private String playId;
    private static CirculePetsAdapter circulePetsCreatorsAdapter;
    private static CirculePetsAdapter circulePetsParticipantsAdapter;

    private FirebaseStorage mStorage;
    private static FirebaseUser currentUser;
    private FirebaseFirestore db;

    private SupportMapFragment mapPlayDetail;
    private TextView tvLocationPlayDetail;
    private TextView tvDateTimePlayDetail;
    private TextView tvSizePlayDetail;
    private ImageView ivOwnerCreator;
    private RecyclerView rvPetsCreator;
    private RecyclerView rvPetsParticipants;
    private Juego playDateDetail;

    private PlacesClient placesClient;
    private GoogleMap mMap;

    private String creatorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_date_detail);

        //intent
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        playId = bundle.getString(KEY_PLAY_DETAIL);

        //db para extraer el juego
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();

        db = FirebaseFirestore.getInstance();

        //Busco los objetos
        mapPlayDetail = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapPlayDetail);
        tvLocationPlayDetail = findViewById(R.id.tvLocationPlayDetail);
        tvDateTimePlayDetail = findViewById(R.id.tvDateTimePlayDetail);
        tvSizePlayDetail = findViewById(R.id.tvSizePlayDetail);
        ivOwnerCreator = findViewById(R.id.ivOwnerCreator);
        rvPetsCreator = findViewById(R.id.rvPetsCreator);
        rvPetsParticipants = findViewById(R.id.rvPetsParticipants);

        //Adapter
        circulePetsCreatorsAdapter = new CirculePetsAdapter(new ArrayList<Mascota>(),this,this);
        circulePetsParticipantsAdapter = new CirculePetsAdapter(new ArrayList<Mascota>(),this,this);

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

        String apiKey = getString(R.string.google_maps_key);
        if(apiKey.isEmpty()){
            Toast.makeText(this, "Not API Found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);

        //Busco info en la base de datos
        DocumentReference playRef = db.collection(getString(R.string.collection_play))
                .document(playId);

        playRef.get().addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               DocumentSnapshot document = task.getResult();
               if (document.exists()) {
                   playDateDetail = document.toObject(Juego.class);
                   getCreator(playDateDetail.getOrganizadorDuenio());
                   getDetails(playDateDetail);
                   creatorId = playDateDetail.getOrganizadorDuenio();
               }
           }else {
               //TODO
           }
        });

    }

    private void getDetails(Juego playDateDetail){
        initMap(playDateDetail.getIdPlace());
        String dateTime = playDateDetail.getFechaJuego() + " - " + playDateDetail.getHoraJuego();
        tvDateTimePlayDetail.setText(dateTime);
        tvSizePlayDetail.setText(playDateDetail.getTamanioPerros());
        circulePetsCreatorsAdapter.setMascotaList(playDateDetail.getOrganizadorMascota());
        circulePetsParticipantsAdapter.setMascotaList(playDateDetail.getInvitados());
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

    private void getCreator(String idCreator){
        DocumentReference userRef = db.collection(getString(R.string.collection_users))
                .document(idCreator);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Duenio creator = document.toObject(Duenio.class);

                    //Traer Foto del Duenio
                    StorageReference storageReference = mStorage.getReference().child(creator.getUserId()).child(creator.getFotoDuenio());

                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> Glide.with(this).load(uri).into(ivOwnerCreator))
                            .addOnFailureListener(e -> Glide.with(this).load(creator.getFotoDuenio()).into(ivOwnerCreator));

                }
            }
        });
    }

    @Override
    public void goToProfile(String idOwner, String idPet) {

    }
}
