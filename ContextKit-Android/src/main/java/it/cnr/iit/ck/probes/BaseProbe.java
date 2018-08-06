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

import android.content.Context;

import java.util.Collection;

import it.cnr.iit.ck.logs.FileLogger;
import it.cnr.iit.ck.model.Loggable;

/**
 * This is the probes' base class.
 *
 * Parameters:
 *  - "logFile" : if present, defines the name of the log file.
 *  - "startDelay" : if present, delays the start of the probe
 *
 */
public abstract class BaseProbe {

    String logFile;
    private Context context;
    private int startDelay;

    public void setLogFile(String logFile){ this.logFile = logFile; }

    public void setContext(Context context){this.context = context;}
    Context getContext(){
        return this.context;
    }

    public void setStartDelay(int startDelay){this.startDelay = startDelay;}
    public int getStartDelay(){return startDelay;}

    public void stop(){ this.onStop(); }

    public abstract void init();
    public abstract void onFirstRun();
    abstract void onStop();

    void logOnFile(boolean withTimeStamp, Collection<? extends Loggable> data){

        if(logFile != null) FileLogger.getInstance().store(logFile, data, withTimeStamp);
    }

    void logOnFile(boolean withTimeStamp, Loggable data){
        if(logFile != null) FileLogger.getInstance().store(logFile, data, withTimeStamp);
    }
}
