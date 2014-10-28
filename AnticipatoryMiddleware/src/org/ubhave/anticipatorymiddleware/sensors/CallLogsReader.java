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

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pullsensor.CallContentListData;
import com.ubhave.sensormanager.data.pullsensor.CallContentReaderEntry;
import com.ubhave.sensormanager.sensors.SensorUtils;

public class CallLogsReader {


	protected SensorData getCallData(Context context) 
	{
		CallContentListData data = new CallContentListData(System.currentTimeMillis(), SensorUtils.getDefaultSensorConfig(SensorUtils.SENSOR_TYPE_CALL_CONTENT_READER));
		Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
		int number_index = cursor.getColumnIndex(CallLog.Calls.NUMBER);
		int name_index = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
		int type_index = cursor.getColumnIndex(CallLog.Calls.TYPE);
		int date_index = cursor.getColumnIndex(CallLog.Calls.DATE);
		int duration_index = cursor.getColumnIndex(CallLog.Calls.DURATION);       
		if (cursor.moveToFirst()) 
		{
			while (cursor.moveToNext())
			{
				String phone_number = cursor.getString(number_index);
				String name = cursor.getString(name_index);
				name = (name == null) ? "" : name;
				String type_code_string = cursor.getString(type_index);
				String date = cursor.getString(date_index);			
				String duration = cursor.getString(duration_index);
				String type = null;
				int type_code_int = Integer.parseInt(type_code_string);
				switch (type_code_int) 
				{
				case CallLog.Calls.OUTGOING_TYPE:
					type = "OUTGOING";
					break;
				case CallLog.Calls.INCOMING_TYPE:
					type = "INCOMING";
					break;
				case CallLog.Calls.MISSED_TYPE:
					type = "MISSED";
					break;
				}
				CallContentReaderEntry entry = new CallContentReaderEntry();
				entry.set("number", phone_number);
				entry.set("name", name);
				entry.set("duration", duration);
				entry.set("date", date);
				entry.set("type", type);
				data.addContent(entry);
			}
		}
		cursor.close();
		return data;
	}
	
	protected String getLastOutgoingCall(Context context)
	{
		return CallLog.Calls.getLastOutgoingCall(context);
	}

}
