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

package it.cnr.iit.ck.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import it.cnr.iit.ck.logs.FileLogger;

public class OpenWeatherData implements Loggable {

    private List<WeatherField> weather;
    private MainInfo main;
    private WindField wind;
    private CloudsField clouds;
    private RainField rain;
    private SnowField snow;
    private SysField sys;

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
                (clouds != null ? String.valueOf(clouds.all) : 0) + FileLogger.SEP +
                (rain != null ? String.valueOf(rain.last3hours) : 0) + FileLogger.SEP +
                (snow != null ? String.valueOf(snow.last3hours) : 0) + FileLogger.SEP +
                (sys != null ? String.valueOf(sys.sunrise) : 0) + FileLogger.SEP +
                (sys != null ? String.valueOf(sys.sunset) : 0);
    }

    private class WeatherField{
        // Weather condition codes: http://www.openweathermap.com/weather-conditions
        int id;
    }

    private class MainInfo{ float temp, temp_min, temp_max, humidity, pressure; }

    private class WindField{ float speed, deg; }

    private class CloudsField{ float all; }

    private class RainField{ @SerializedName("3h") float last3hours; }

    private class SnowField{ @SerializedName("3h") float last3hours; }

    private class SysField{ long sunrise, sunset; }
}
