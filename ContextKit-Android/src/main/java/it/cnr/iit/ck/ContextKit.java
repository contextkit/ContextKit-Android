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

package it.cnr.iit.ck;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

@SuppressWarnings("unused")
public class ContextKit {

    private String jsonConfiguration;
    private CKConfiguration configuration;
    private Context context;

    /**
     * CK Contructor.
     *
     * @param context               The application's context.
     * @param jsonConfiguration     The CK configuration in Json format.
     */
    public ContextKit(Context context, String jsonConfiguration){
        this.context = context;
        this.jsonConfiguration = jsonConfiguration;
    }

    public ContextKit(Context context, CKConfiguration configuration){
        this.context = context;
        this.configuration = configuration;
    }

    /**
     * Starts the background service and probes.
     */
    public void start() throws Exception {

        if(!CKManager.RUNNING) {
            Log.d("CK", "Starting CK");
            Intent intent = new Intent(context, CKManager.class);

            if(jsonConfiguration != null)
                intent.putExtra(CKManager.JSON_SETUP_KEY, jsonConfiguration);
            else if(configuration != null)
                intent.putExtra(CKManager.SETUP_KEY, configuration);
            else
                throw new Exception("Missing configuration");

            context.startService(intent);
        }
    }

    public void stop(){

        if(CKManager.RUNNING){
            context.stopService(new Intent(context, CKManager.class));
        }
    }
}
