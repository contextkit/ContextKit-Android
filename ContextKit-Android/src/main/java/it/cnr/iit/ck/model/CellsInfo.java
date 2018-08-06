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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CellsInfo implements Loggable {

    private List<CellInfo> cells = new ArrayList<>();

    public CellsInfo(List<CellInfo> cells){ this.cells = cells; }

    public void addCell(CellInfo cellInfo){ this.cells.add(cellInfo); }

    @Override
    public String getDataToLog() {
        List<String> data = new ArrayList<>();

        for(CellInfo cellInfo : cells) data.add(cellInfo.getDataToLog());

        return StringUtils.join(data, "\t");
    }
}
