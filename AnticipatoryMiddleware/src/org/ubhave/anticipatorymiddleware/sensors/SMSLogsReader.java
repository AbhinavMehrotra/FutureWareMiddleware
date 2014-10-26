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
