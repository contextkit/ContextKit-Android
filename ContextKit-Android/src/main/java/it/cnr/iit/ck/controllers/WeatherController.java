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

package it.cnr.iit.ck.controllers;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import it.cnr.iit.ck.controllers.network.GsonRequest;
import it.cnr.iit.ck.model.OpenWeatherData;

public class WeatherController {

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";

    private static final int UNIT_KELVIN = 1;
    private static final int UNIT_METRIC = 2;
    private static final int UNIT_IMPERIAL = 3;

    public static void getWeatherByCoordinates(Context context, final WeatherListener listener,
                                               double latitude, double longitude, int unit,
                                               String appId){

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = BASE_URL.concat("lat=").concat(String.valueOf(latitude))
                .concat("&lon=").concat(String.valueOf(longitude)).concat("&APPID=").concat(appId);

        switch (unit){
            //Kelvin is the default unit for openweathermap, there is no need to specify it
            case UNIT_KELVIN:
                break;

            case UNIT_METRIC:
                url = url.concat("&units=metric");
                break;

            case UNIT_IMPERIAL:
                url = url.concat("&units=imperial");
                break;
        }

        GsonRequest<OpenWeatherData> request = new GsonRequest<>(url,
                OpenWeatherData.class,
                null,
                new Response.Listener<OpenWeatherData>() {
                    @Override
                    public void onResponse(OpenWeatherData response) {
                        listener.onWeatherAvailable(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onWeatherFailed(error.getMessage());
                    }});

        queue.add(request);
    }

    public interface WeatherListener{
        void onWeatherAvailable(OpenWeatherData data);
        void onWeatherFailed(String reason);
    }

}
