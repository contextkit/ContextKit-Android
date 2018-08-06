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

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ActivityRecognitionData implements Loggable {

    private int[] act = new int[8];

    public ActivityRecognitionData(ActivityRecognitionResult result){

        List<DetectedActivity> activities = result.getProbableActivities();

        for (DetectedActivity da : activities) {

            int pos = -1;

            switch (da.getType()) {
                case DetectedActivity.IN_VEHICLE:
                    pos = 0;
                    break;

                case DetectedActivity.ON_BICYCLE:
                    pos = 1;
                    break;

                case DetectedActivity.ON_FOOT:
                    pos = 2;
                    break;

                case DetectedActivity.RUNNING:
                    pos = 3;
                    break;

                case DetectedActivity.STILL:
                    pos = 4;
                    break;

                case DetectedActivity.TILTING:
                    pos = 5;
                    break;

                case DetectedActivity.WALKING:
                    pos = 6;
                    break;

                case DetectedActivity.UNKNOWN:
                    pos = 7;
                    break;
            }

            if (pos != -1) act[pos] = da.getConfidence();
        }

    }

    @Override
    public String getDataToLog() {
        return StringUtils.join(ArrayUtils.toObject(act), "\t");
    }
}
