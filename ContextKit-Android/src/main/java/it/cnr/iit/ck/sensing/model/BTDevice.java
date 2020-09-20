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

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;

import it.cnr.iit.ck.logs.FileLogger;

public class BTDevice implements Comparable<BTDevice>, Comparator<BTDevice>, SensorData{

    public static final String[] LOG_HEADER = {"device_name", "physical_address", "device_class",
            "rssi,still"};

    private String name, address;
    private int deviceClass;
    private Short rssi;

    @SuppressLint("MissingPermission")
    public BTDevice(BluetoothDevice device, Short rssi){
        this.name = device.getName();
        this.address = device.getAddress();
        this.deviceClass = device.getBluetoothClass().getDeviceClass();
        this.rssi = rssi;
    }

    @Override
    public int compareTo(@NonNull BTDevice o) {
        return this.address.compareTo(o.address);
    }

    @Override
    public int compare(BTDevice o1, BTDevice o2) {
        return o1.address.compareTo(o2.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BTDevice && ((BTDevice)obj).address.equals(address);
    }

    @Override
    public String getDataToLog() {
        return name + FileLogger.SEP + address + FileLogger.SEP + deviceClass + FileLogger.SEP + rssi;
    }

    @Override
    public String getLogHeader() {
        return StringUtils.join(LOG_HEADER, FileLogger.SEP);
    }

    @Override
    public List<Double> getFeatures() { return null; }
}
