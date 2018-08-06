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
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ck.commons.Utils;
import it.cnr.iit.ck.probes.BaseProbe;
import it.cnr.iit.ck.probes.ContinuousProbe;

/**
 * This class parses and represents the list of probes requested by the user through the
 * configuration. The configuration should be specified using the Json format.
 *
 */
class CKSetup {

    // Expected fields in the Json configuration
    private static final String JSON_PROBES = "probes";
    private static final String JSON_PROBE_NAME = "name";
    private static final String JSON_PROBE_INTERVAL = "interval";
    private static final String JSON_PROBE_START_DELAY = "startDelay";
    private static final String JSON_PROBE_LOG_FILE = "logFile";

    private static final String JSON_LOGGER_PATH = "logPath";
    private static final String JSON_REMOTE_LOGGER = "remoteLoggerDest";
    private static final String JSON_ZIPPER_INTERVAL = "zipperInterval";
    private static final String JSON_MAX_LOG_SIZE = "maxLogSizeMb";

    // Name of the package that contains probes
    private static final String PROBES_PKG = "it.matbell.ask.probes";

    public List<BaseProbe> probes = new ArrayList<>();
    String loggerPath;
    String remoteLogger;
    Integer maxLogSizeMb;
    Integer zipperInterval;

    private CKSetup(){}

    /**
     * Parses the Json configuration string.
     *
     * @param context       The application's context
     * @param jsonConf      The string that represents the Json configuration
     *
     * @return              The SKSetup object containing the Probe objects specified in the Json
     *                      configuration
     */
    static CKSetup parse(Context context, String jsonConf){

        CKSetup skSetup = new CKSetup();

        try {

            JSONObject conf = new JSONObject(jsonConf);

            JSONArray jsonProbes = conf.getJSONArray(JSON_PROBES);

            for(int i = 0; i < jsonProbes.length(); i++){

                BaseProbe probe = getProbeFromClass(jsonProbes.getJSONObject(i).toString());

                if(probe != null){
                    probe.setContext(context);
                    skSetup.probes.add(probe);
                }
            }

            if(conf.has(JSON_LOGGER_PATH))
                skSetup.loggerPath = conf.getString(JSON_LOGGER_PATH).replace(
                        "\"","");

            if(conf.has(JSON_REMOTE_LOGGER))
                skSetup.remoteLogger = conf.getString(JSON_REMOTE_LOGGER).replace(
                        "\"","");

            if(conf.has(JSON_ZIPPER_INTERVAL))
                skSetup.zipperInterval = conf.getInt(JSON_ZIPPER_INTERVAL);

            if(conf.has(JSON_MAX_LOG_SIZE))
                skSetup.maxLogSizeMb = conf.getInt(JSON_MAX_LOG_SIZE);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return skSetup;
    }

    /**
     * Creates the Probe object based on the class name specified in the Json configuration.
     *
     * @param jsonObject        The String that represents the probe
     *
     * @return                  The Probe object
     */
    private static BaseProbe getProbeFromClass(String jsonObject){

        GsonBuilder builder = new GsonBuilder();
        // Needed for the the Gson library bug -----------------------------------------------------
        // https://stackoverflow.com/questions/32431279/android-m-retrofit-json-cant-make-field-
        // constructor-accessible
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        builder.serializeNulls();
        //------------------------------------------------------------------------------------------

        Gson gson = builder.create();

        JsonObject jsonProbe = new JsonParser().parse(jsonObject).getAsJsonObject();
        String className = jsonProbe.get(JSON_PROBE_NAME).getAsString();

        BaseProbe probe = null;

        try{

            Class<?> clazz = Class.forName(PROBES_PKG+"."+className);
            probe = (BaseProbe) gson.fromJson(jsonProbe, clazz);

            probe = parseRequiredFields(probe, jsonProbe.getAsJsonObject(), className);
            probe = parseOptionalFields(probe, jsonProbe.getAsJsonObject());

        }catch (ClassNotFoundException e){
            Log.e(Utils.TAG, "Probe "+className+" not found.");

        } catch (Exception e){
            Log.e(Utils.TAG, "Error creating probe: "+e.getMessage());
        }

        return probe;
    }

    /**
     * Parses the probe's required parameters specified in the Json configuration, such as the
     * interval parameter required by the continuous probes.
     *
     * @param probe         The probe object
     * @param jsonObject    The JsonObject parsed from the configuration
     * @param className     Name of the probe specified in the Json configuration
     *
     * @return              the probe object with the optional parameters (if present)
     */
    private static BaseProbe parseRequiredFields(BaseProbe probe,
                                                 JsonObject jsonObject, String className){

        if(probe instanceof ContinuousProbe){

            if(!jsonObject.has(JSON_PROBE_INTERVAL)){
                Log.e(Utils.TAG, "Missing field "+JSON_PROBE_INTERVAL+" for "+className);
                probe = null;

            }else{
                ((ContinuousProbe)probe).setInterval(jsonObject.get(
                        JSON_PROBE_INTERVAL).getAsInt());
            }
        }

        return probe;
    }

    /**
     * Parses the probe's optional parameters specified in the Json configuration, such as the
     * logFile and startsDelay parameters.
     *
     * @param probe         The probe object
     * @param jsonObject    The JsonObject parsed from the configuration
     *
     * @return              the probe object with the optional parameters (if present)
     */
    private static BaseProbe parseOptionalFields(BaseProbe probe, JsonObject jsonObject){

        if(jsonObject.has(JSON_PROBE_LOG_FILE))
            probe.setLogFile(jsonObject.get(JSON_PROBE_LOG_FILE).getAsString());

        if(jsonObject.has(JSON_PROBE_START_DELAY))
            probe.setStartDelay(jsonObject.get(JSON_PROBE_START_DELAY).getAsInt());

        return probe;
    }
}
