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

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ck.sensing.controllers.PreferencesController;
import it.cnr.iit.ck.logs.FileChecker;
import it.cnr.iit.ck.logs.FileLogger;
import it.cnr.iit.ck.logs.FileSender;
import it.cnr.iit.ck.sensing.probes.BaseProbe;
import it.cnr.iit.ck.sensing.probes.ContinuousProbe;
import it.cnr.iit.ck.sensing.probes.OnEventProbe;
import it.cnr.iit.ck.workers.SimpleWorker;
import it.cnr.iit.ck.workers.ThreadWorker;
import it.cnr.iit.ck.workers.Worker;

public class CKManager extends Service {

    public static boolean RUNNING = false;

    // Intent's action that contains the Json configuration string
    public static final String JSON_SETUP_KEY = "CK_JSON_SETUP";
    public static final String SETUP_KEY = "CK_SETUP";

    private List<Worker> workers = new ArrayList<>();
    private FileChecker fileChecker;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for(Worker worker : workers) worker.stop();
        if(fileChecker != null) fileChecker.stop();
        RUNNING = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!RUNNING) {
            getConfiguration(intent);
            RUNNING = true;
        }

        return Service.START_STICKY;
    }

    void startFramework(CKSetup setup){

        if(PreferencesController.isFirstRun(this))
            PreferencesController.generateUniqueDeviceID(this);

        FileLogger.getInstance().setBaseDir(getApplicationContext());

        startSensing(setup);
        startRemoteEndpoint(setup);
        startFeaturesWorker(setup);

        PreferencesController.firstRunDone(this);
    }

    void startSensing(CKSetup setup){
        for(BaseProbe probe : setup.probes.values()) {
            Worker worker = null;

            if(probe instanceof OnEventProbe)
                worker = new SimpleWorker(probe, PreferencesController.isFirstRun(this));
            else if(probe instanceof ContinuousProbe)
                worker = new ThreadWorker((ContinuousProbe) probe, true);

            if(worker != null){
                worker.start();
                workers.add(worker);
            }
        }
    }

    void startRemoteEndpoint(CKSetup setup){
        FileSender fileSender = null;
        if(setup.remoteLogger != null) fileSender = new FileSender(setup.remoteLogger);

        if(setup.zipperInterval != null)
            fileChecker = new FileChecker(getApplicationContext(), fileSender,
                    setup.zipperInterval, setup.maxLogSizeMb);
    }

    void startFeaturesWorker(CKSetup setup){
        if(setup.featuresExtractor != null) {
            Worker worker = new ThreadWorker(setup.featuresExtractor, true);
            worker.start();
            workers.add(worker);
        }
    }

    /**
     * Reads the Json configuration. During the first execution, the configuration should be in the
     * Intent's extras. If the system kills and restart the service, the configuration can be find
     * in the shared preferences.
     *
     * @param intent    The Intent object received in the
     *                  {@link Service#onStartCommand(Intent, int, int)} method.
     *
     * @return          The Json configuration.
     */
    private void getConfiguration(Intent intent){

        if(intent != null && intent.hasExtra(JSON_SETUP_KEY))
            parseJsonConfiguration(intent.getStringExtra(JSON_SETUP_KEY));
    }

    /**
     * Creates a {@link Worker} object for each Probe specified in the configuration.
     *
     * @param jsonConf      The Json configuration
     */
    private void parseJsonConfiguration(String jsonConf){
        new ParseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonConf);
    }

    private class ParseTask extends AsyncTask<String, Void, CKSetup>{

        @Override
        protected CKSetup doInBackground(String... conf) {
            return CKSetup.fromJson(getApplicationContext(), conf[0]);
        }

        @Override
        protected void onPostExecute(CKSetup setup) {
            startFramework(setup);
        }
    }
}
