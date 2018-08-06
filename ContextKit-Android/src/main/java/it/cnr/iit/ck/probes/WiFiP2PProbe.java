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
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.cnr.iit.ck.commons.Utils;
import it.cnr.iit.ck.model.WifiP2PData;

/**
 * This probe performs a continuous Wi-Fi P2P Peer discovery to monitor the other devices in
 * proximity. For each device, this probe reports its Wi-Fi P2P Mac address.
 *
 * Required permissions:
 *
 *      - "android.permission.ACCESS_COARSE_LOCATION"
 *      - "android.permission.ACCESS_WIFI_STATE"
 *      - "android.permission.CHANGE_WIFI_STATE"
 *      - "android.permission.INTERNET"
 *
 */
@SuppressWarnings("unused")
class WiFiP2PProbe extends ContinuousProbe {

    private static final String LOCK_KEY = WiFiP2PProbe.class.getName();
    private static final int DISCOVERY_RESTART_DELAY = 5;
    private static final int DISCOVERY_DURATION = 60;

    private WifiP2pManager.Channel wifiP2pChannel;
    private WifiP2pManager wifiP2pManager;
    private WifiManager wifiManager;
    //private WifiManager.WifiLock wifiLock;
    private List<String> addressList = new ArrayList<>();

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {

        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            Collection<WifiP2pDevice> peers = peerList.getDeviceList();

            if(peers != null){

                for(WifiP2pDevice device : peers){
                    if(!addressList.contains(device.deviceAddress))
                        addressList.add(device.deviceAddress);
                }

            }else{
                Log.d(Utils.TAG, "Peers null");
            }
        }
    };

    @SuppressWarnings("all")
    private BroadcastReceiver eventsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

                if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                    Log.e(Utils.TAG, "The WiFi P2P is disabled.");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            wifiManager.setWifiEnabled(true);
                            startPeerDiscovery();
                        }
                    }, DISCOVERY_RESTART_DELAY*1000);
                }

            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

                if (wifiP2pManager != null) {
                    wifiP2pManager.requestPeers(wifiP2pChannel, peerListListener);
                }

            }
        }
    };

    @Override
    public void init() {

        wifiManager = (WifiManager) getContext()
                .getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiP2pManager = (WifiP2pManager) getContext().getSystemService(Context.WIFI_P2P_SERVICE);

        wifiP2pChannel = wifiP2pManager.initialize(getContext(),
                getContext().getMainLooper(), null);

    }

    @Override
    public void onFirstRun() {}

    @Override
    void onStop() {
        stopPeerDiscovery();
        //wifiLock = Utils.releaseWifiLock(wifiLock);

        logOnFile(true, new WifiP2PData(addressList));
        this.addressList = new ArrayList<>();
    }

    @Override
    public void exec() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        getContext().registerReceiver(eventsReceiver, intentFilter);

        //wifiLock = Utils.acquireWifiLock(wifiManager, wifiLock, LOCK_KEY);
        startPeerDiscovery();

    }

    private void startPeerDiscovery(){

        Log.d(Utils.TAG, "Start peer discovery");

        wifiP2pManager.discoverPeers(wifiP2pChannel, new WifiP2pManager.ActionListener() {

            @SuppressWarnings("all")
            @Override
            public void onSuccess() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        wifiManager.setWifiEnabled(true);
                        stop();
                    }
                }, DISCOVERY_DURATION*1000);
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.e(Utils.TAG, "Failure starting WiFi P2P Peer Discovery." +
                        "Reason: "+reasonCode);
            }
        });
    }

    private void stopPeerDiscovery(){

        wifiP2pManager.stopPeerDiscovery(wifiP2pChannel, null);

    }
}
