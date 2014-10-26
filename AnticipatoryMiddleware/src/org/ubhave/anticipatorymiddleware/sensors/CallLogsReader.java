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
