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

import java.util.List;

import it.cnr.iit.ck.controllers.CalendarController;
import it.cnr.iit.ck.model.CalendarEvent;
import it.cnr.iit.ck.model.LoggableElements;

/**
 * @author Mattia Campana (m.campana@iit.cnr.it)
 *
 * This probe monitors the current events saved in the user's calendars.
 *
 * Requires:
 *
 *  - "android.permission.READ_CALENDAR"
 *
 */
@SuppressWarnings("unused")
class CurrentCalendarEventsProbe extends ContinuousProbe {

    @Override
    public void init() {}

    @Override
    public void onFirstRun() {}

    @Override
    void onStop() {}

    @Override
    public void exec() {

        List<CalendarEvent> events = CalendarController.getCurrentEvents(getContext());

        logOnFile(true, new LoggableElements(events));
    }
}
