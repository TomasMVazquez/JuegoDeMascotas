package com.applications.toms.juegodemascotas.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.applications.toms.juegodemascotas.R;
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
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class MapTest extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapTest";
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
    //Widgets
    private TextView responseView;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_test);

        getLocationPermission();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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

    }

    private void init(){
        Log.d(TAG, "init: initializing");

        ImageButton testBtn = findViewById(R.id.testBtn);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        .build(MapTest.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            }
        });

        hideSoftKeyboard();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d(TAG, "Place: " + place.getName() + ", " + place.getId());
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
                Log.d(TAG, "onActivityResult: request: " + request);

                // Add a listener to handle the response.
                placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse response) {
                        Log.d(TAG, "onSuccess: response: " + response);
                        Place mPlace = response.getPlace();
                        Log.d(TAG, "Place found: " + mPlace.getName() + " - " + mPlace.getLatLng() + " - " + mPlace.getId());
                        moveCamera(mPlace.getLatLng(),DEFAULT_ZOOM,mPlace.getName());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e(TAG, "Place not found: " + exception.getMessage());
                        }
                    }
                });


                //FIN
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

/**

    LO QUE ESTA ABAJO ESTA OK **********************************************************

 */

    private void geoLocate(Place place){
        Log.d(TAG, "geoLocate: geolocating: " + place.getId());

//        moveCamera(new LatLng(place.getLatLng().latitude,place.getLatLng().longitude),DEFAULT_ZOOM,place.getName());

        //VERSION CON EDIT TEXT
//        String searchString = etPlayLocation.getText().toString();
//        Geocoder geocoder = new Geocoder(MapTest.this);
//
//        List<Address> list = new ArrayList<>();
//        try {
//            list = geocoder.getFromLocationName(searchString,1);
//        }catch (IOException e){
//            Log.d(TAG, "geoLocate: IOException " + e.getMessage());
//        }
//        if (list.size()>0){
//            Address address = list.get(0);
//
//            Log.d(TAG, "geoLocate: found location " + address.toString());
//            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
//        }
    }

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
                            Toast.makeText(MapTest.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom,String title){
        Log.d(TAG, "moveCamera: Moving the camera to: lat: " + latLng.latitude + " , lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        if (!title.equals(getResources().getString(R.string.myLocation))) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyboard();
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapTest.this);
    }

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

    private void hideSoftKeyboard(){
         this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted){
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            init();
        }
    }
}
