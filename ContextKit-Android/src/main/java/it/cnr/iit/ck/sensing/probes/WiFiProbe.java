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

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ck.commons.Utils;
import it.cnr.iit.ck.logs.FileLogger;
import it.cnr.iit.ck.sensing.model.SensorData;
import it.cnr.iit.ck.sensing.model.WiFiAp;

/**
 * This probe performs a continuous Wi-Fi scan to discover the Wi-Fi Access Points in proximity.
 * For each Ap, this probe reports the following information:
 *
 *      - SSID	: The AP's SSID
 *      - BSSID	: The AP's BSSID
 *      - RSSI	: The RSSI level in the [0,4] range
 *      - SIG_LEVEL	: The raw RSSI level
 *      - CAPABILITIES	: Authentication, key management, and encryption schemes supported by the AP
 *      - FREQUENCY	The primary 20 MHz frequency (in MHz) of the channel over which the client is
 *      communicating with the access point
 *      - CONNECTED	: TRUE/FALSE if the device is currently connected to the Access Point
 *      - CONFIGURED : TRUE/FALSE if the AP is in the list of already configured WIFi Networks
 *
 * Required permissions:
 *
 *      - "android.permission.ACCESS_WIFI_STATE"
 *      - "android.permission.CHANGE_WIFI_STATE"
 *      - "android.permission.ACCESS_COARSE_LOCATION"
 *      - "android.permission.ACCESS_NETWORK_STATE"
 *
 */
class WiFiProbe extends ContinuousProbe {

    private WifiManager wifiManager;
    private WiFiAp lastData = new WiFiAp();

    @Override
    public void init() {
        wifiManager = (WifiManager) getContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onFirstRun() {}

    @Override
    public void onStop() {}

    @Override
    public void parseConfiguration(JsonObject configuration) {}

    @Override
    public SensorData getLastData() {
        return lastData;
    }

    @Override
    public String getFeaturesHeader() {
        return StringUtils.join(WiFiAp.FEATURES_HEADER, FileLogger.SEP);
    }

    @Override
    public void exec() {
        if(wifiManager != null && wifiManager.isWifiEnabled()) getNearbyWifiAPs();
        else Log.e(Utils.TAG, "WiFiManager is null in "+this.getClass().getName());
    }

    @SuppressLint("MissingPermission")
    private void getNearbyWifiAPs(){
        List<WiFiAp> aps = new ArrayList<>();

        String connectedBSSID = null;

        if(wifiManager.getConnectionInfo() != null)
            connectedBSSID = wifiManager.getConnectionInfo().getBSSID();

        for(ScanResult scanResult : wifiManager.getScanResults()){
            WiFiAp ap = new WiFiAp(scanResult, connectedBSSID);
            if(ap.isConnected()) lastData = ap;
            aps.add(ap);
        }

        logOnFile(aps);
    }
}
