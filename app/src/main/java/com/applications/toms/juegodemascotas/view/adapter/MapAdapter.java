package com.applications.toms.juegodemascotas.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder>  {

    private static final String TAG = "MapAdapter";

    private List<PlayDate> playDates;
    private Context context;
    private MapAdapterInterface mapAdapterInterface;

    public MapAdapter(List<PlayDate> playDates, MapAdapterInterface mapAdapterInterface) {
        super();
        this.playDates = playDates;
        this.mapAdapterInterface = mapAdapterInterface;
    }

    public void setPlayDates(List<PlayDate> plays) {
//        this.playDates = playDates;
        playDates.clear();
        playDates.addAll(plays);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_play_date, parent, false));
    }

    /**
     * This function is called when the user scrolls through the screen and a new item needs
     * to be shown. So we will need to bind the holder with the details of the next item.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder == null) {
            return;
        }
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return playDates.size();
    }

    public interface MapAdapterInterface{
        void goToPlayDetail(String juegoId);
    }

    /*
     * Holder for Views used in the {@link LiteListDemoActivity.MapAdapter}.
     * Once the  the <code>map</code> field is set, otherwise it is null.
     * When the {@link #onMapReady(com.google.android.gms.maps.GoogleMap)} callback is received and
     * the {@link com.google.android.gms.maps.GoogleMap} is ready, it stored in the {@link #map}
     * field. The map is then initialised with the NamedLocation that is stored as the tag of the
     * MapView. This ensures that the map is initialised with the latest data that it should
     * display.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        private MapView mapView;
        private GoogleMap map;
        private View layout;
        private LatLng location;
        private TextView locationPlayDate;
        private TextView dateTimePlayDate;
        private TextView sizeDogsPlayDate;
        private Button btnJoinMe;
        private Button btnDetails;

        private String idPlay;

        private ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            mapView = (MapView) layout.findViewById(R.id.mapPlayDate);
            locationPlayDate = layout.findViewById(R.id.locationPlayDate);
            dateTimePlayDate = layout.findViewById(R.id.dateTimePlayDate);
            sizeDogsPlayDate = layout.findViewById(R.id.sizeDogsPlayDate);

            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }

            btnJoinMe = layout.findViewById(R.id.btnJoinMe);
            btnDetails = layout.findViewById(R.id.btnDetails);

            btnJoinMe.setOnClickListener(v -> {
                Toast.makeText(context, "ASISITIRE a " + idPlay, Toast.LENGTH_SHORT).show();
            });

            btnDetails.setOnClickListener(v -> mapAdapterInterface.goToPlayDetail(idPlay));
        }

        public void onResume(){
            mapView.onResume();
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context.getApplicationContext());
            map = googleMap;
            setMapLocation();
        }

        /*
         * Displays a {@link LiteListDemoActivity.NamedLocation} on a
         * {@link com.google.android.gms.maps.GoogleMap}.
         * Adds a marker and centers the camera on the NamedLocation with the normal map type.
         */
        private void setMapLocation() {
            if (map == null) return;

            PlayDate data = (PlayDate) mapView.getTag();
            if (data == null) return;

            String apiKey = context.getString(R.string.google_maps_key);
            if(apiKey.isEmpty()){
                Toast.makeText(context, "Not API Found", Toast.LENGTH_SHORT).show();
            }
            // Setup Places Client
            if (!Places.isInitialized()) {
                Places.initialize(context, apiKey);
            }
            PlacesClient placesClient = Places.createClient(context);
            //Get location Details
            // Specify the fields to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS);
            // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
            // and once again when the user makes a selection (for example when calling fetchPlace()).
            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

            // Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.builder(data.getIdPlace(), placeFields)
                    .setSessionToken(token)
                    .build();

            // Add a listener to handle the response.
            placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                Place mPlace = response.getPlace();
                location = mPlace.getLatLng();
                locationPlayDate.setText(mPlace.getName());
                // Add a marker for this item and set the camera
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f));
                map.addMarker(new MarkerOptions().position(location));

                // Set the map type back to normal.
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }).addOnFailureListener(exception -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                }
            });

        }

        private void bindView(int pos) {
            PlayDate item = playDates.get(pos);
            // Store a reference of the ViewHolder object in the layout.
            layout.setTag(this);
            // Store a reference to the item in the mapView's tag. We use it to get the
            // coordinate of a location, when setting the map location.
            mapView.setTag(item);
            setMapLocation();
            String dateTime = item.getDatePlay() + " - " + item.getTimePlay();
            dateTimePlayDate.setText(dateTime);
            sizeDogsPlayDate.setText(item.getSize());
            idPlay = item.getIdPlay();
            onResume();
        }

    }

}
