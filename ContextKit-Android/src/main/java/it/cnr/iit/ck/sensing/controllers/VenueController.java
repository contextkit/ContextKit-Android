package it.cnr.iit.ck.sensing.controllers;

import android.location.Location;

import java.util.List;

import it.cnr.iit.ck.sensing.model.Venue;

public class VenueController {

    private VenueSource venueSource;
    private VenueListener listener;

    public VenueController(VenueSource venueSource, VenueListener listener){
        this.venueSource = venueSource;
        this.listener = listener;
    }

    public void getVenue(Location location){
        this.venueSource.getVenue(listener, location);
    }

    public interface VenueListener {

        void onVenueAvailable(Venue venue);
        void onVenueFailed();
    }

    public List<String> getVenueCategories(){
        return venueSource.getCategories();
    }
}