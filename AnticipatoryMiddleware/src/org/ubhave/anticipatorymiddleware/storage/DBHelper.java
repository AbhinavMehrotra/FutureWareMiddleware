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
package org.ubhave.anticipatorymiddleware.storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper {
	

	private static final String TAG = "AnticipatoryManager";

	private SQLiteHelper dbHelper;
	public final String DB_NAME;
	public final String _ID = "_id";
	public final String _value = "_value";
	
	/**
	 * Constructor
	 * @param context Application context
	 * @param db_name Db name. Use {@link SQLiteUtils} to get such constants.
	 */
	public DBHelper(Context context, String db_name){
		this.DB_NAME = db_name;
		dbHelper = new SQLiteHelper(context, DB_NAME);
	}
	
	
	/**
	 * Adds a record to the end position of the table.
	 * @param table_name The table name to compile the query against. Use {@link SQLiteUtils} to get such constants.
	 * @param value The value to be added.
	 * @return The row ID of the newly inserted row, or -1 if an error occurred
	 */
	@SuppressLint("NewApi")
	public long addRecord(String table_name, String value){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		int id;
		String[] cols = new String[]{_ID, _value};
		Cursor c = database.query(true, table_name, cols, null, null, null, null, null, null, null);
		if(c != null){
			id = c.getCount();
			c.close();
		}
		else{
			id = 0;
		}
		ContentValues values = new ContentValues();
		values.put(_ID, id);
		values.put(_value, value);
		long result = database.insert(table_name, null, values);
		database.close();
		return result;
	}
	
	
	/**
	 * Update a record at the given position. 
	 * @param table_name The table name to compile the query against. Use {@link SQLiteUtils} to get such constants.
	 * @param value The value to be replaced.
	 * @param row_id position of the row which needs to be updated.
	 * @return The number of rows affected
	 */
	public int updateRecord(String table_name, String value, int row_id){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(_ID, row_id);
		values.put(_value, value);
		String where_clause = "_id " + "=" + row_id;
		return database.update(table_name, values, where_clause, null);
	}

	/**
	 * Returns the records as a Cursor.
	 * @param table_name The table name to compile the query against. Use {@link SQLiteUtils} to get such constants.
	 * @return Records as a Cursor.
	 */
	@SuppressLint("NewApi")
	public Cursor selectRecords(String table_name){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String[] cols = new String[]{_ID, _value};
		Cursor c = database.query(true, table_name, cols, null, null, null, null, null, null, null);
		if(c != null){
			c.moveToFirst();
		} 
		database.close();
		return c;
	}

	/**
	 * Returns the records as a String[]. 
	 * @param table_name The table name to compile the query against. Use {@link SQLiteUtils} to get such constants.
	 * @return Records as a String[]
	 */
	@SuppressLint("NewApi")
	public String[] getRecords(String table_name){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String[] cols = new String[]{_ID, _value};
		Cursor c = database.query(true, table_name, cols, null, null, null, null, null, null, null);
		if(c != null){
			c.moveToFirst();
		} 
		String[] result = new String[c.getCount()];
		for(int i=0; i<c.getCount(); i++){
			result[i] = c.getString(c.getColumnIndex(_value));
			c.moveToNext();
		}
		c.close();
		database.close();
		return result;
	}

	@SuppressLint("NewApi")
	public JSONArray getRecordsAsJSONArray(String table_name){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		JSONArray json_array = new JSONArray();
		String[] cols = new String[]{_ID, _value};
		Cursor c = database.query(true, table_name, cols, null, null, null, null, null, null, null);
		if(c != null){
			c.moveToFirst();
		} 
		for(int i=0; i<c.getCount(); i++){
			String json_string = c.getString(c.getColumnIndex(_value));
			try {
				JSONObject json_object = new JSONObject(json_string);
				json_array.put(json_object);
			} catch (JSONException e) {
				try
				{
					JSONArray json_array_row = new JSONArray(json_string);
					json_array.put(json_array_row);
				}
				catch(JSONException e1)
				{
					Log.e(TAG, e.toString());
				}
			}
			c.moveToNext();
		}
		c.close();
		database.close();
		return json_array;
	}
	/**	
	 * To delete a table from DB
	 * @param table_name the table to delete. Use {@link SQLiteUtils} to get such constants.
	 * @return the number of rows affected

	 */
	public int deleteTable(String table_name){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		int result = database.delete(table_name, null, null);
		database.close();
		return result;
	}
}



