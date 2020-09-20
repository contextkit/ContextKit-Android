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

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.cnr.iit.ck.commons.Utils;
import it.cnr.iit.ck.features.FeaturesExtractor;
import it.cnr.iit.ck.sensing.probes.BaseProbe;
import it.cnr.iit.ck.sensing.probes.ContinuousProbe;

/**
 * This class parses and represents the list of probes requested by the user through the
 * configuration. The configuration should be specified using the Json format.
 *
 */
public class CKSetup implements Serializable {

    // Sensing section
    private static final String SECTION_SENSING = "sensors";
    private static final String FIELD_PROBE = "probe";
    private static final String FIELD_INTERVAL = "interval";
    private static final String FIELD_START_DELAY = "startDelay";
    private static final String FIELD_LOG_FILE = "logFile";

    // Remote Endpoint section
    private static final String SECTION_REMOTE_ENDPOINT = "remoteEndpoint";
    private static final String FIELD_REMOTE_URL = "url";
    private static final String FIELD_MAX_LOG_SIZE = "maxLogSizeMb";

    // Features section
    private static final String SECTION_FEATURES = "features";
    private static final String FIELD_SOURCES = "sources";

    // Name of the package that contains probes
    private static final String PROBES_PKG = "it.cnr.iit.ck.sensing.probes";

    public HashMap<String, BaseProbe> probes = new HashMap<>();
    String remoteLogger;
    Integer maxLogSizeMb;
    Integer zipperInterval;

    FeaturesExtractor featuresExtractor;

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
    static CKSetup fromJson(Context context, String jsonConf){

        CKSetup ckSetup = new CKSetup();

        try {
            JSONObject conf = new JSONObject(jsonConf);

            if(conf.has(SECTION_SENSING))
                getSensorsSettings(ckSetup, context, conf.getJSONArray(SECTION_SENSING));

            if(conf.has(SECTION_REMOTE_ENDPOINT))
                getRemoteEndpointSettings(ckSetup, conf.getJSONObject(SECTION_REMOTE_ENDPOINT));

            if(conf.has(SECTION_FEATURES))
                getFeaturesSettings(ckSetup, conf.getJSONObject(SECTION_FEATURES));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ckSetup;
    }

    /**
     * Create sensing Probes from the Json configuration.
     *
     * @param setup         Instance of CKSetup to update
     * @param context       Application context
     * @param jsonProbes    JSONArray representing the list of Probes to activate
     */
    private static void getSensorsSettings(CKSetup setup, Context context, JSONArray jsonProbes){

        try{
            for(int i = 0; i < jsonProbes.length(); i++){
                String probeConf = jsonProbes.getJSONObject(i).toString();
                String probeName = jsonProbes.getJSONObject(i).getString(FIELD_PROBE);
                BaseProbe probe = getProbeFromClass(probeConf, context);
                if(probe != null) setup.probes.put(probeName, probe);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remote Endpoint configurations.
     *
     * @param setup         Instance of CKSetup to update
     * @param conf          JSONObject that represents the configuration
     */
    private static void getRemoteEndpointSettings(CKSetup setup, JSONObject conf){
        try {

            if(conf.has(FIELD_REMOTE_URL))
                setup.remoteLogger = conf.getString(FIELD_REMOTE_URL).replace(
                        "\"","");

            if(conf.has(FIELD_INTERVAL))
                setup.zipperInterval = conf.getInt(FIELD_INTERVAL);

            if(conf.has(FIELD_MAX_LOG_SIZE))
                setup.maxLogSizeMb = conf.getInt(FIELD_MAX_LOG_SIZE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Features configurations.
     *
     * @param setup         Instance of CKSetup to update
     * @param conf          JSONObject that represents the configuration
     */
    private static void getFeaturesSettings(CKSetup setup, JSONObject conf){

        try {
            List<BaseProbe> sources = new ArrayList<>();

            for(int i=0; i<conf.getJSONArray(FIELD_SOURCES).length(); i++) {
                String sourceName = conf.getJSONArray(FIELD_SOURCES).getString(i);
                sources.add(setup.probes.get(sourceName));
            }

            FeaturesExtractor featuresExtractor = new FeaturesExtractor(sources);
            featuresExtractor.setInterval(conf.getInt(FIELD_INTERVAL));

            if(conf.has(FIELD_START_DELAY))
                featuresExtractor.setStartDelay(conf.getInt(FIELD_START_DELAY));

            if(conf.has(FIELD_LOG_FILE))
                featuresExtractor.setLogFile(conf.getString(FIELD_LOG_FILE));

            setup.featuresExtractor = featuresExtractor;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the Probe object based on the class name specified in the Json configuration.
     *
     * @param jsonObject        The String that represents the probe
     *
     * @return                  The Probe object
     */
    private static BaseProbe getProbeFromClass(String jsonObject, Context context){

        GsonBuilder builder = new GsonBuilder();
        // Needed for the the Gson library bug -----------------------------------------------------
        // https://stackoverflow.com/questions/32431279/android-m-retrofit-json-cant-make-field-
        // constructor-accessible
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        builder.serializeNulls();
        //------------------------------------------------------------------------------------------

        Gson gson = builder.create();

        JsonObject jsonProbe = new JsonParser().parse(jsonObject).getAsJsonObject();
        String className = jsonProbe.get(FIELD_PROBE).getAsString();

        BaseProbe probe = null;

        try{

            Class<?> clazz = Class.forName(PROBES_PKG+"."+className);
            probe = (BaseProbe) gson.fromJson(jsonProbe, clazz);
            probe.setContext(context);
            parseRequiredFields(probe, jsonProbe.getAsJsonObject(), className);
            parseOptionalFields(probe, jsonProbe.getAsJsonObject());
            probe.parseConfiguration(jsonProbe.getAsJsonObject());

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
    private static void parseRequiredFields(BaseProbe probe, JsonObject jsonObject, String className){

        if(probe instanceof ContinuousProbe){

            if(!jsonObject.has(FIELD_INTERVAL)){
                Log.e(Utils.TAG, "Missing field "+ FIELD_INTERVAL +" for "+className);
                probe = null;

            }else{
                ((ContinuousProbe)probe).setInterval(jsonObject.get(
                        FIELD_INTERVAL).getAsInt());
            }
        }
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
    private static void parseOptionalFields(BaseProbe probe, JsonObject jsonObject){

        if(jsonObject.has(FIELD_LOG_FILE))
            probe.setLogFile(jsonObject.get(FIELD_LOG_FILE).getAsString());

        if(jsonObject.has(FIELD_START_DELAY))
            probe.setStartDelay(jsonObject.get(FIELD_START_DELAY).getAsInt());
    }
}
