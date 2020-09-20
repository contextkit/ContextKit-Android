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

package it.cnr.iit.ck.logs;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import it.cnr.iit.ck.sensing.model.Loggable;
import it.cnr.iit.ck.sensing.model.SensorData;

public class FileLogger {
    public static final String SEP = ",";

    private static FileLogger instance;
    String basePath;

    public static FileLogger getInstance(){
        if(instance == null) instance = new FileLogger();

        return instance;
    }

    public void setBaseDir(Context applicationContext){
        basePath = ContextCompat.getExternalFilesDirs(applicationContext, null)[0].getPath();
        File dir = new File(basePath);
        if(!dir.exists()) dir.mkdir();
        Log.d("CK", "Base log path: " + basePath);
    }

    public void store(final String fileName, final Loggable... dataList){

        new Thread(() -> {

            File file = createLogFile(fileName, dataList[0].getLogHeader());

            Date currentTime = Calendar.getInstance().getTime();

            for(Loggable data : dataList) {
                String toWrite = currentTime + SEP + data.getDataToLog();
                writeToFile(file, toWrite);
            }

        }).start();
    }

    private File createLogFile(String fileName, String header){
        File file = new File(basePath + File.separator + fileName);
        header = "date_time,"+header;
        try {
            if(!file.exists() && file.createNewFile()) writeToFile(file, header);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private void writeToFile(File file, String stringData){

        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(stringData+"\n");
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
