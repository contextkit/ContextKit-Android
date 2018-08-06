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

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;

/**
 * This probe monitors the user's current activity using the {@link ActivityRecognition} class.
 * The monitored activities are those defined in {@link DetectedActivity} class, such as
 * {@link DetectedActivity#IN_VEHICLE}, {@link DetectedActivity#WALKING}, and so on.
 * Each activity is associated with a confidence level, which is an int between 0 and 100.
 *
 * In order to use this probe, the following lines should be included in the app's Manifest:
 *
 * <service
 * android:name="it.matbell.ask.probes.SKActivityRecognitionReceiver"
 * android:exported="false" />
 *
 * Parameters:
 *
 *  - "updateInterval" : Controls the activity detection update interval (in seconds). Larger values
 *  will result in fewer activity detections while improving battery life. Smaller values will
 *  result in more frequent activity detections but will consume more power since the device must be
 *  woken up more frequently. The default value is 300 seconds.
 *
 * Required permissions:
 *
 *  - "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
 */
class ActivityRecognitionProbe extends OnEventProbe {

    private static final int DEFAULT_UPDATE_INTERVAL = 300;
    static final String LOG_INTENT_FIELD = "logFile";
    private ActivityRecognitionClient client;
    private PendingIntent pendingIntent;

    @SuppressWarnings("all")
    private long updateInterval = DEFAULT_UPDATE_INTERVAL;

    @SuppressWarnings("all")
    @Override
    public void init() {

        pendingIntent = getActivityDetectionIntent();

        client = ActivityRecognition.getClient(getContext());
        client.requestActivityUpdates(updateInterval*1000, pendingIntent);
    }

    @Override
    public void onFirstRun() {}

    @SuppressWarnings("all")
    @Override
    void onStop() {

        if(pendingIntent != null && client != null)
            client.removeActivityUpdates(pendingIntent);
    }

    private PendingIntent getActivityDetectionIntent() {
        Intent intent = new Intent(getContext(), ActivityRecognitionReceiver.class);
        intent.putExtra(LOG_INTENT_FIELD, logFile);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        pendingIntent = PendingIntent.getService(getContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}
