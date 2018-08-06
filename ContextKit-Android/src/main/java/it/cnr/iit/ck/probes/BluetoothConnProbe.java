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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.HashSet;
import java.util.Set;

import it.cnr.iit.ck.model.BTDevice;
import it.cnr.iit.ck.model.BTDevices;

/**
 * This probe monitors the Bluetooth connections. Reports both the name and address of each
 * connected device.
 *
 * Requires:
 *
 *  - "android.permission.BLUETOOTH"
 */
@SuppressWarnings("all")
class BluetoothConnProbe extends OnEventProbe {

    private Set<BTDevice> connectedDevices = new HashSet<>();
    private BTEventsReceiver receiver;
    private int receivedProxies = 0;
    private boolean btEnabled = false;

    private BluetoothProfile.ServiceListener initialServiceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {

            for(BluetoothDevice device : proxy.getConnectedDevices()){
                connectedDevices.add(new BTDevice(device, null));
            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
            receivedProxies++;
            if(receivedProxies == btProfiles.length) printData();
        }

        @Override
        public void onServiceDisconnected(int profile) {}
    };

    private int[] btProfiles = {BluetoothProfile.A2DP, BluetoothProfile.HEADSET,
            BluetoothProfile.HEALTH};

    @Override
    public void init() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        receiver = new BTEventsReceiver();
        getContext().registerReceiver(receiver, intentFilter);

        getAlreadyConnectedDevices();
    }

    private void getAlreadyConnectedDevices(){

        for(int btProfile : btProfiles)
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(getContext(),
                    initialServiceListener, btProfile);
    }

    @Override
    public void onFirstRun() {}

    @Override
    void onStop() {

        getContext().unregisterReceiver(receiver);
    }

    private void printData(){
        logOnFile(true, new BTDevices(connectedDevices));
    }

    class BTEventsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if(action != null) {

                BTDevice device;

                switch (action) {

                    case BluetoothDevice.ACTION_ACL_CONNECTED:

                        device = new BTDevice((BluetoothDevice) intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE), null);

                        if(!connectedDevices.contains(device)) {
                            connectedDevices.add(device);
                            printData();
                        }

                        break;

                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:

                        device = new BTDevice((BluetoothDevice) intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE), null);

                        if(connectedDevices.contains(device)) {
                            connectedDevices.remove(device);
                            printData();
                        }
                        break;
                }
            }

        }
    }
}
