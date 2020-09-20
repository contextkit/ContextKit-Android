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

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;

import com.google.gson.JsonObject;

import it.cnr.iit.ck.sensing.controllers.SmsController;
import it.cnr.iit.ck.sensing.model.SensorData;
import it.cnr.iit.ck.sensing.model.Sms;

/**
 * This probe monitors both the received and sent sms. During the first run, it read all the
 * received/sent sms in the history of the local device.
 *
 * Required permissions:
 *
 *      - "android.permission.READ_SMS"
 *      - "android.permission.READ_CONTACTS"
 *      - "android.permission.RECEIVE_SMS"
 *
 */
@SuppressWarnings("unused")
public class SmsProbe extends OnEventProbe {

    private OutGoingObserver outGoingObserver;
    private SmsReceiver receiver;
    private long lastSmsId;

    private Runnable fetchAllSms = () -> logOnFile(SmsController.getAllSms(getContext()));

    public SmsProbe(){}

    @Override
    public void init() {

        outGoingObserver = new OutGoingObserver(new Handler());

        receiver = new SmsReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);

        getContext().registerReceiver(receiver, intentFilter);

        ContentResolver contentResolver = getContext().getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms/out"),
                true, outGoingObserver);
    }

    @Override
    public void onFirstRun() {

        new Thread(fetchAllSms).start();
    }


    @Override
    public void onStop() {

        if(receiver != null) getContext().unregisterReceiver(receiver);
        receiver = null;
    }

    @Override
    public void parseConfiguration(JsonObject configuration) {

    }

    @Override
    public SensorData getLastData() {
        return null;
    }

    @Override
    public String getFeaturesHeader() {
        return null;
    }

    private void onSmSEvent(Sms sms){

        if(lastSmsId != sms.getId()){
            logOnFile(sms);
            lastSmsId = sms.getId();
        }

    }

    class OutGoingObserver extends ContentObserver {

        OutGoingObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            Sms outgoingSms = SmsController.getLastSentSms(getContext());
            if(outgoingSms != null) onSmSEvent(outgoingSms);
        }
    }

    class SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction() != null){

                switch (intent.getAction()){

                    case Telephony.Sms.Intents.SMS_RECEIVED_ACTION:

                        Sms receivedSms = SmsController.getLastReceivedSms(getContext());
                        if(receivedSms != null) onSmSEvent(receivedSms);

                        break;
                }

            }
        }
    }

}
