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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class WiFiScanInfo implements Loggable{

    public List<WiFiAp> wiFiAps = new ArrayList<>();

    public void addAp(String SSID, String BSSID, int signalLevel, int dbmLevel, String capabilities,
                      int frequency, boolean connected, boolean configured){

        this.wiFiAps.add(new WiFiAp(SSID, BSSID, signalLevel, dbmLevel, capabilities, frequency,
                connected, configured));
    }

    @Override
    public String getDataToLog() {

        List<String> apsData = new ArrayList<>();
        for(WiFiAp ap : wiFiAps) apsData.add(ap.getDataToLog());

        return StringUtils.join(apsData, "\t");
    }


    class WiFiAp implements Loggable{

        private String SSID, BSSID, capabilities;
        private int signalLevel, dbmLevel, frequency;
        private boolean connected, configured;

        public WiFiAp(String SSID, String BSSID, int signalLevel, int dbmLevel, String capabilities,
                      int frequency, boolean connected, boolean configured){

            this.SSID = SSID;
            this.BSSID = BSSID;
            this.signalLevel = signalLevel;
            this.dbmLevel = dbmLevel;
            this.capabilities = capabilities;
            this.frequency = frequency;
            this.connected = connected;
            this.configured = configured;
        }

        @Override
        public String getDataToLog() {
            return SSID + "," + BSSID + "," + signalLevel + "," + dbmLevel + "," + capabilities +
                    frequency + "," + connected + "," + configured;
        }
    }

}
