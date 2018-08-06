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
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContactsController {

    // ContactsContract.Contacts.CONTENT_URI COLUMNS
    private static final String COLUMN_CONTACT_ID = "_id";
    private static final String COLUMN_DISPLAY_NAME = "display_name";
    private static final String COLUMN_LOOKUP_KEY = "lookup";
    private static final String COLUMN_STARRED = "starred";
    private static final String COLUMN_TIMES_CONTACTED = "times_contacted";
    private static final String COLUMN_CUSTOM_RINGTONE = "custom_ringtone";
    private static final String COLUMN_LAST_TIME_CONTACTED = "last_time_contacted";

    private Context context;

    public ContactsController(Context context){ this.context = context; }

    public static String getNameFromNumber(Context context, String number) {

        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));

        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };

        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection,
                null, null, null);

        try {
            if (cur != null && cur.moveToFirst()) {
                return cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return null;
    }

    public List<Contact> retriveAllContacts(){

        List<Contact> contacts = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,null, null,
                null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                Contact contact = new Contact();
                contact.id = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_ID));
                contact.lookupKey = cursor.getString(cursor.getColumnIndex(COLUMN_LOOKUP_KEY));
                contact.name = cursor.getString(cursor.getColumnIndex(COLUMN_DISPLAY_NAME));
                contact.starred = cursor.getInt(cursor.getColumnIndex(COLUMN_STARRED)) == 1;
                contact.timesContacted = cursor.getInt(cursor.getColumnIndex(
                        COLUMN_TIMES_CONTACTED));
                contact.customRingtone = !cursor.isNull(cursor.getColumnIndex(
                        COLUMN_CUSTOM_RINGTONE));
                contact.lastTimeContacted = cursor.getString(cursor.getColumnIndex(
                        COLUMN_LAST_TIME_CONTACTED));

                contacts.add(getContactDetails(contact));

            } while (cursor.moveToNext());

            cursor.close();
        }

        return contacts;
    }

    private Contact getContactDetails(Contact contact){

        contact = getDetails(contact);
        contact = getSocialNetworks(contact);

        return contact;
    }

    private Contact getDetails(Contact contact) {

        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND ("
                + ContactsContract.Data.MIMETYPE + " = ? OR "
                + ContactsContract.Data.MIMETYPE + " = ? OR "
                + ContactsContract.Data.MIMETYPE + " = ? OR "
                + ContactsContract.Data.MIMETYPE + " = ?)";
        String[] selectionArgs = {contact.id,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI, null, selection,
                selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                switch (cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE))) {

                    case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                        contact = getPhone(cursor, contact);
                        break;

                    case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                        contact = getEmail(cursor, contact);
                        break;

                    case ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE:
                        contact = getWebSites(cursor, contact);
                        break;

                    case ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE:
                        contact = getImAccounts(cursor, contact);
                        break;
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        return contact;
    }

    private Contact getWebSites(Cursor cursor, Contact contact){

        contact.webUrls.add(new WebPage(
                cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Website.DISPLAY_NAME)),
                cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Website.URL))
        ));

        return contact;
    }

    private Contact getImAccounts(Cursor cursor, Contact contact){
        contact.imProtocols.add(new ImProtocol(
                cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Im.DATA)),
                cursor.getInt(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Im.PROTOCOL)),
                cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL))
        ));

        return contact;
    }

    private Contact getEmail(Cursor cursor, Contact contact){

        contact.emails.add(new Email(
                        cursor.getString(cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Email.ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE))
                )
        );

        return contact;
    }

    private Contact getPhone(Cursor cursor, Contact contact){

        contact.phoneNumbers.add(new PhoneNumber(
                        cursor.getString(cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)),
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
                )
        );

        return contact;
    }

    private Contact getSocialNetworks(Contact contact){

        String selection = ContactsContract.Data.CONTACT_ID + " = ?";
        String[] selectionArgs = { contact.id};

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI, null, selection,
                selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                if(!cursor.isNull(cursor.getColumnIndex("account_type"))){

                    if(!cursor.getString(cursor.getColumnIndex("account_type"))
                            .equals("com.google")){
                        contact.imProtocols.add(new ImProtocol(
                                cursor.getString(cursor.getColumnIndex("account_type")),
                                cursor.getString(cursor.getColumnIndex("sync1"))
                        ));
                    }

                }


            } while (cursor.moveToNext());

            cursor.close();
        }

        return contact;

    }


    public class Contact{

        public String name;
        public String id;
        String lookupKey;
        String lastTimeContacted;
        int timesContacted;
        boolean starred;
        boolean customRingtone;
        List<Email> emails = new ArrayList<>();
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        List<ImProtocol> imProtocols = new ArrayList<>();
        List<WebPage> webUrls = new ArrayList<>();

        public String toString(){

            return "Name: ".concat(name)
                    +"\nEmails: "+ Arrays.toString(emails.toArray())
                    +"\nPhones: "+ Arrays.toString(phoneNumbers.toArray())
                    +"\nIM: "+Arrays.toString(imProtocols.toArray())
                    +"\nWeb Urls: "+Arrays.toString(webUrls.toArray())
                    +"\nTimes Contacted: "+String.valueOf(timesContacted)
                    +"\nLast time contacted: "+String.valueOf(lastTimeContacted)
                    +"\nStarred: "+String.valueOf(starred)
                    +"\nCustom ringtone: "+String.valueOf(customRingtone);
        }
    }

    public class WebPage{
        public String name;
        String url;

        WebPage(String name, String url){
            this.name = name;
            this.url = url;
        }

        public String toString(){
            return name + "," + url;
        }
    }

    public class ImProtocol{
        String account;
        String protocol;

        ImProtocol(String account, String protocol){

            this.account = account;
            this.protocol = protocol;
        }

        ImProtocol(String account, int protocol, String customProtocol){

            this.account = account;

            switch (protocol){

                case -1:
                    this.protocol = customProtocol;
                    break;

                case 1:
                    this.protocol = "MSN";
                    break;

                case 2:
                    this.protocol = "YAHOO";
                    break;

                case 3:
                    this.protocol = "SKYPE";
                    break;

                case 4:
                    this.protocol = "QQ";
                    break;

                case 5:
                    this.protocol = "GOOGLE_TALK";
                    break;

                case 6:
                    this.protocol = "ICQ";
                    break;

                case 7:
                    this.protocol = "JABBER";
                    break;

                case 8:
                    this.protocol = "NETMEETING";
                    break;
            }
        }
        public String toString(){

            return account + "," + protocol;
        }

    }

    public class Email{
        String address;
        public String type;

        Email(String address, String type){
            this.address = address;
            this.type = type;
        }

        public String toString(){

            return address + "," + type;
        }
    }

    public class PhoneNumber{
        public String number;
        public String type;

        PhoneNumber(String number, String type){
            this.number = number;
            this.type = type;
        }

        public String toString(){
            return number + "," + type;
        }
    }
}