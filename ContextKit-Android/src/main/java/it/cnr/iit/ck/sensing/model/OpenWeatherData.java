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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.cnr.iit.ck.commons.FeaturesUtils;
import it.cnr.iit.ck.logs.FileLogger;

public class OpenWeatherData implements SensorData {

    public static List<String> LOG_HEADER = Arrays.asList("weather_id", "temp", "temp_min",
            "temp_max", "humidity", "pressure", "wind_speed", "wind_degree", "cloudiness");

    private List<WeatherField> weather;
    private MainInfo main;
    private WindField wind;
    private CloudsField clouds;

    private List<String> weatherConditions;

    @Override
    public String getDataToLog() {

        return  (weather != null ? String.valueOf(weather.get(0).id) : 0) + FileLogger.SEP +
                (main != null ? String.valueOf(main.temp) : 0) + FileLogger.SEP +
                (main != null ? String.valueOf(main.temp_min) : 0) + FileLogger.SEP +
                (main != null ? String.valueOf(main.temp_max) : 0) + FileLogger.SEP +
                (main != null ? String.valueOf(main.humidity) : 0) + FileLogger.SEP +
                (main != null ? String.valueOf(main.pressure) : 0) + FileLogger.SEP +
                (wind != null ? String.valueOf(wind.speed) : 0) + FileLogger.SEP +
                (wind != null ? String.valueOf(wind.deg) : 0) + FileLogger.SEP +
                (clouds != null ? String.valueOf(clouds.all) : 0);
    }

    @Override
    public String getLogHeader() {
        return StringUtils.join(LOG_HEADER, FileLogger.SEP);
    }

    @Override
    public List<Double> getFeatures() {

        List<Double> features = new ArrayList<>(FeaturesUtils.oneHotVector(String.valueOf(
                weather.get(0).id), weatherConditions));

        Collections.addAll(features, Double.valueOf(main.temp), Double.valueOf(main.temp_min),
                Double.valueOf(main.temp_max), Double.valueOf(main.humidity),
                Double.valueOf(main.pressure), Double.valueOf(wind.speed),
                Double.valueOf(wind.deg), Double.valueOf(clouds.all));

        return features;
    }

    public void setWeatherConditions(List<String> weatherConditions){
        this.weatherConditions = weatherConditions;
    }

    private static class WeatherField{
        // Weather condition codes: http://www.openweathermap.com/weather-conditions
        Integer id;
    }
    private static class MainInfo{ Float temp, temp_min, temp_max, humidity, pressure; }
    private static class WindField{ Float speed, deg; }
    private static class CloudsField{ Float all; }
}
