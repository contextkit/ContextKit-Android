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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ck.controllers.PreferencesController;
import it.cnr.iit.ck.logs.FileChecker;
import it.cnr.iit.ck.logs.FileLogger;
import it.cnr.iit.ck.logs.FileSender;
import it.cnr.iit.ck.probes.BaseProbe;
import it.cnr.iit.ck.probes.ContinuousProbe;
import it.cnr.iit.ck.probes.OnEventProbe;
import it.cnr.iit.ck.workers.SimpleWorker;
import it.cnr.iit.ck.workers.ThreadWorker;
import it.cnr.iit.ck.workers.Worker;

public class CKManager extends Service {

    public static boolean RUNNING = false;

    // Intent's action that contains the Json configuration string
    public static final String SETUP_KEY = "CKSetup";

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

            String configuration = getConfiguration(intent);

            parseConfiguration(configuration);

            RUNNING = true;

        }

        return Service.START_STICKY;
    }

    /**
     * Creates a {@link Worker} object for each Probe specified in the configuration.
     *
     * @param jsonConf      The Json configuration
     */
    private void parseConfiguration(String jsonConf){
        new ParseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonConf);
    }

    void startProbes(CKSetup setup){

        Log.d("CK", "Checking first run: " + PreferencesController.isFirstRun(this));
        if(PreferencesController.isFirstRun(this)){
            PreferencesController.generateUniqueDeviceID(this);
        }

        FileSender fileSender = null;
        if(setup.remoteLogger != null) fileSender = new FileSender(setup.remoteLogger);

        if(setup.zipperInterval != null)
            fileChecker = new FileChecker(getApplicationContext(), fileSender,
                    setup.zipperInterval, setup.maxLogSizeMb);

        FileLogger.getInstance().setBaseDir(setup.loggerPath);

        for(BaseProbe probe : setup.probes) {

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

        PreferencesController.firstRunDone(this);
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
    private String getConfiguration(Intent intent){

        String configuration;

        if(intent != null && intent.hasExtra(SETUP_KEY)){
            configuration = intent.getStringExtra(SETUP_KEY);

        }else{

            configuration = PreferencesController.getSavedConfiguration(this);

        }

        if(configuration != null)
            PreferencesController.saveConfiguration(this, configuration);

        return configuration;
    }

    private class ParseTask extends AsyncTask<String, Void, CKSetup>{

        @Override
        protected CKSetup doInBackground(String... conf) {
            return CKSetup.parse(getApplicationContext(), conf[0]);
        }

        @Override
        protected void onPostExecute(CKSetup setup) {
            startProbes(setup);
        }
    }
}
