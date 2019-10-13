package com.applications.toms.juegodemascotas.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.OwnerController;
import com.applications.toms.juegodemascotas.controller.PetController;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.applications.toms.juegodemascotas.util.Util;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class NewPlayDate extends AppCompatActivity implements OnMapReadyCallback, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "NewPlayDate";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LONG_BOUNDS = new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));
    public static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    //Atributos
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng myLocationBias;
    private String size="";
    private String idPlace;
    private List<Pet> myPets = new ArrayList<Pet>();

    private static String userFirestore;
    private static String playFirestore;

    private OwnerController ownerController;
    private PetController petController;

    private static FirebaseUser currentUser;
    private static Context context;
    private Owner creator;

    //Widgets
    private PlacesClient placesClient;
    private EditText etPlayLocation;
    private static EditText etDatePlayDate;
    private EditText etHourPlayDate;
    private EditText etTitlePlayDate;
    private Button btnNewPlayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_play_date);

        etPlayLocation = findViewById(R.id.etPlayLocation);
        etDatePlayDate = findViewById(R.id.etDatePlayDate);
        etHourPlayDate = findViewById(R.id.etHourPlayDate);
        etTitlePlayDate = findViewById(R.id.etTitlePlayDate);
        btnNewPlayDate = findViewById(R.id.btnNewPlayDate);

        etDatePlayDate.clearFocus();

        //controller
        ownerController = new OwnerController();
        petController = new PetController();

        //Base De Datos
        context = getApplicationContext();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        playFirestore = getResources().getString(R.string.collection_play);
        userFirestore = getResources().getString(R.string.collection_users);

        //Traigo al creador
        ownerController.giveOwnerData(currentUser.getUid(),this,resultado -> creator=resultado);

        //Traigo Mascotas Owner
        petController.giveOwnerPets(currentUser.getUid(),this,resultado -> myPets.addAll(resultado));

        //Seleccion del tamanio de mascota
        Spinner spinnerPetSizePlayDate = findViewById(R.id.spinnerPetSizePlayDate);
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(getResources().getString(R.string.add_pet_size));
        spinnerArray.add(getResources().getString(R.string.spinner_small));
        spinnerArray.add(getResources().getString(R.string.spinner_medium));
        spinnerArray.add(getResources().getString(R.string.spinner_large));
        spinnerArray.add(getResources().getString(R.string.spinner_all));
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,spinnerArray);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPetSizePlayDate.setAdapter(adapterSpinner);
        spinnerPetSizePlayDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                size =  parent.getItemAtPosition(position).toString();
                if (size != getResources().getString(R.string.add_pet_size)){
                    etPlayLocation.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Fecha del PlayDate
        etDatePlayDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                datePicker();
            }
        });

        //Hora del PlayDate
        etHourPlayDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                timePicker();
            }
        });

        //MAPS AND GEOLOCATING
        getLocationPermission();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), Util.googleMapsApiKey(this));
        }
        placesClient = Places.createClient(this);
        
        //Al clickear el boton crear juego
        btnNewPlayDate.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: Click Crear PlayDate");
            addNewPlayToDB();
            
        });

        etPlayLocation.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                searchInMap();
            }
        });
    }

    //Aniadir PlayDate a la base de Datos
    private void addNewPlayToDB(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Creo la collection de juegos "dentro del usuario" para agregar nuevo juego
        CollectionReference playRefMasc = db.collection(userFirestore)
                .document(currentUser.getUid()).collection(getString(R.string.collection_my_plays));

        //Tomo el ID del nuevo juego
        String idPlay = playRefMasc.document().getId();

        //Creo el nuevo juego dentro de la collection juego
        DocumentReference playRef = db.collection(context.getResources().getString(R.string.collection_play))
                .document(idPlay);

        List<String> invitados = new ArrayList<>();
        //Creo el nuevo juego
        PlayDate newPlay = new PlayDate(
                idPlay,
                0, //La privacidad es 0 para publica y 1 para privada -- por ahora no esta la funcionalidad de privada
                etDatePlayDate.getText().toString(),
                etHourPlayDate.getText().toString(),
                idPlace,
                size,
                myPets,
                creator,
                invitados
                );

        //Agrego el juego a las collectiones
        playRefMasc.document(idPlay).set(newPlay).addOnSuccessListener(aVoid -> {
            //TODO
            Log.d(TAG, "addNewPlayToDB: Creado en el Owner");
        }).addOnFailureListener(aVoid -> {
            //TODO
        });

        playRef.set(newPlay).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               //TODO
               Log.d(TAG, "addNewPlayToDB: Creado en collection");
               Intent i = new Intent(NewPlayDate.this,MainActivity.class);
               startActivity(i);
               finish();
           }else {
               //TODO
           }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(NewPlayDate.this,MainActivity.class);
        startActivity(i);
    }

    //DatePicker
    private void datePicker(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                NewPlayDate.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setMinDate(now);
        // If you're calling this from a support Fragment
        // dpd.show(getFragmentManager(), "Datepickerdialog");
        // If you're calling this from an AppCompatActivity
         dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    //TimePicker
    private void timePicker(){
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                NewPlayDate.this,
                now.get(Calendar.HOUR),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.show(getSupportFragmentManager(),"Timepickerdialog");
    }

    //Inicializar MAPS y SEARCH
    private void init(){
        Log.d(TAG, "init: initializing");

        ImageButton searchBtn = findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(v -> {
            searchInMap();
        });

        etPlayLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    geoLocate();
                }
                return false;
            }
        });

        hideSoftKeyboard();
    }

    private void searchInMap(){
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        //Create Location Bias
        LatLng biasLocation = null;
        if (myLocationBias != null){
            biasLocation = myLocationBias;
        }else {
            biasLocation = new LatLng(0,0);
        }

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setLocationBias(RectangularBounds.newInstance(
                        biasLocation, biasLocation))
                .build(NewPlayDate.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    //OnActivityResult para el search de MAPS
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //Start Test
                // Specify the fields to return.
                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG);

                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                // and once again when the user makes a selection (for example when calling fetchPlace()).
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

                // Construct a request object, passing the place ID and fields array.
                FetchPlaceRequest request = FetchPlaceRequest.builder(place.getId(), placeFields)
                        .setSessionToken(token)
                        .build();

                // Add a listener to handle the response.
                placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                    Place mPlace = response.getPlace();
                    moveCamera(mPlace.getLatLng(),DEFAULT_ZOOM,mPlace.getName());
                    etPlayLocation.setText(mPlace.getName());
                    idPlace = mPlace.getId();
                }).addOnFailureListener(exception -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                    }
                });

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    //Localizar lo buscado en el Edit Text
    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating...");

        //VERSION CON EDIT TEXT
        String searchString = etPlayLocation.getText().toString();
        Geocoder geocoder = new Geocoder(NewPlayDate.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.d(TAG, "geoLocate: IOException " + e.getMessage());
        }
        if (list.size()>0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found location " + address.toString());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }

    //Localizar el Equipo
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device current location");
        try {
            if (mLocationPermissionGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();
                            myLocationBias = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                            Log.d(TAG, "onComplete: Fond Location - LAT " + currentLocation.getLatitude() + " -LON " + currentLocation.getLongitude());
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,getResources().getString(R.string.myLocation));
                        }else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(NewPlayDate.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    //Mover mapa
    private void moveCamera(LatLng latLng, float zoom,String title){
        Log.d(TAG, "moveCamera: Moving the camera to: lat: " + latLng.latitude + " , lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        if (!title.equals(getResources().getString(R.string.myLocation))) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyboard();
    }

    //Inicializar el mapa
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(NewPlayDate.this);
    }

    //Permisos para localizar
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting Location Permission ");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if ((ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)) ==
        PackageManager.PERMISSION_GRANTED){
            if ((ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)) ==
                    PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted=true;
                initMap();
            }else {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length>0){
                    for (int i = 0; i < grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: GRANTED");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    //Esconder el teclado
    private void hideSoftKeyboard(){
         this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //Una vez esta listo el mapa
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted){
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            init();
        }
    }

    //DATE TIME PICKERS Method
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = hourOfDay+":"+minute;
        etHourPlayDate.setText(time);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        etDatePlayDate.setText(date);
    }

}
