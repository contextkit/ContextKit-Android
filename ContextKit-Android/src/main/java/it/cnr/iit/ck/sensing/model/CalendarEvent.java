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

package it.cnr.iit.ck.sensing.model;

import it.cnr.iit.ck.logs.FileLogger;
import me.everything.providers.android.calendar.Event;

public class CalendarEvent implements Loggable{

    private String calendarName;
    private long start, end;
    private String title, location;
    public boolean allDay;

    public CalendarEvent(String calendarName, long start, long end, String title, String location,
                         boolean allDay) {

        this.calendarName = calendarName;
        this.start = start;
        this.end = end;
        this.title = title;
        this.location = location;
        this.allDay = allDay;
    }

    public CalendarEvent(Event event){

        this.start = event.dTStart;
        this.end = event.dTend;
        this.title = event.title;
        this.location = event.eventLocation;
        this.allDay = event.allDay;

    }

    @Override
    public String getDataToLog() {
        return calendarName + FileLogger.SEP + allDay + FileLogger.SEP + start + FileLogger.SEP +
                end + FileLogger.SEP + title + FileLogger.SEP + "[" + location + "]";
    }

    @Override
    public String getLogHeader() {
        return null;
    }
}