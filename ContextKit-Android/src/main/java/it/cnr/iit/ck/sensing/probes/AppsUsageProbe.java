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
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.cnr.iit.ck.commons.Utils;
import it.cnr.iit.ck.sensing.controllers.AppsUsageController;
import it.cnr.iit.ck.sensing.model.AppUsageData;
import it.cnr.iit.ck.sensing.model.AppUsageStats;
import it.cnr.iit.ck.sensing.model.SensorData;

/**
 * This probe monitors the apps usage statistics using the {@link UsageStatsManager}. For this
 * reason, it requires that the minimum API level supported by the app is >= 22. In addition, it
 * requires a system-level permission which should be granted by the user through the Settings app,
 * typically under  "Settings" -> "Security" -> "Apps with usage access".
 *
 * Parameters:
 *
 *  - "statsInterval" : defines the beginning of the range of stats to include in the results.
 *  It should be defined in the following format: "<number> <time>". <number> should be a positive
 *  integer, while <time> should be one of the following: "day", "days", "week", "weeks", "month",
 *  "months", "year", or "years". For example, "statsInterval" : "1 day" considers the apps usage
 *  statistics since yesterday. The default value is "1 day".
 *
 * Required permissions:
 *
 * - "android.permission.PACKAGE_USAGE_STATS"
 *
 */
@SuppressWarnings("unused")
class AppsUsageProbe extends ContinuousProbe {

    private static final String DEFAULT_STATS_INTERVAL = "1 day";

    private String statsInterval = DEFAULT_STATS_INTERVAL;
    private boolean valid = false;
    private int time, type;
    private Date startDate;

    @Override
    public void init() {
        checkInterval();

        if(valid){

            Date now = Calendar.getInstance().getTime();

            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(type, -time);

            startDate = cal.getTime();
        }
    }

    @Override
    public void onFirstRun() {}


    @Override
    public void onStop() {}

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

        if(valid) getRunningApps();
    }

    private void getRunningApps(){

        List<AppUsageStats> apps = AppsUsageController.getAppUsageStats(getContext(), startDate);
        logOnFile(new AppUsageData(apps));

    }

    /**
     * Checks the "statsInterval" parameter.
     */
    private void checkInterval(){

        statsInterval = statsInterval.toLowerCase();

        if(statsInterval.split(" ").length != 2){
            invalidInterval();

        } else{

            try {
                time = Integer.parseInt(statsInterval.split(" ")[0]);
            }catch (NumberFormatException e){
                Log.e(Utils.TAG, e.getMessage());
                invalidInterval();
                return;
            }

            if(time < 1){
                invalidInterval();
                return;
            }

            String timeString = statsInterval.split(" ")[1];

            switch(timeString){

                case "minute":
                case "minutes":
                    type = Calendar.MINUTE;
                    break;

                case "hour":
                case "hours":
                    type = Calendar.HOUR;
                    break;

                case "day":
                case "days":
                    type = Calendar.DAY_OF_MONTH;
                    break;

                case "week":
                case "weeks":
                    type = Calendar.WEEK_OF_MONTH;
                    break;

                case "month":
                case "months":
                    type = Calendar.MONTH;
                    break;

                case "year":
                case "years":
                    type = Calendar.YEAR;
                    break;

                default:
                    invalidInterval();
                    break;

            }

            valid = true;
        }

    }

    private void invalidInterval(){

        Log.e(Utils.TAG, "Malformed start interval: "+statsInterval);
    }
}
