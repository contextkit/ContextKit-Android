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

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import it.cnr.iit.ck.commons.Utils;
import it.cnr.iit.ck.model.Loggable;

public class FileLogger {

    private static final String DEFAULT_BASE_DIR = "AndroidSensingKit";
    public static final String SEP = "\t";

    private static FileLogger instance;
    String basePath;

    public static FileLogger getInstance(){
        if(instance == null) instance = new FileLogger();

        return instance;
    }

    public void setBaseDir(String baseDir){

        if(baseDir == null) baseDir = DEFAULT_BASE_DIR;

        basePath = Environment.getExternalStorageDirectory() + File.separator + baseDir;
        File dir = new File(basePath);
        if(!dir.exists()) dir.mkdir();
    }

    public void store(final String fileName, final Collection<? extends Loggable> data,
                      final boolean withTimeStamp){

        new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(basePath + File.separator + fileName);

                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {

                    FileWriter fw = new FileWriter(file, true);

                    Date currentTime = Calendar.getInstance().getTime();

                    for(Loggable printable : data){


                        String toWrite;

                        if(withTimeStamp){

                            toWrite = currentTime.getTime() + SEP + printable.getDataToLog();

                        } else{
                            toWrite = printable.getDataToLog();
                        }

                        fw.write(toWrite+"\n");

                    }

                    fw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void store(final String fileName, final Loggable data, final boolean withTimeStamp){

        new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(basePath + File.separator + fileName);

                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    FileWriter fw = new FileWriter(file, true);

                    String toWrite;

                    if(withTimeStamp){

                        Date currentTime = Calendar.getInstance().getTime();
                        toWrite = currentTime.getTime() + SEP + data.getDataToLog();

                    } else{
                        toWrite = data.getDataToLog();
                    }

                    fw.write(toWrite+"\n");
                    fw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
