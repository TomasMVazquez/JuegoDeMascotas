package com.applications.toms.juegodemascotas.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.view.adapter.AutoCompleteAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class AutocompleteTest extends AppCompatActivity {

    public static final String TAG = "AutocompleteTest";
    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteAdapter adapter;
    TextView responseView;
    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autocomplete_test);

        responseView = findViewById(R.id.response);

        String apiKey = getString(R.string.google_maps_key);
        if(apiKey.isEmpty()){
            responseView.setText("EERROORR");
            return;
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        placesClient = Places.createClient(this);
        initAutoCompleteTextView();
    }

    private void initAutoCompleteTextView() {

        autoCompleteTextView = findViewById(R.id.auto);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnItemClickListener(autocompleteClickListener);
        adapter = new AutoCompleteAdapter(this, placesClient);
        autoCompleteTextView.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener autocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            try {
                final AutocompletePrediction item = adapter.getItem(i);
                Log.d(TAG, "onItemClick: item: " + item );
                String placeID = null;
                if (item != null) {
                    placeID = item.getPlaceId();
                    Log.d(TAG, "onItemClick: placeID: " + placeID);
                }

//                To specify which data types to return, pass an array of Place.Fields in your FetchPlaceRequest
//                Use only those fields which are required.

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS
                        , Place.Field.LAT_LNG);

                FetchPlaceRequest request = null;
                if (placeID != null) {
                    request = FetchPlaceRequest.builder(placeID, placeFields)
                            .build();
                    Log.d(TAG, "onItemClick: request: " + request);
                }

                if (request != null) {
                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(FetchPlaceResponse task) {
                            responseView.setText(task.getPlace().getName() + "\n" + task.getPlace().getAddress());
                            Log.d(TAG, "onSuccess: task: " + task.getPlace().getName());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            responseView.setText(e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
}
