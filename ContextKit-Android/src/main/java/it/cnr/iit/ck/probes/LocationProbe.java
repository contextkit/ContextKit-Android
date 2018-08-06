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

package it.cnr.iit.ck.probes;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Arrays;

import it.cnr.iit.ck.model.LocationInfo;

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
 *      - position's timestamp
 *
 * Requires:
 *
 *  - "com.google.android.gms.permission.ACCESS_COARSE_LOCATION"
 *
 */
@SuppressWarnings("unused")
class LocationProbe extends ContinuousProbe {

    private FusedLocationProviderClient locationProviderClient;

    @Override
    public void init() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public void onFirstRun() {}

    @Override
    void onStop() {

    }

    @Override
    public void exec() {
        getLastKnownLocation();
    }

    @SuppressWarnings("all")
    private void getLastKnownLocation(){

        locationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            logOnFile(true, new LocationInfo(location));
                        }
                    }
                });
    }
}
