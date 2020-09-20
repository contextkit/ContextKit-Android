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

package it.cnr.iit.ck.sensing.model;

import android.location.Location;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.cnr.iit.ck.commons.FeaturesUtils;
import it.cnr.iit.ck.logs.FileLogger;

public class LocationInfo implements SensorData{

    public static List<String> LOG_HEADER = Arrays.asList("latitude", "longitude", "speed",
            "accuracy", "altitude", "bearing");

    public static List<String> VENUE_LOG_HEADER = Arrays.asList("venue_name", "venue_address",
            "venue_categories");

    public static List<String> FEATURES_HEADER = Arrays.asList("latitude", "longitude", "speed",
            "accuracy", "altitude", "bearing");

    private Double latitude, longitude, altitude, speed, accuracy, bearing;
    private List<String> venuePossibleCategories;
    private Venue venue = new Venue();

    public LocationInfo(){}

    public LocationInfo(Location location){
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.speed = (double) location.getSpeed();
        this.accuracy = (double) location.getAccuracy();
        this.altitude = location.getAltitude();
        this.bearing = (double) location.getBearing();
    }

    public void setVenue(Venue venue){this.venue = venue;}
    public void setVenuePossibleCategories(List<String> venuePossibleCategories){
        this.venuePossibleCategories = venuePossibleCategories;
    }

    @Override
    public String getDataToLog() {

        String data = latitude + FileLogger.SEP + longitude + FileLogger.SEP + speed
                + FileLogger.SEP + accuracy + FileLogger.SEP + altitude + FileLogger.SEP + bearing;

        if(venuePossibleCategories != null)
            data += FileLogger.SEP + venue.name + FileLogger.SEP + venue.address + FileLogger.SEP +
                    StringUtils.join(venue.types, " ");

        return data;
    }

    @Override
    public String getLogHeader() {

        List<String> header = new ArrayList<>(LOG_HEADER);
        if(venuePossibleCategories != null) header.addAll(VENUE_LOG_HEADER);

        return StringUtils.join(header, FileLogger.SEP);
    }

    @Override
    public List<Double> getFeatures() {
        List<Double> features = new ArrayList<>(Arrays.asList(latitude, longitude, altitude, speed,
                accuracy, bearing));

        if(venuePossibleCategories != null)
            features.addAll(FeaturesUtils.oneHotVector(venue.types, venuePossibleCategories));
        return features;
    }
}
