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

package it.cnr.iit.ck.sensing.controllers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.SparseArray;

import it.cnr.iit.ck.sensing.model.SensorSamples;

public class SensorMonitor {

    @SuppressWarnings("all")
    private SparseArray<SensorSamples> data;
    private SensorManager sensorManager;

    private final SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            data.get(event.sensor.getType()).newSample(event.values);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    public SensorMonitor(Context context, int capacity){
        data = new SparseArray<>(capacity);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void registerSensor(int sensorId, int accuracy, int dimensions, int maxElements){

        data.put(sensorId, new SensorSamples(dimensions, maxElements));

        Sensor sensor = sensorManager.getDefaultSensor(sensorId);
        if (sensor != null)
            sensorManager.registerListener(listener, sensor, accuracy);
    }

    public void unRegisterSensor(int sensorId){

        sensorManager.unregisterListener(listener, sensorManager.getDefaultSensor(sensorId));
        data.remove(sensorId);
    }

    public double[] getStats(int sensorId){
        return data.get(sensorId).getStatistics();
    }

    public void resetSamples(int sensorId){

        if(sensorId != Sensor.TYPE_LIGHT)
             data.get(sensorId).reset();
    }
}
