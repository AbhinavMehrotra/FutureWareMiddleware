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
import android.net.Uri;

import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pullsensor.SMSContentListData;
import com.ubhave.sensormanager.data.pullsensor.SMSContentReaderEntry;
import com.ubhave.sensormanager.sensors.SensorUtils;

public class SMSLogsReader 
{


	protected SensorData getSMSData(Context context) 
	{
		SMSContentListData data = new SMSContentListData(System.currentTimeMillis(), SensorUtils.getDefaultSensorConfig(SensorUtils.SENSOR_TYPE_SMS_CONTENT_READER));
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		int number_index = cursor.getColumnIndexOrThrow("address");
		int type_index = cursor.getColumnIndexOrThrow("type");
		int date_index = cursor.getColumnIndexOrThrow("date");
		int body_index = cursor.getColumnIndexOrThrow("body");  
		if (cursor.moveToFirst()) 
		{
			while (cursor.moveToNext()) 
			{
				String body = cursor.getString(body_index);
				String phone_number = cursor.getString(number_index);
				String date = cursor.getString(date_index);
				String type = cursor.getString(type_index);
				String type_of_SMS = null;
				switch (Integer.parseInt(type)) 
				{
				case 1:
					type_of_SMS = "INBOX";
					break;
				case 2:
					type_of_SMS = "SENT";
					break;
				case 3:
					type_of_SMS = "DRAFT";
					break;
				}
				if(body != null && phone_number != null && date != null && type_of_SMS != null) 
				{
					SMSContentReaderEntry entry = new SMSContentReaderEntry();
					entry.set("body", body);
					entry.set("number", phone_number);
					entry.set("date", date);
					entry.set("type", type_of_SMS);
					data.addContent(entry);
				}
			}
		}
		cursor.close();
		return data;
	}

}
