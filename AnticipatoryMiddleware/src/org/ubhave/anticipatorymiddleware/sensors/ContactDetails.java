/*******************************************************************************
 *
 * FutureWare Middleware
 *
 * Copyright (c) ${2014}, University of Birmingham
 * Abhinav Mehrotra, a.mehrotra@cs.bham.ac.uk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Birmingham 
 *       nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE ABOVE COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *******************************************************************************/
package org.ubhave.anticipatorymiddleware.sensors;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactDetails {

	public Map<String, String> getContacts(Context context)
	{
		Map<String, String> contacts = new HashMap<String, String>();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection    = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER};

		Cursor people = context.getContentResolver().query(uri, projection, null, null, null);

		int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

		if(people.moveToFirst())
		{
			do 
			{
				String name   = people.getString(indexName);
				String number = people.getString(indexNumber);
				contacts.put(number, name);
			} 
			while (people.moveToNext());
		}
		return contacts;
	}


	public String getName(Context context, String number)
	{
		String name = "";
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection    = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER};

		Cursor people = context.getContentResolver().query(uri, projection, null, null, null);

		int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

		if(people.moveToFirst())
		{
			do 
			{
				if(people.getString(indexNumber).equalsIgnoreCase(number))
				{
					name   = people.getString(indexName);
					break;
				}
			} 
			while (people.moveToNext());
		}
		return name;
	}	



	public String getNumber(Context context, String name)
	{
		String number = "";
		if(name.equalsIgnoreCase(""))
		{
			return number;
		}
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection    = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER};

		Cursor people = context.getContentResolver().query(uri, projection, null, null, null);

		int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

		if(people.moveToFirst())
		{
			do 
			{
				if(people.getString(indexName).equalsIgnoreCase(name))
				{
					number   = people.getString(indexNumber);
					break;
				}
			} 
			while (people.moveToNext());
		}
		return number;
	}


	public Bitmap retrieveContactPhoto(Context context, String number) 
	{
		Bitmap photo = null;
		try 
		{
			InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
					ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(getId(context, number))));
			if (inputStream != null) 
			{
				photo = BitmapFactory.decodeStream(inputStream);
			}
			inputStream.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return photo;
	}
	
	
	private String getId(Context context, String number)
	{
		String id = null;
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER };
        Cursor people = context.getContentResolver().query(uri, projection, null, null, null);
        
        int indexId = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
		int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

		if(people.moveToFirst())
		{
			do 
			{
				if(people.getString(indexNumber).equalsIgnoreCase(number))
				{
					id   = people.getString(indexId);
					break;
				}
			} 
			while (people.moveToNext());
		}
		return id;
	}


}
