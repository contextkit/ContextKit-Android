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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.Arrays;

import it.cnr.iit.ck.commons.Utils;
import it.cnr.iit.ck.controllers.HardwareInfoController;
import it.cnr.iit.ck.model.HardwareInfo;

/**
 * Fetches different information related to the device's hardware.
 * Some information can be used to uniquely identify the user/device. Specifically, this probe
 * fetches the following information:
 *
 *      - ANDROID_ID : A 64-bit number, unique to each combination of app-signing key, user, and
 *      device. This is only available on Android API >= 26.
 *      - WI_FI_MAC	: The MAC address associated to the Wi-Fi interface
 *      - WI_FI_P2P_MAC	: The MAC address associated to the Wi-Fi P2P interface
 *      - BT_MAC : The MAC address associated to the Bluetooth interface
 *      - BRAND	: The device's brand
 *      - MODEL	: The device's model
 *      - MANUFACTURER : The device's manufacturer
 *      - DEVICE_ID	: The unique device ID (i.e., the IMEI for GSM and the MEID or ESN for
 *      CDMA phones)
 *
 *  * Requires:
 *  - {@link android.Manifest.permission#ACCESS_WIFI_STATE}
 *  - {@link android.Manifest.permission#CHANGE_WIFI_STATE}
 *  - {@link android.Manifest.permission#BLUETOOTH}
 *  - {@link android.Manifest.permission#READ_PHONE_STATE}
 *  - {@link android.Manifest.permission#INTERNET}
 */
@SuppressWarnings("unused")
class HardwareInfoProbe extends ContinuousProbe {

    private static final String WIFI_LOCK_KEY = HardwareInfoProbe.class.getName();

    private String wifiP2pMac;
    //private WifiManager.WifiLock wifiLock;

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction() != null &&
                    intent.getAction().equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)){

                WifiP2pDevice device = intent.getParcelableExtra(
                        WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

                wifiP2pMac= device.deviceAddress;
                fetchInfo();
                stop();
            }
        }
    };

    @Override
    public void init() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        getContext().registerReceiver(wifiReceiver, intentFilter);
    }

    @Override
    public void onFirstRun() {}

    @Override
    void onStop() {
        try {
            getContext().unregisterReceiver(wifiReceiver);
        }catch (IllegalArgumentException e){
            Log.e(Utils.TAG, "HardwareInfoProbe - Receiver not registered.");
        }
        //wifiLock = Utils.releaseWifiLock(wifiLock);
    }

    @Override
    @SuppressWarnings("all")
    public void exec() {

        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        if(wifiManager != null) {

            //wifiLock = Utils.acquireWifiLock(wifiManager, wifiLock, WIFI_LOCK_KEY);

            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
            wifiManager.setWifiEnabled(true);
        }

    }

    private void fetchInfo(){

        logOnFile(true, new HardwareInfo(
                HardwareInfoController.getAndroidID(getContext()),
                HardwareInfoController.getWiFiMac(),
                wifiP2pMac,
                HardwareInfoController.getBTMac(getContext()),
                HardwareInfoController.getPhoneBrand(),
                HardwareInfoController.getPhoneModel(),
                HardwareInfoController.getDeviceID(getContext()),
                HardwareInfoController.getPhoneNumber(getContext())));
    }
}
