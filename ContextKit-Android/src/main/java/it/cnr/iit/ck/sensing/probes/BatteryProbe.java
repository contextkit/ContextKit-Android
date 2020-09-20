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

import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import it.cnr.iit.ck.sensing.controllers.BatteryController;
import it.cnr.iit.ck.logs.FileLogger;
import it.cnr.iit.ck.sensing.model.BatteryInfo;
import it.cnr.iit.ck.sensing.model.SensorData;

/**
 * This probe monitors some information related to the batter.
 * Specifically, it monitors the following information:
 *  - battery level
 *  - if the device is charging
 *
 */
@SuppressWarnings("unused")
class BatteryProbe extends ContinuousProbe {

    private BatteryInfo lastData = new BatteryInfo();

    @Override
    public void init() {}

    @Override
    public void onFirstRun() {}

    @Override
    public void onStop() {}

    @Override
    public void parseConfiguration(JsonObject configuration) {}

    @Override
    public SensorData getLastData() { return lastData; }

    @Override
    public String getFeaturesHeader() {
        return StringUtils.join(BatteryInfo.FEATURES_HEADER, FileLogger.SEP);
    }

    @Override
    public void exec() {
        lastData = BatteryController.getBatteryInfo(getContext());
        logOnFile(lastData);
    }
}
