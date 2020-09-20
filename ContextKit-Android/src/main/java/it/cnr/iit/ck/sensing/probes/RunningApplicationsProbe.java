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

import android.app.usage.UsageStatsManager;

import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import it.cnr.iit.R;
import it.cnr.iit.ck.sensing.controllers.AppsUsageController;
import it.cnr.iit.ck.sensing.controllers.GooglePlayStoreController;
import it.cnr.iit.ck.logs.FileLogger;
import it.cnr.iit.ck.sensing.model.ApplicationData;
import it.cnr.iit.ck.sensing.model.RunningApplications;
import it.cnr.iit.ck.sensing.model.SensorData;

/**
 * This probe monitors the usage statistics of the running applications using the
 * {@link UsageStatsManager}. For this reason, it requires that the minimum API level supported by
 * the app is >= 22.
 *
 * Parameters:
 *      - "lastNMinutes" : controls the size of the time used to identify the running applications.
 *      For example, with "lastNMinutes" : 5, this probe considers as "running" all the apps with
 *      last usage timestamp >= 5 minutes ago. The default value is 5 minutes.
 *
 * Requires:
 *      - "android.permission.PACKAGE_USAGE_STATS"
 */
class RunningApplicationsProbe extends ContinuousProbe
        implements GooglePlayStoreController.ApplicationsDataListener {

    private static final int DEFAULT_LAST_N_MINUTES = 5;

    // This parameter can be set through the Json configuration
    private int lastNMinutes = DEFAULT_LAST_N_MINUTES;
    private GooglePlayStoreController playStoreController;

    private RunningApplications lastData;
    private String[] categories;

    @Override
    public void init() {
        playStoreController = GooglePlayStoreController.getInstance();
        lastData = new RunningApplications(categories);
    }

    @Override
    public void onFirstRun() {}

    @Override
    public void onStop() {}

    @Override
    public void parseConfiguration(JsonObject configuration) {}

    @Override
    public SensorData getLastData() {
        return lastData;
    }

    @Override
    public String getFeaturesHeader() {
        categories = getContext().getResources().getStringArray(R.array.play_store_categories);
        Arrays.sort(categories);

        return StringUtils.join(categories, FileLogger.SEP);
    }

    @Override
    public void exec() {

        List<String> pkgs = AppsUsageController.getRecentApplications(getContext(), lastNMinutes);
        if(pkgs.size() != 0)
            playStoreController.getAppCategory(getContext(), this,
                    pkgs.toArray(new String[0]));
    }

    @Override
    public void onApplicationsAvailable(List<ApplicationData> applications) {
        lastData = new RunningApplications(applications);
        lastData.setCategories(categories);
        logOnFile(applications);
    }
}
