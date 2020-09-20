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

package it.cnr.iit.ck.workers;

import android.util.Log;

import it.cnr.iit.ck.commons.Utils;
import it.cnr.iit.ck.sensing.probes.ContinuousProbe;

public class ThreadWorker extends Worker {

    private RunThread thread;
    private boolean active;

    public ThreadWorker(ContinuousProbe probe, boolean firstRun){
        super(probe, firstRun);
    }

    @Override
    public void start() {

        if(!active){

            active = true;
            thread = new RunThread();
            thread.start();
        }
    }

    @Override
    public void stop() {

        active = false;
        thread = null;

    }

    class RunThread extends Thread{

        @Override
        public void run() {
            super.run();

            try {

                sleep(getProbe().getStartDelay() * 1000);

                if(isFirstRun()) getProbe().onFirstRun();

                while (active) {

                    ContinuousProbe continuousProbe = (ContinuousProbe) getProbe();
                    continuousProbe.exec();
                    sleep(continuousProbe.getInterval() * 1000);
                }

                getProbe().stop();

            }catch (InterruptedException e){
                Log.e(Utils.TAG, e.getMessage());
            }
        }

    }
}
