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

import android.content.Context;

import it.cnr.iit.ck.sensing.controllers.ContactsController;
import it.cnr.iit.ck.logs.FileLogger;

public class Sms implements Loggable{

    private static final String LOG_HEADER = "sms_time,phone_number,sent,contains_url,saved_contact";

    private String address, body;
    private long timestamp;
    private boolean containsUrl;
    private boolean fromAddressBook;
    private boolean sent;
    private long id;

    public Sms(Context context, me.everything.providers.android.telephony.Sms sms, boolean sent){

        this.id = sms.id;
        this.address = sms.address;
        this.timestamp = sms.sentDate;
        this.sent = sent;
        this.body = sms.body;
        this.containsUrl = sms.body.contains("http://") || sms.body.contains("www.");
        this.fromAddressBook = ContactsController.getNameFromNumber(context, sms.address) != null;
    }

    public long getId(){return id;}

    @Override
    public String getDataToLog() {
        return timestamp + FileLogger.SEP + address + FileLogger.SEP + sent +
                FileLogger.SEP + containsUrl + FileLogger.SEP + fromAddressBook;
    }

    @Override
    public String getLogHeader() {
        return LOG_HEADER;
    }
}
