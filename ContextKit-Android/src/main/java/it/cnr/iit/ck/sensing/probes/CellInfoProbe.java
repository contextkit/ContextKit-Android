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

import it.cnr.iit.ck.sensing.controllers.CellController;
import it.cnr.iit.ck.sensing.model.CellsInfo;
import it.cnr.iit.ck.sensing.model.SensorData;

/**
 * This probe monitors the phone cells visible by the local device. For each cell, it reports the
 * folloing information:
 *
 *  - type of the cell (i.e., GSM, LTE, CDMA, or WCDMA)
 *  - cell's id, if available
 *  - signal level in dbm
 *
 * Requires:
 *
 *  - "android.permission.ACCESS_COARSE_LOCATION"
 *  - "android.permission.READ_PHONE_STATE"
 *
 */
@SuppressWarnings("unused")
class CellInfoProbe extends ContinuousProbe {

    @Override
    public void init() {}

    @Override
    public void onFirstRun() {}

    @Override
    public void onStop() {}

    @Override
    public void parseConfiguration(JsonObject configuration) {

    }

    @Override
    public SensorData getLastData() {
        return null;
    }

    @Override
    public String getFeaturesHeader() {
        return null;
    }

    @Override
    public void exec() {

        logOnFile(new CellsInfo(CellController.getVisibleCells(getContext())));
    }
}
