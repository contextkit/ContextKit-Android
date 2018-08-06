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

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.cnr.iit.ck.model.AppUsageStats;

public class AppsUsageController {

    @SuppressWarnings("all")
    public static List<AppUsageStats> getAppUsageStats(Context context, Date startDate){

        Date now = Calendar.getInstance().getTime();

        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(
                Context.USAGE_STATS_SERVICE);

        final List<UsageStats> queryUsageStats= usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, startDate.getTime(), now.getTime());

        List<AppUsageStats> apps = new ArrayList<>();

        for(UsageStats stats : queryUsageStats){
                apps.add(new AppUsageStats(stats));
        }

        return apps;
    }

    @SuppressWarnings("all")
    public static List<String> getRecentApplications(Context context, int minutes){

        Date now = Calendar.getInstance().getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, -minutes);

        Date start = cal.getTime();

        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(
                Context.USAGE_STATS_SERVICE);

        final List<UsageStats> queryUsageStats= usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, start.getTime(), now.getTime());

        List<String> apps = new ArrayList<>();

        for(UsageStats stats : queryUsageStats){

            if(stats.getTotalTimeInForeground() > 0 &&
                    (start.getTime() <= stats.getLastTimeUsed() &&
                            now.getTime() >= stats.getLastTimeUsed())){
                apps.add(stats.getPackageName());
            }

        }

        return apps;
    }
}
