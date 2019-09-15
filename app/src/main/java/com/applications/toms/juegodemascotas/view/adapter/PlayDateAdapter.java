package com.applications.toms.juegodemascotas.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class PlayDateAdapter extends RecyclerView.Adapter {

    //Atributos
    private Context context;
    private List<PlayDate> playDates;
    private PlacesClient placesClient;
    private FragmentManager fragmentManager;
    private PlayDateAdapterInterface playDateAdapterInterface;

    //Constructor
    public PlayDateAdapter(Context context, List<PlayDate> playDates, FragmentManager fragmentManager, PlayDateAdapterInterface playDateAdapterInterface) {
        this.context = context;
        this.playDates = playDates;
        this.fragmentManager = fragmentManager;
        this.playDateAdapterInterface = playDateAdapterInterface;
    }

    //setter
    public void setPlayDates(List<PlayDate> playDates) {
        this.playDates = playDates;
        notifyDataSetChanged();
    }

    //methods
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        String apiKey = context.getString(R.string.google_maps_key);
        if(apiKey.isEmpty()){
            Toast.makeText(context, "Not API Found", Toast.LENGTH_SHORT).show();
        }
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey);
        }
        placesClient = Places.createClient(context);

        context = parent.getContext();
        //pasamos contexto a inflador
        LayoutInflater inflater = LayoutInflater.from(context);
        //inflamos view
        View view = inflater.inflate(R.layout.card_view_play_date,parent,false);
        //pasamos holder
        PlayDatesViewHolder playDatesViewHolder = new PlayDatesViewHolder(view);

        return playDatesViewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //buscamos datos
        PlayDate playDate = playDates.get(position);
        //casteamos
        PlayDatesViewHolder playDateViewHolder = (PlayDatesViewHolder) holder;
        //cargamos
        playDateViewHolder.cargar(playDate);

    }


    private class MapReadyCallback implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap googleMap) {
//            drawMap(googleMap);
        }
    }

    @Override
    public int getItemCount() {
        return playDates.size();
    }

    public interface PlayDateAdapterInterface{
        void goToPlayDetail(String juegoId);
    }

    public class PlayDatesViewHolder extends RecyclerView.ViewHolder {

        //Atributos
        private TextView locationPlayDate;
        private TextView dateTimePlayDate;
        private TextView sizeDogsPlayDate;
        private Button btnJoinMe;
        private Button btnDetails;
        private LinearLayout lLItem;

        private LatLng location;
        private SupportMapFragment mapPlayDate;
        private GoogleMap mMap;

        //Constructor

        public PlayDatesViewHolder(@NonNull View itemView) {
            super(itemView);

            locationPlayDate = itemView.findViewById(R.id.locationPlayDate);
            dateTimePlayDate = itemView.findViewById(R.id.dateTimePlayDate);
            sizeDogsPlayDate = itemView.findViewById(R.id.sizeDogsPlayDate);

            mapPlayDate = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapPlayDate);

            btnJoinMe = itemView.findViewById(R.id.btnJoinMe);
            btnDetails = itemView.findViewById(R.id.btnDetails);

            btnJoinMe.setOnClickListener(v -> {
                Toast.makeText(context, "ASISITIRE", Toast.LENGTH_SHORT).show();
            });

            btnDetails.setOnClickListener(v -> {
                PlayDate playDetail = playDates.get(getAdapterPosition());
                playDateAdapterInterface.goToPlayDetail(playDetail.getIdPlay());
            });
        }

        //Metodos para cargar Tarjetas
        public void cargar(PlayDate playDate){

            String dateTime = playDate.getDatePlay() + " - " + playDate.getTimePlay();
            dateTimePlayDate.setText(dateTime);
            sizeDogsPlayDate.setText(playDate.getSize());

            //Get location Details
            // Specify the fields to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS);
            // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
            // and once again when the user makes a selection (for example when calling fetchPlace()).
            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

            // Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.builder(playDate.getIdPlace(), placeFields)
                    .setSessionToken(token)
                    .build();

            // Add a listener to handle the response.
            placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                Place mPlace = response.getPlace();
                location = mPlace.getLatLng();
                locationPlayDate.setText(mPlace.getName());
                mapPlayDate.getMapAsync(googleMap -> {
                    mMap = googleMap;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14f));
                    mMap.addMarker(new MarkerOptions().position(location));
                });
            }).addOnFailureListener(exception -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                }
            });

        }

    }

}
