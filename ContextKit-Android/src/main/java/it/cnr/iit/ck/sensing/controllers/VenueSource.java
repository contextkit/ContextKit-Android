package it.cnr.iit.ck.sensing.controllers;

import android.location.Location;

import java.util.List;

public interface VenueSource {

    void getVenue(VenueController.VenueListener listener, Location location);
    List<String> getCategories();
}
