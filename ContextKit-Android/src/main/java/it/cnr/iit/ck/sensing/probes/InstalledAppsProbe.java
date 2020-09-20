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

package it.cnr.iit.ck.sensing.probes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.List;

import it.cnr.iit.ck.commons.Utils;
import it.cnr.iit.ck.sensing.model.Featurable;
import it.cnr.iit.ck.sensing.model.PackagesData;
import it.cnr.iit.ck.sensing.model.SensorData;

/**
 * This probe monitors the installed applications. For each application, it reports the package
 * name.
 *
 */
@SuppressWarnings("unused")
class InstalledAppsProbe extends ContinuousProbe {

    private BroadcastReceiver appReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction() != null &&
                    (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||
                            intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED))){
                Log.d(Utils.TAG, "New app installation / uninstall");
                fetchPackages();
            }
        }
    };

    @Override
    public void init() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");

        getContext().registerReceiver(appReceiver, intentFilter);
    }

    @Override
    public void onFirstRun() {

    }

    @Override
    public void onStop() {

        getContext().unregisterReceiver(appReceiver);
    }

    @Override
    public void parseConfiguration(JsonObject configuration) {

    }

    @Override
    public SensorData getLastData() {
        return null;
    }

    @Override
    public String getFeaturesHeader() {
        return null;
    }

    @Override
    public void exec() {
        fetchPackages();
    }

    private void fetchPackages(){

        final PackageManager pm = getContext().getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        logOnFile(new PackagesData(packages));
    }
}
