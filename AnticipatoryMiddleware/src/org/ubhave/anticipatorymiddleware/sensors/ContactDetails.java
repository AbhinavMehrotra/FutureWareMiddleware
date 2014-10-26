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
