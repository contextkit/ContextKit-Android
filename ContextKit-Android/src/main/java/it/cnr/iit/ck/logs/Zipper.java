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
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import it.cnr.iit.ck.sensing.controllers.PreferencesController;

class Zipper {
    private static final int BUFFER = 1024;
    private String baseDir;
    private Context context;

    public Zipper(Context context){

        this.context = context;
        baseDir = FileLogger.getInstance().basePath;

    }

    public String zip(File[] files){

        String tempDir = createTempDir();

        String zipFile = null;

        if(tempDir != null){
            Log.d("ZIPPER", "TempDir: " + tempDir);
            moveFiles(files, tempDir);

            zipFile = tempDir + ".zip";
            createZipFromDirectory(zipFile, tempDir);

            try {
                FileUtils.deleteDirectory(new File(tempDir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return zipFile;
    }

    private String createTempDir(){

        String dirName = baseDir + File.separator + PreferencesController.getUniqueDeviceID(context)
                + "_" + (System.currentTimeMillis()/1000);

        File dirFile = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                Log.e("ASK_ZIPPER", "Problem creating " + dirName);
                return null;
            }
        }

        return dirName;
    }

    private void moveFiles(File[] files, String destDir){

        for(File file : files){

            File newFile = new File(destDir + "/" + file.getName());
            try {
                FileUtils.moveFile(file, newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createZipFromDirectory(String zipFile, String srcDir) {

        try {

            FileOutputStream fos = new FileOutputStream(zipFile);

            ZipOutputStream zos = new ZipOutputStream(fos);

            File srcFile = new File(srcDir);

            addDirToArchive(zos, srcFile);

            // close the ZipOutputStream
            zos.close();

        }
        catch (IOException ioe) {
            System.out.println("Error creating zip file: " + ioe);
        }

    }

    private static void addDirToArchive(ZipOutputStream zos, File srcFile) {

        File[] files = srcFile.listFiles();

        System.out.println("Adding directory: " + srcFile.getName());

        for (int i = 0; i < files.length; i++) {

            // if the file is directory, use recursion
            if (files[i].isDirectory()) {
                addDirToArchive(zos, files[i]);
                continue;
            }

            try {

                System.out.println("tAdding file: " + files[i].getName());

                // create byte buffer
                byte[] buffer = new byte[1024];

                FileInputStream fis = new FileInputStream(files[i]);

                zos.putNextEntry(new ZipEntry(files[i].getName()));

                int length;

                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();

                // close the InputStream
                fis.close();

            } catch (IOException ioe) {
                System.out.println("IOException :" + ioe);
            }

        }

    }

    /**
     * Zip it
     * @param zipFile output ZIP file location
     */
    private void createZip(String zipFile, String... inputFiles){

        byte[] buffer = new byte[1024];

        try{

            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);

            for(String file : inputFiles){

                System.out.println("File Added : " + file);
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in = new FileInputStream(baseDir + File.separator + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            //remember close it
            zos.close();

            System.out.println("Done");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private static String generateZipEntry(String file){
        return file.substring(file.lastIndexOf(File.separator) + 1, file.length());
    }
}
