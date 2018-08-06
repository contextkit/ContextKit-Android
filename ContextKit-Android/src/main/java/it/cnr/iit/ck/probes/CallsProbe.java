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
import android.telephony.TelephonyManager;

import java.util.Date;

import it.cnr.iit.ck.controllers.CallsController;
import it.cnr.iit.ck.controllers.ContactsController;
import it.cnr.iit.ck.model.Call;

/**
 * This probe monitors both the incoming and outgoing phone calls. For each call, it reports the
 * folloing information:
 *
 *  - timestamp of the call
 *  - if it is an incoming or outgoing call
 *  - the phone number of the caller/receiver
 *  - name of the caller/receiver, if present in the addressbook
 *
 *  During the first run, the probe fetches the phone calls history.
 *
 * Requires:
 *
 *  - "android.permission.READ_CALL_LOG"
 *  - "android.permission.PROCESS_OUTGOING_CALLS"
 *
 */
@SuppressWarnings("unused")
class CallsProbe extends OnEventProbe {

    private BroadcastReceiver callsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction() != null){

                String phoneNumber = null;
                boolean incoming = false;

                if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
                    phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

                }else{

                    if(intent.getExtras() != null){

                        String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                        if(state != null && state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                            phoneNumber = intent.getExtras().getString(
                                    TelephonyManager.EXTRA_INCOMING_NUMBER);
                            incoming = true;
                        }

                    }
                }

                if(phoneNumber != null) onNewCall(phoneNumber, incoming);
            }
        }
    };


    @Override
    public void init() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        getContext().registerReceiver(callsReceiver, intentFilter);

    }

    @Override
    public void onFirstRun() {
        logOnFile(false, CallsController.getAllCalls(getContext()));
    }

    @Override
    void onStop() {}

    private void onNewCall(String number, boolean incoming){

        String name = ContactsController.getNameFromNumber(getContext(), number);

        logOnFile(false, new Call(new Date().getTime(), name, number, incoming));

    }
}
