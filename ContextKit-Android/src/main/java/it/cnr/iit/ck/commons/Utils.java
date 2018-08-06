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

package it.cnr.iit.ck.commons;

import android.net.wifi.WifiManager;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final String TAG = "CK";

    public static WifiManager.WifiLock acquireWifiLock(WifiManager wifiManager,
                                                        WifiManager.WifiLock wifiLock,
                                                        String lockKey) {

        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, lockKey);
        wifiLock.setReferenceCounted(false);
        wifiLock.acquire();

        return wifiLock;
    }

    public static WifiManager.WifiLock releaseWifiLock(WifiManager.WifiLock wifiLock) {

        if (wifiLock != null) {
            if (wifiLock.isHeld()) {
                wifiLock.release();
            }
            wifiLock = null;
        }

        return wifiLock;
    }

    public static String formatLogOutput(Object...data){

        List<String> list = new ArrayList<>();
        for(Object o : data) list.add(String.valueOf(o));

        return StringUtils.join(list, ",");
    }
}
