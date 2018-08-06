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

import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.util.Log;

import it.cnr.iit.ck.model.MultimediaData;

public class MultimediaProbe extends OnEventProbe {

    private FileObserver observer;

    @Override
    public void init() {
        String url = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/";

        observer = new FileObserver(url) {
            @Override
            public void onEvent(int event, @Nullable String path) {

                if(event == FileObserver.CREATE && path != null){

                    logOnFile(true, new MultimediaData(path));
                }

            }
        };

        observer.startWatching();
    }

    @Override
    public void onFirstRun() {

    }

    @Override
    void onStop() {

        if(observer != null) observer.startWatching();
    }
}
