/*
 *   Copyright (c) 2017. Mattia Campana, mattia.campana@iit.cnr.it, Franca Delmastro, franca.delmastro@gmail.com
 *
 *   This file is part of ContextKit.
 *
 *   ContextKit (CK) is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ContextKit (CK) is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ContextKit (CK).  If not, see <http://www.gnu.org/licenses/>.
 */

package it.cnr.iit.ck.sensing.probes;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ck.sensing.controllers.GooglePlacesController;
import it.cnr.iit.ck.sensing.controllers.VenueController;
import it.cnr.iit.ck.sensing.controllers.VenueSource;
import it.cnr.iit.ck.logs.FileLogger;
import it.cnr.iit.ck.sensing.model.LocationInfo;
import it.cnr.iit.ck.sensing.model.SensorData;
import it.cnr.iit.ck.sensing.model.Venue;

/**
 * This probe monitors the geographical location of the local device. Specifically, it reports the
 * following information:
 *
 *      - latitude
 *      - longitude
 *      - speed
 *      - the position's accuracy in meters
 *      - altitude
 *      - bearing
 *
 * Requires:
 *
 *  - "com.google.android.gms.permission.ACCESS_COARSE_LOCATION"
 *
 * Check the VenueSource for additional permissions.
 *
 */
@SuppressWarnings("unused")
public class LocationProbe extends OnEventProbe {

    public static final String CONF_FIELD_VENUE_SOURCE = "venueSource";
    public static final String CONF_FIELD_LOCATION_INTERNAL = "locationIntervalSeconds";
    public static final String CONF_FIELD_VENUE_SOURCE_API = "apiKey";

    public static final String VENUE_SOURCE_GOOGLE = "Google";

    private FusedLocationProviderClient locationProviderClient;
    private VenueController venueController;

    private LocationInfo lastData = new LocationInfo();

    private int intervalSeconds = 180;

    private final VenueController.VenueListener venueListener = new VenueController.VenueListener(){
        @Override
        public void onVenueAvailable(Venue venue) {
            lastData.setVenue(venue);
            logOnFile(lastData);
        }

        @Override
        public void onVenueFailed(){
            logOnFile(lastData);
        }
    };

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null && locationResult.getLastLocation() != null) {

                Location location = locationResult.getLastLocation();

                lastData = new LocationInfo(location);

                if(venueController != null){
                    lastData.setVenuePossibleCategories(venueController.getVenueCategories());
                    venueController.getVenue(location);
                }
                else logOnFile(lastData);
            }
        }
    };

    @Override
    public void init() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        startLocationUpdates();
    }

    @Override
    public void onFirstRun(){}

    @Override
    public void onStop(){
        locationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void parseConfiguration(JsonObject configuration) {
        if(configuration.has(CONF_FIELD_VENUE_SOURCE)) {
            switch (configuration.get(CONF_FIELD_VENUE_SOURCE).getAsString()) {

                case VENUE_SOURCE_GOOGLE:
                    if(configuration.has(CONF_FIELD_VENUE_SOURCE_API)){
                        String api = configuration.get(CONF_FIELD_VENUE_SOURCE_API).getAsString();
                        VenueSource vs = GooglePlacesController.getInstance(getContext(), api);
                        venueController = new VenueController(vs, venueListener);
                        lastData.setVenuePossibleCategories(venueController.getVenueCategories());
                    }
                    break;
            }
        }

        if(configuration.has(CONF_FIELD_LOCATION_INTERNAL)){
            this.intervalSeconds = configuration.get(CONF_FIELD_LOCATION_INTERNAL).getAsInt();
        }
    }

    @Override
    public SensorData getLastData() {
        return lastData;
    }

    @Override
    public String getFeaturesHeader() {

        List<String> header = new ArrayList<>(LocationInfo.FEATURES_HEADER);

        if(venueController != null)
            header.addAll(venueController.getVenueCategories());

        return StringUtils.join(header, FileLogger.SEP);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        locationProviderClient.requestLocationUpdates(createLocationRequest(),
                locationCallback,
                Looper.getMainLooper());
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(intervalSeconds *1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }
}
