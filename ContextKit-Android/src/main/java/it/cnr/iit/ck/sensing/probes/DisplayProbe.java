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

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;

import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import it.cnr.iit.ck.logs.FileLogger;
import it.cnr.iit.ck.sensing.model.DisplayInfo;
import it.cnr.iit.ck.sensing.model.SensorData;

/**
 * This probe monitors the display status, i.e., if it is on and its current rotation grade.
 *
 */
@SuppressWarnings("unused")
class DisplayProbe extends ContinuousProbe {

    private DisplayManager displayManager;
    private DisplayInfo lastData = new DisplayInfo();

    @Override
    public void init() {
        displayManager = (DisplayManager) getContext().getSystemService(Context.DISPLAY_SERVICE);
    }

    @Override
    public void onFirstRun() {}

    @Override
    public void onStop() {}

    @Override
    public void exec() {
        lastData = new DisplayInfo(displayManager.getDisplay(Display.DEFAULT_DISPLAY));
        logOnFile(lastData);
    }

    @Override
    public void parseConfiguration(JsonObject configuration) {}

    @Override
    public SensorData getLastData() { return lastData; }

    @Override
    public String getFeaturesHeader() {
        return StringUtils.join(DisplayInfo.FEATURES_HEADER, FileLogger.SEP);
    }
}
