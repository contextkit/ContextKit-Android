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

package it.cnr.iit.ck.model;

public class CellInfo implements Loggable{

    public static final int TYPE_GSM = 0;
    public static final int TYPE_LTE = 1;
    public static final int TYPE_CDMA = 2;
    public static final int TYPE_WCDMA = 3;

    private int cellType, id, signalLevel;

    public CellInfo(int cellType, int id, int signalLevel){
        this.cellType = cellType;
        this.id = id;
        this.signalLevel = signalLevel;
    }

    @Override
    public String getDataToLog() {
        return cellType + "," + id + "," + signalLevel;
    }
}
