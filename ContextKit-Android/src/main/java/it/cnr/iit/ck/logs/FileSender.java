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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import it.cnr.iit.ck.commons.Utils;

/**
 * Sends log files to the backend specified in the Json configuration.
 *
 * Permissions required:
 *      - android.permission.ACCESS_NETWORK_STATE
 */
public class FileSender {

    private String backendUrl;

    public FileSender(String backendUrl){
        this.backendUrl = backendUrl;
    }

    @SuppressWarnings("all")
    void send(Context context, File[] files){

        ConnectivityManager connManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(files != null && mWifi.isConnected()) {

            for(File file : files) {

                Log.d(Utils.TAG, FileSender.class.getName() + " : Sending " +
                        file.getAbsolutePath() + " to the backend");

                new AsyncSender(backendUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file);
            }
        }

    }


    private static class AsyncSender extends AsyncTask<File, Void, Void> {

        private String backendUrl;

        AsyncSender(String backendUrl){ this.backendUrl = backendUrl; }

        @Override
        protected Void doInBackground(File... files) {

            File file = files[0];

            try {

                HttpURLConnection conn;
                DataOutputStream dos;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1024 * 1024;

                if (file.isFile()) {

                    try {

                        String fileName = file.getAbsolutePath().substring(
                                file.getAbsolutePath().lastIndexOf(File.separator) + 1,
                                file.getAbsolutePath().length());

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(file);
                        URL url = new URL(backendUrl);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;" +
                                "boundary=" + boundary);
                        conn.setRequestProperty("log", fileName);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"log\"" +
                                ";filename=\""
                                + fileName + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);

                        }

                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        if (conn.getResponseCode() == 200) {
                            file.delete();
                        }else{
                            Log.e(Utils.TAG, FileSender.class.getName() + " : Server" +
                                    "response ("+conn.getResponseCode()+") : "
                                    + conn.getResponseMessage());
                        }

                        // close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(Utils.TAG, FileSender.class.getName() + " : " + e.getMessage());

                    }
                }


            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e(Utils.TAG, FileSender.class.getName() + " : " + ex.getMessage());
            }
            return null;
        }
    }
}
