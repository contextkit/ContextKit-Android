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

package it.cnr.iit.ckdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import it.cnr.iit.ck.CK;
import it.cnr.iit.ck.CKManager;

/**
 * @author Mattia Campana (m.campana@iit.cnr.it)
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE = 0;
    private static final String[] RUNTIME_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALENDAR
    };
    private CK ask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermissions();

        if(CKManager.RUNNING) ((Button)findViewById(R.id.button2)).setText("STOP READING");
        else ((Button)findViewById(R.id.button2)).setText("START NEW READING");
    }

    private void startASK(){
        ask = new CK(this, getResources().getString(R.string.ask_conf));
        ask.start();
    }

    private void stopASK(){

        if(ask != null) ask.stop();
    }

    public void onControlClicked(View view){

        if(!CKManager.RUNNING){
            startASK();
            ((Button)view).setText("STOP READING");

        }else{
            stopASK();
            ((Button)view).setText("START NEW READING");
        }
    }

    private void checkPermissions(){

        boolean require = false;

        for(String permission : RUNTIME_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(
                    this, permission) != PackageManager.PERMISSION_GRANTED){
                require = true;
                break;
            }
        }

        if(require)
            ActivityCompat.requestPermissions(this, RUNTIME_PERMISSIONS, REQ_CODE);
    }

    private boolean allPermissionsGranted(int[] granted){

        for(int perm : granted)
            if(perm == PackageManager.PERMISSION_DENIED) return false;

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case 0: {

                if (allPermissionsGranted(grantResults)) {
                    Log.d(MainActivity.class.getName(), "Permissions granted");

                } else {

                    Log.d(MainActivity.class.getName(), "Doh! Missing some permission.");
                }

            }
        }
    }
}
