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

import it.cnr.iit.ck.logs.FileLogger;

public class HardwareInfo implements Loggable {

    private String androidID, wifiMac, wifiP2pMac, bluetoothMac, phoneBrand, phoneModel, deviceID;
    private String phoneNumber;

    public HardwareInfo(String androidID, String wifiMac, String wifiP2pMac, String bluetoothMac,
                        String phoneBrand, String phoneModel, String deviceID, String phoneNumber){

        this.androidID = androidID;
        this.wifiMac = wifiMac;
        this.wifiP2pMac = wifiP2pMac;
        this.bluetoothMac = bluetoothMac;
        this.phoneBrand = phoneBrand;
        this.phoneModel = phoneModel;
        this.deviceID = deviceID;
        this.phoneNumber = phoneNumber;
    }


    @Override
    public String getDataToLog() {

        return androidID + FileLogger.SEP + wifiMac + FileLogger.SEP + wifiP2pMac + FileLogger.SEP +
                bluetoothMac + FileLogger.SEP + phoneBrand + FileLogger.SEP + phoneModel +
                FileLogger.SEP + deviceID + FileLogger.SEP + phoneNumber;
    }
}
