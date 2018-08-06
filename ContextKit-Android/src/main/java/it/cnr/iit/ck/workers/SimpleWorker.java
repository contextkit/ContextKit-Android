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

import android.os.Handler;

import it.cnr.iit.ck.probes.BaseProbe;

public class SimpleWorker extends Worker {

    public SimpleWorker(BaseProbe probe, boolean isFirstRun){
        super(probe, isFirstRun);
    }

    @Override
    public void start() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(isFirstRun()) getProbe().onFirstRun();

            }
        }, getProbe().getStartDelay()*1000);

    }

    @Override
    public void stop() {

        getProbe().stop();
    }
}
