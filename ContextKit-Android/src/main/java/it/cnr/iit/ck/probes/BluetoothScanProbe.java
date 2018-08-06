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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.HashSet;
import java.util.Set;

import it.cnr.iit.ck.controllers.BluetoothController;
import it.cnr.iit.ck.model.BTDevice;
import it.cnr.iit.ck.model.LoggableElements;

/**
 * This probe performs a continuous Bluetooth scan and reports both name and address of the BT
 * devices in proximity.
 *
 * Requires:
 *
 *  - "android.permission.BLUETOOTH"
 *  - "android.permission.BLUETOOTH_ADMIN"
 *
 */
@SuppressWarnings("unused")
class BluetoothScanProbe extends ContinuousProbe {

    private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private Set<BTDevice> devices = new HashSet<>();

    private BroadcastReceiver btReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)){

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (byte)100);

                devices.add(new BTDevice(device, rssi));
            }
        }
    };

    private BroadcastReceiver btStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        performTask();
                        break;
                }
            }
        }
    };

    @Override
    public void init() {
        getContext().registerReceiver(btReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        getContext().registerReceiver(btStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    public void onFirstRun() {}

    @SuppressWarnings("all")
    @Override
    void onStop() {
        getContext().unregisterReceiver(btReceiver);
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
    }

    @Override
    public void exec() {

        boolean waitBtEnable=false;

        if(!BluetoothController.isBluetoothEnabled()){
            BluetoothController.setBluetooth(true);
            waitBtEnable = true;
        }

        if(!waitBtEnable) performTask();
    }

    @SuppressWarnings("all")
    private void performTask(){
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        logOnFile(true, new LoggableElements(devices));

        devices = new HashSet<>();
        mBtAdapter.startDiscovery();
    }
}
