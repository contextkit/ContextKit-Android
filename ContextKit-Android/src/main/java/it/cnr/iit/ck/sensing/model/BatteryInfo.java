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

import android.os.BatteryManager;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import it.cnr.iit.ck.commons.FeaturesUtils;
import it.cnr.iit.ck.logs.FileLogger;

public class BatteryInfo implements SensorData{

    public static List<String> LOG_HEADER = Arrays.asList("battery_level", "recharge_mode");

    public static List<String> FEATURES_HEADER = Arrays.asList("battery_not_plugged",
            "battery_plugged_ac", "battery_plugged_usb", "battery_plugged_wireless");

    private static final int NOT_PLUGGED = 0;
    private static final List<Integer> PLUG_MODES = Arrays.asList(NOT_PLUGGED,
            BatteryManager.BATTERY_PLUGGED_AC, BatteryManager.BATTERY_PLUGGED_USB,
            BatteryManager.BATTERY_PLUGGED_WIRELESS);

    private Float batteryPercentage;
    private Integer plugged;

    public BatteryInfo(){}
    public BatteryInfo(float batteryPercentage, int plugged){

        this.plugged = plugged;
        this.batteryPercentage = batteryPercentage;
    }

    @Override
    public String getDataToLog() {
        return batteryPercentage + FileLogger.SEP + plugged;
    }

    @Override
    public String getLogHeader() {
        return StringUtils.join(LOG_HEADER, FileLogger.SEP);
    }

    @Override
    public List<Double> getFeatures() {
        return plugged != null? FeaturesUtils.oneHotVector(plugged, PLUG_MODES) : null;
    }
}
