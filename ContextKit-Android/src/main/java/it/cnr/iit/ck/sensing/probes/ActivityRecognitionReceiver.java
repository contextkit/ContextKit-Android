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

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionResult;

import it.cnr.iit.ck.sensing.model.ActivityRecognitionData;

/**
 * This IntentService has been used by the {@link ActivityRecognitionProbe} to receive the intents
 * sent by the Android Activity Recognition.
 * Each activity is associated with a confidence level, which is an int between 0 and 100.
 *
 */
public class ActivityRecognitionReceiver extends IntentService {

    private static final String TAG = ActivityRecognitionReceiver.class.getName();

    public ActivityRecognitionReceiver(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        if(result != null) {

            ActivityRecognitionData data = new ActivityRecognitionData(result);

            if(intent != null) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(ActivityRecognitionProbe.AR_AVAILABLE_MSG);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(ActivityRecognitionProbe.AR_DATA, data);
                sendBroadcast(broadcastIntent);
            }
        }
    }
}
