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

import android.content.Context;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ck.model.CellInfo;

public class CellController {

    @SuppressWarnings("all")
    public static List<CellInfo> getVisibleCells(Context context){

        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        List<CellInfo> cells = new ArrayList<>();

        if(tm != null && tm.getAllCellInfo() != null) {
            for (android.telephony.CellInfo cellInfo : tm.getAllCellInfo()) {

                CellInfo cell = getCellInfo(cellInfo);

                if(cell != null) cells.add(cell);
            }
        }

        return cells;
    }

    private static CellInfo getCellInfo(android.telephony.CellInfo cellInfo){

        CellInfo info = null;

        if(cellInfo instanceof CellInfoGsm){

            info = fetchGsmInfo((CellInfoGsm) cellInfo);

        } else if(cellInfo instanceof CellInfoLte) {

            info = fetchLteInfo((CellInfoLte) cellInfo);

        } else if(cellInfo instanceof CellInfoCdma){

            info = fetchCdmaInfo((CellInfoCdma) cellInfo);

        } else if(cellInfo instanceof CellInfoWcdma){

            info = fetchWcdmaInfo((CellInfoWcdma) cellInfo);
        }

        return info;
    }

    private static CellInfo fetchGsmInfo(CellInfoGsm info){

        //info.getCellSignalStrength().getDbm();
        int signalLevel = info.getCellSignalStrength().getDbm();

        //Cid = Cell identity descriptor [0, 65535]
        int baseStationId = info.getCellIdentity().getCid();
        if(baseStationId == Integer.MAX_VALUE) baseStationId = -1; //unknown base station ID

        return new CellInfo(CellInfo.TYPE_GSM, baseStationId, signalLevel);
    }

    private static CellInfo fetchLteInfo(CellInfoLte info){

        int signalLevel = info.getCellSignalStrength().getDbm();

        //Ci = Cell identity 28 bit
        int baseStationId = info.getCellIdentity().getCi();
        if(baseStationId == Integer.MAX_VALUE) baseStationId = -1; //unknown base station ID

        return new CellInfo(CellInfo.TYPE_LTE, baseStationId, signalLevel);
    }

    private static CellInfo fetchCdmaInfo(CellInfoCdma info){

        int signalLevel = info.getCellSignalStrength().getCdmaDbm();

        //Base station ID [0, 65535]
        int baseStationId = info.getCellIdentity().getBasestationId();
        if(baseStationId == Integer.MAX_VALUE) baseStationId = -1; //unknown base station ID

        return new CellInfo(CellInfo.TYPE_CDMA, baseStationId, signalLevel);
    }

    private static CellInfo fetchWcdmaInfo(CellInfoWcdma info){

        int signalLevel = info.getCellSignalStrength().getDbm();

        //Base station ID [0, 268435455]
        int baseStationId = info.getCellIdentity().getCid();
        if(baseStationId == Integer.MAX_VALUE) baseStationId = -1; //unknown base station ID

        return new CellInfo(CellInfo.TYPE_WCDMA, baseStationId, signalLevel);
    }
}
