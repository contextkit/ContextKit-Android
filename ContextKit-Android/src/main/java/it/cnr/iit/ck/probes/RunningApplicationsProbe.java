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

import android.app.usage.UsageStatsManager;

import it.cnr.iit.ck.controllers.AppsUsageController;
import it.cnr.iit.ck.model.PackagesData;

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
@SuppressWarnings("all")
class RunningApplicationsProbe extends ContinuousProbe {

    private static final int DEFAULT_LAST_N_MINUTES = 5;

    private int lastNMinutes = DEFAULT_LAST_N_MINUTES;


    @Override
    public void init() {

    }

    @Override
    public void onFirstRun() {

    }

    @Override
    void onStop() {

    }

    @Override
    public void exec() {

        PackagesData packagesData = new PackagesData();
        packagesData.packages = AppsUsageController.getRecentApplications(getContext(), lastNMinutes);

        logOnFile(true, packagesData);
    }
}
