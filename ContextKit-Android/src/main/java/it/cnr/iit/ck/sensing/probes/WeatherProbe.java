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

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.cnr.iit.R;
import it.cnr.iit.ck.commons.Utils;
import it.cnr.iit.ck.sensing.controllers.WeatherController;
import it.cnr.iit.ck.logs.FileLogger;
import it.cnr.iit.ck.sensing.model.OpenWeatherData;
import it.cnr.iit.ck.sensing.model.SensorData;

/**
 * This probe monitors the current weather conditions (e.g., temperature, humidity, etc.) at the
 * current device location. Weather information comes from the API service of
 * http://www.openweathermap.com
 *
 * Returns the following information:
 *
 *  - the weather conditions id, according to http://www.openweathermap.com/weather-conditions
 *  - temperature
 *  - minimum temperature
 *  - maximum temperature
 *  - humidity
 *  - pressure
 *  - wind speed
 *  - wind direction in degrees
 *  - percentage of cloudiness
 *  - rain volume for the last 3 hours
 *  - snow volume for the last 3 hour
 *  - sunrise time (unix UTC timestamp)
 *  - sunset time (unix UTC timestamp)
 *
 * Requires:
 *
 *  - "com.google.android.gms.permission.ACCESS_FINE_LOCATION"
 *  - "android.permission.INTERNET"
 *
 */
public class WeatherProbe extends ContinuousProbe {

    private FusedLocationProviderClient locationProviderClient;
    private int unit;
    private String appId;

    private List<String> weatherConditions;

    private OpenWeatherData lastData = new OpenWeatherData();

    private WeatherController.WeatherListener listener = new WeatherController.WeatherListener(){

        @Override
        public void onWeatherAvailable(OpenWeatherData data) {

            lastData = data;
            lastData.setWeatherConditions(weatherConditions);
            logOnFile(data);
        }

        @Override
        public void onWeatherFailed(String reason) {
            Log.d(Utils.TAG, WeatherProbe.class.getName() + " failure: "+reason);
        }
    };

    @Override
    public void init() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public void onFirstRun() {}

    @Override
    public void onStop() {}

    @Override
    public void parseConfiguration(JsonObject configuration) {}

    @Override
    public SensorData getLastData() {
        return lastData;
    }

    @Override
    public String getFeaturesHeader() {

        int[] codes = getContext().getResources().getIntArray(R.array.weather_conditions_codes);
        weatherConditions = new ArrayList<>();
        for(int c : codes) weatherConditions.add(String.valueOf(c));
        Collections.sort(weatherConditions);

        List<String> header = new ArrayList<>();
        for(String wc : weatherConditions) header.add("weather_"+wc);

        List<String> header2 = new ArrayList<>(OpenWeatherData.LOG_HEADER);
        header2.remove(0);
        header.addAll(header2);

        return StringUtils.join(header, FileLogger.SEP);
    }

    @Override
    @SuppressWarnings("all")
    public void exec() {

        locationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            WeatherController.getWeatherByCoordinates(getContext(), listener,
                                    location.getLatitude(), location.getLongitude(), unit, appId);
                        }
                    }
                });
    }
}
