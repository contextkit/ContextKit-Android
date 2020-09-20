package it.cnr.iit.ck.sensing.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ck.sensing.model.Venue;

/**
 * Use the Google Places API to fetch the venue information (i.e., name and categories).
 *
 * Requires:
 *
 *  - com.google.android.gms.permission.ACCESS_FINE_LOCATION
 *  - com.google.android.gms.permission.ACCESS_WIFI_STATE
 *
 */
public class GooglePlacesController implements VenueSource{

    private static final String TAG = "GooglePlaceController";
    private static final ArrayList<Place.Field> FIELDS = new ArrayList<Place.Field>() {{
        add(Place.Field.NAME);
        add(Place.Field.ADDRESS);
        add(Place.Field.TYPES);
    }};
    private PlacesClient placesClient;

    private static  GooglePlacesController instance;

    public static GooglePlacesController getInstance(Context context, String apiKey){
        if(instance == null)
            instance = new GooglePlacesController(context, apiKey);

        return instance;
    }

    private GooglePlacesController(Context context, String apiKey){

        Places.initialize(context, apiKey);
        placesClient = Places.createClient(context);
    }

    public List<String> getCategories(){

        List<String> categories = new ArrayList<>();
        for(Place.Type type : Place.Type.values()) categories.add(type.name());

        return categories;
    }

    @Override
    public void getVenue(VenueController.VenueListener listener, Location location){

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(FIELDS);

        @SuppressLint("MissingPermission")
        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FindCurrentPlaceResponse response = task.getResult();

                if(response != null){
                    Place place = response.getPlaceLikelihoods().get(0).getPlace();

                    Venue venue = new Venue();
                    venue.name = place.getName();
                    venue.address = place.getAddress();
                    venue.types = new ArrayList<>();
                    if(place.getTypes() != null) {
                        for (Place.Type type : place.getTypes())
                            venue.types.add(type.name());
                    }

                    listener.onVenueAvailable(venue);

                }

            } else {
                Exception exception = task.getException();
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }

                listener.onVenueFailed();
            }
        });
    }
}
