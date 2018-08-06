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

import it.cnr.iit.ck.controllers.BatteryController;
import it.cnr.iit.ck.model.BatteryInfo;

/**
 * This probe monitors some information related to the batter.
 * Specifically, it monitors the following information:
 *  - battery level
 *  - if the device is charging
 *
 */
@SuppressWarnings("unused")
class BatteryProbe extends ContinuousProbe {

    @Override
    public void init() {}

    @Override
    public void onFirstRun() {}

    @Override
    void onStop() {}

    @Override
    public void exec() {

        BatteryInfo batteryInfo = BatteryController.getBatteryInfo(getContext());

        if(batteryInfo != null){
            logOnFile(true, batteryInfo);
        }
    }
}
