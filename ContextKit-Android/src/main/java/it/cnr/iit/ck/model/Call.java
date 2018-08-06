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

package it.cnr.iit.ck.model;

import it.cnr.iit.ck.logs.FileLogger;

public class Call implements Loggable{

    private long callDate;
    private String name, number;
    private boolean incoming;

    public Call(me.everything.providers.android.calllog.Call call){

        this.callDate = call.callDate;
        this.name = call.name == null ? "null" : call.name;
        this.number = call.number;
        this.incoming = call.type == me.everything.providers.android.calllog.Call.CallType.INCOMING;
    }

    public Call(long callDate, String name, String number, boolean incoming){

        this.callDate = callDate;
        this.name = name == null ? "null" : name;
        this.number = number;
        this.incoming = incoming;
    }

    @Override
    public String getDataToLog(){

        return callDate + FileLogger.SEP + incoming + FileLogger.SEP +
                number + FileLogger.SEP + name;
    }
}
