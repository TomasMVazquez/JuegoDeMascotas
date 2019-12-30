package com.applications.toms.juegodemascotas.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.PlayDate;
import com.bumptech.glide.Glide;
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
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class MyPlaysMapAdapter extends RecyclerView.Adapter<MyPlaysMapAdapter.ViewHolder>  {

    private static final String TAG = "MyPlaysMapAdapter";

    private List<PlayDate> myPlays;
    private Context context;
    private MapAdapterInterface mapAdapterInterface;

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public MyPlaysMapAdapter(List<PlayDate> playDates, MapAdapterInterface mapAdapterInterface) {
        super();
        this.myPlays = playDates;
        this.mapAdapterInterface = mapAdapterInterface;
    }

    public void setMyPlays(List<PlayDate> plays) {
        myPlays.clear();
        myPlays.addAll(plays);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_my_plays, parent, false));
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
        return myPlays.size();
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
    public class ViewHolder extends RecyclerView.ViewHolder {

        private View layout;
        private LatLng location;
        private TextView locationPlayDate;
        private TextView dateTimePlayDate;
        private TextView sizeDogsPlayDate;

        private ImageView imgMap;

        private String idPlay;
        private String idPlace;

        private ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
//            mapView = (MapView) layout.findViewById(R.id.mapMyPlays);
            locationPlayDate = layout.findViewById(R.id.locationMyPlays);
            dateTimePlayDate = layout.findViewById(R.id.dateTimeMyPlays);
            sizeDogsPlayDate = layout.findViewById(R.id.sizeDogsMyPlays);
            imgMap = layout.findViewById(R.id.imgMap);

            itemView.setOnClickListener(v -> mapAdapterInterface.goToPlayDetail(idPlay));
        }

        private void setMapLocation() {

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
            FetchPlaceRequest request = FetchPlaceRequest.builder(idPlace, placeFields)
                    .setSessionToken(token)
                    .build();

            // Add a listener to handle the response.
            placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                Place mPlace = response.getPlace();
                location = mPlace.getLatLng();
                locationPlayDate.setText(mPlace.getName());
                String staticMap = "https://maps.googleapis.com/maps/api/staticmap?center=" + location.latitude + "," + location.longitude
                        + "&zoom=14&size=640x640&markers=size:mid|" + location.latitude + "," + location.longitude + "&key=" + apiKey;

                Glide.with(context).load(staticMap).into(imgMap);

            }).addOnFailureListener(exception -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                }
            });

        }

        private void bindView(int pos) {
            PlayDate item = myPlays.get(pos);
            String dateTime = item.getDatePlay() + " - " + item.getTimePlay();
            dateTimePlayDate.setText(dateTime);
            sizeDogsPlayDate.setText(item.getSize());
            idPlay = item.getIdPlay();
            idPlace = item.getIdPlace();
            setMapLocation();
        }

    }

}
