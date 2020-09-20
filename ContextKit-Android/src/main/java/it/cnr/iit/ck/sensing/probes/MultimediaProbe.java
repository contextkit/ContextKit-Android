/*
 *   Copyright (c) 2017. Mattia Campana, mattia.campana@iit.cnr.it,
 *   Franca Delmastro, franca.delmastro@gmail.com
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

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import com.google.gson.JsonObject;

import java.util.List;

import it.cnr.iit.ck.sensing.model.Featurable;
import it.cnr.iit.ck.sensing.model.MultimediaData;
import it.cnr.iit.ck.sensing.model.SensorData;

public class MultimediaProbe extends OnEventProbe {

    private ContentObserver contentObserver;

    private MultimediaData lastData;

    @Override
    public void init() {
        contentObserver = initMediaStoreObserver();
        registerMediaStoreObserver();
    }

    private ContentObserver initMediaStoreObserver() {
        return new ContentObserver(new Handler()) {

            @Override
            public void onChange(boolean selfChange, Uri uri) {

                Cursor cursor = null;

                try {
                    cursor = queryMediaStore(uri);
                } catch (IllegalStateException e){
                    e.printStackTrace();
                }

                if (cursor != null){
                    if(cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        String fileName = cursor.getString(0);
                        lastData = new MultimediaData(fileName, isImage(uri));

                        logOnFile(lastData);
                    }
                    cursor.close();
                }
                super.onChange(selfChange, uri);
            }

            private Cursor queryMediaStore(Uri uri) {
                String[] projection = { "" };
                String selectionClause = "";
                String[] selectionArgs = {"Camera", "" + (System.currentTimeMillis() - 20000)};

                String sortOrder = "";
                if (isImage(uri)) {
                    projection[0] = MediaStore.Images.ImageColumns.TITLE;
                    selectionClause = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " = ? AND "+MediaStore.Images.ImageColumns.DATE_TAKEN+" > ?";
                    sortOrder = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";
                } else if (isVideo(uri)) {
                    projection[0] = MediaStore.Video.VideoColumns.TITLE;
                    selectionClause = MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME + " = ? AND "+MediaStore.Video.VideoColumns.DATE_TAKEN+" > ?";
                    sortOrder = MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC";
                }
                return getContext().getContentResolver().query(
                        uri,
                        projection,
                        selectionClause,
                        selectionArgs,
                        sortOrder
                );
            }

            private boolean isVideo(Uri uri) {
                return uri.equals(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            }

            private boolean isImage(Uri uri) {
                return uri.equals(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }
        };
    }

    private void registerMediaStoreObserver() {
        ContentResolver contentResolver = getContext().getContentResolver();
        contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                false,
                contentObserver);
        contentResolver.registerContentObserver(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                false,
                contentObserver
        );
    }

    @Override
    public void onFirstRun() {}

    @Override
    public void onStop() {
        ContentResolver contentResolver = getContext().getContentResolver();
        contentResolver.unregisterContentObserver(contentObserver);
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
}