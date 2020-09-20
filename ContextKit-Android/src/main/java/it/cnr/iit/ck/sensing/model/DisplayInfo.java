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

import android.view.Display;
import android.view.Surface;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import it.cnr.iit.ck.commons.FeaturesUtils;
import it.cnr.iit.ck.logs.FileLogger;

/**
 * {@link DisplayInfo#state} can be:
 *      - {@link Display#STATE_ON}
 *      - {@link Display#STATE_OFF}
 *      - {@link Display#STATE_DOZE}
 *      - {@link Display#STATE_DOZE_SUSPEND}
 *      - {@link Display#STATE_UNKNOWN}
 *
 *
 * {@link DisplayInfo#rotation} can be:
 *      - {@link Surface#ROTATION_0}
 *      - {@link Surface#ROTATION_90}
 *      - {@link Surface#ROTATION_180}
 *      - {@link Surface#ROTATION_270}
 */
public class DisplayInfo implements SensorData{

    public static List<String> LOG_HEADER = Arrays.asList("state", "rotation");

    public static List<String> FEATURES_HEADER = Arrays.asList("display_state_unknown",
            "display_state_off", "display_state_on", "display_state_doze",
            "display_state_doze_suspended", "display_rotation_0", "display_rotation_90",
            "display_rotation_180", "display_rotation_270");

    private static final List<Integer> DISPLAY_MODES = Arrays.asList(Display.STATE_UNKNOWN,
            Display.STATE_OFF, Display.STATE_ON, Display.STATE_DOZE, Display.STATE_DOZE_SUSPEND);
    private static final List<Integer> ROTATION_STATUSES = Arrays.asList(Surface.ROTATION_0,
            Surface.ROTATION_90, Surface.ROTATION_180, Surface.ROTATION_270);

    private int state, rotation;

    public DisplayInfo(){}

    public DisplayInfo(Display display){
        this.state = display.getState();
        this.rotation = display.getRotation();
    }

    @Override
    public String getDataToLog() {
        return state + FileLogger.SEP + rotation;
    }

    @Override
    public String getLogHeader() {
        return StringUtils.join(LOG_HEADER, FileLogger.SEP);
    }

    @Override
    public List<Double> getFeatures() {
        List<Double> features = FeaturesUtils.oneHotVector(state, DISPLAY_MODES);
        features.addAll(FeaturesUtils.oneHotVector(rotation, ROTATION_STATUSES));
        return features;
    }
}
