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

package it.cnr.iit.ck.sensing.controllers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import it.cnr.iit.ck.commons.Utils;

public class HardwareInfoController {

    @SuppressWarnings("all")
    public static String getAndroidID(Context context){
        return Settings.Secure.getString(context.getApplicationContext()
                .getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getWiFiMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            Log.e(Utils.TAG, "Error reading WiFi Mac address");
        }

        return "02:00:00:00:00:00";
    }

    @SuppressWarnings("all")
    public static String getBTMac(Context context){
        return  android.provider.Settings.Secure.getString(context.getContentResolver(),
                "bluetooth_address");
    }

    public static String getPhoneBrand(){
        return Build.BRAND;
    }

    public static String getPhoneModel(){
        return Build.MODEL;
    }

    @SuppressWarnings("all")
    public static String getDeviceID(Context context){

        TelephonyManager telephonyManager = ((TelephonyManager) context.getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE));

        return telephonyManager.getImei();
    }

    @SuppressWarnings("all")
    public static String getPhoneNumber(Context context){
        /*TelephonyManager tMgr = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        return  tMgr.getLine1Number();*/

        String main_data[] = {"data1", "is_primary", "data3", "data2", "data1", "is_primary",
                "photo_uri", "mimetype"};
        Object object = context.getContentResolver().query(Uri.withAppendedPath(
                android.provider.ContactsContract.Profile.CONTENT_URI, "data"),
                main_data, "mimetype=?",
                new String[]{"vnd.android.cursor.item/phone_v2"},
                "is_primary DESC");
        String phoneNumber = "";
        if (object != null) {
            do {
                if (!((Cursor) (object)).moveToNext())
                    break;
                phoneNumber = ((Cursor) (object)).getString(4);
            } while (true);
            ((Cursor) (object)).close();
        }

        return phoneNumber;
    }
}
