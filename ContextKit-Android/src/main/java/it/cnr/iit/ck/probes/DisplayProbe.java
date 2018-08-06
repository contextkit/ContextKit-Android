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
import android.hardware.display.DisplayManager;
import android.view.Display;

import it.cnr.iit.ck.model.DisplayInfo;

/**
 * This probe monitors the display status, i.e., if it is on and its current rotation grade.
 *
 */
@SuppressWarnings("unused")
class DisplayProbe extends OnEventProbe {

    private DisplayManager displayManager;
    private BroadcastReceiver displayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction() != null){

                switch (intent.getAction()){

                    case Intent.ACTION_SCREEN_ON:
                    case Intent.ACTION_SCREEN_OFF:
                        fetchData();
                        break;
                }
            }
        }
    };

    @Override
    public void init() {

        displayManager = (DisplayManager) getContext().getSystemService(Context.DISPLAY_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        getContext().registerReceiver(displayReceiver, intentFilter);

        fetchData();
    }

    @Override
    public void onFirstRun() {}

    @Override
    void onStop() {
        getContext().unregisterReceiver(displayReceiver);
    }

    private void fetchData(){
        logOnFile(true, new DisplayInfo(displayManager.getDisplay(
                Display.DEFAULT_DISPLAY)));
    }
}
