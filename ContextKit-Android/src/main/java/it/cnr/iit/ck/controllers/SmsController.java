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

package it.cnr.iit.ck.controllers;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ck.commons.Utils;
import it.cnr.iit.ck.model.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;

public class SmsController {

    public static List<Sms> getAllSms(Context context){

        List<Sms> smsList = new ArrayList<>();

        TelephonyProvider telephonyProvider = new TelephonyProvider(context);

        for (me.everything.providers.android.telephony.Sms sms : telephonyProvider.getSms(
                TelephonyProvider.Filter.INBOX).getList()) {
            smsList.add(new Sms(context, sms, false));
        }

        for (me.everything.providers.android.telephony.Sms sms : telephonyProvider.getSms(
                TelephonyProvider.Filter.SENT).getList()) {
            smsList.add(new Sms(context, sms, true));
        }

        return smsList;
    }

    public static Sms getOutGoingSms(Context context){
        TelephonyProvider telephonyProvider = new TelephonyProvider(context);

        Sms sms = null;

        try {

            List<me.everything.providers.android.telephony.Sms> list = telephonyProvider.getSms(
                    TelephonyProvider.Filter.OUTBOX).getList();

            if(list != null && list.size() > 0) {
                sms = new Sms(context, list.get(0), true);
            }

        }catch (NullPointerException e){
            Log.w(Utils.TAG, "SMS outbox is empty");
        }

        return sms;
    }

    public static Sms getLastSentSms(Context context){
        TelephonyProvider telephonyProvider = new TelephonyProvider(context);

        Sms sms = null;

        try {

            List<me.everything.providers.android.telephony.Sms> list = telephonyProvider.getSms(
                    TelephonyProvider.Filter.SENT).getList();

            if(list != null && list.size() > 0) {
                sms = new Sms(context, list.get(0), true);
            }

        }catch (NullPointerException e){
            Log.w(Utils.TAG, "SMS sent is empty");
        }

        return sms;
    }

    public static Sms getLastReceivedSms(Context context){
        TelephonyProvider telephonyProvider = new TelephonyProvider(context);

        Sms sms = null;

        try {

            List<me.everything.providers.android.telephony.Sms> list = telephonyProvider.getSms(
                    TelephonyProvider.Filter.INBOX).getList();

            if(list != null && list.size() > 0){
                sms = new Sms(context, list.get(0), false);
            }


        }catch (NullPointerException e){
            Log.w(Utils.TAG, "SMS inbox is empty");
        }

        return sms;

    }
}
