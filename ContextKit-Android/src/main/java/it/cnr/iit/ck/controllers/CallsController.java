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

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ck.model.Call;
import it.cnr.iit.ck.model.Loggable;
import me.everything.providers.android.calllog.CallsProvider;

public class CallsController {

    public static List<Call> getAllCalls(Context context){

        List<Call> calls = new ArrayList<>();
        CallsProvider provider = new CallsProvider(context);

        for(me.everything.providers.android.calllog.Call call : provider.getCalls().getList()){
            calls.add(new Call(call));
        }

        return calls;
    }
}
