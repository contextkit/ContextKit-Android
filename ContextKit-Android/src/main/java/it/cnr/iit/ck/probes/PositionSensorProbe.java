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

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import it.cnr.iit.ck.controllers.SensorMonitor;
import it.cnr.iit.ck.model.SensorsStats;

/**
 * Monitors sensors that measure the physical position of a device. This category includes
 * orientation sensors and magnetometers.
 * Specifically, this probe monitors the following sensors:
 *
 *      - GAME ROTATION : Rotation vector component along the x,y,z axes
 *      - GEOMAGNETIC ROTATION : Rotation vector component along the x,y,z axes
 *      - MAGNETIC FIELD : Geomagnetic field strength along the x,y,z axes in μT
 *      - PROXIMITY : Distance from object in cm
 *
 *  Parameters:
 *
 *      - "maxSamples" : controls the maximum number of samples for each sensor used to calculate
 *      the statistics. The default value is 200 samples.
 *
 */
@SuppressWarnings("unused")
class PositionSensorProbe extends ContinuousProbe {

    private static final int DEFAULT_MAX_ELEMENTS = 200;

    private static final int[] MOTION_SENSORS = new int[]{
            Sensor.TYPE_GAME_ROTATION_VECTOR,
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_PROXIMITY
    };

    private SensorMonitor sensorMonitor;

    private int maxSamples = DEFAULT_MAX_ELEMENTS;

    @Override
    public void init() {

        sensorMonitor = new SensorMonitor(getContext(), MOTION_SENSORS.length);

        for (int sensorId : MOTION_SENSORS) {

            int dimensions = (sensorId == Sensor.TYPE_PROXIMITY) ? 1 : 3;

            sensorMonitor.registerSensor(sensorId, SensorManager.SENSOR_DELAY_NORMAL,
                    dimensions, maxSamples);
        }
    }

    @Override
    public void onFirstRun() {}

    @Override
    void onStop() {
        for (int sensorId : MOTION_SENSORS) sensorMonitor.unRegisterSensor(sensorId);
    }

    @Override
    public void exec() {
        printStatistics();
    }

    private void printStatistics(){

        double stats[] = null;

        for (int sensorId : MOTION_SENSORS) {

            double[] sensorData = sensorMonitor.getStats(sensorId);

            if(stats == null) stats = sensorData;
            else stats = ArrayUtils.addAll(stats, sensorData);

            sensorMonitor.resetSamples(sensorId);
        }

        if(stats != null) logOnFile(true, new SensorsStats(stats));
    }
}