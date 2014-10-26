package org.ubhave.anticipatorymiddleware.classifiers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.ubhave.anticipatorymiddleware.sensors.ContactDetails;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.util.Log;

import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pullsensor.AbstractContentReaderEntry;
import com.ubhave.sensormanager.data.pullsensor.ApplicationData;
import com.ubhave.sensormanager.data.pullsensor.SMSContentListData;

public class SMSLogsClassifier  implements Classifier{

	private static final String TAG = "AnticipatoryManager";

	private final Context context;

	private final int sensor_id;

	public SMSLogsClassifier (Context context){
		this.context =  context;
		this.sensor_id = Constants.SENSOR_TYPE_LOCATION;
	}

	@Override
	public void trainClassifier(JSONArray array) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<String> getAllLabels() {
		ArrayList<String> names = new ArrayList<String>();
		ContactDetails contact_details = new ContactDetails();
		Map<String, String> contacts = contact_details.getContacts(context);
		for(Map.Entry<String, String> contact : contacts.entrySet())
		{
			names.add(contact.getValue());
		}
		return names;
	}

	@Override
	public String classifySensorData(SensorData data) 
	{
		ContactDetails contact_details = new ContactDetails();
		SharedPreferences sp = new SharedPreferences(context);
		int context_sampling_rate = sp.getInt(sp.CONTEXT_SAMPLING_RATE);
		JSONArray classified_data = new JSONArray();
		SMSContentListData sms_list_data = (SMSContentListData) data;
		ArrayList<AbstractContentReaderEntry> sms_list = sms_list_data.getContentList();
		for(AbstractContentReaderEntry entry : sms_list)
		{
			long time = Long.parseLong(entry.get("date"));
			String number = entry.get("number");
			String name = contact_details.getName(context, number);
			if(validateTime(time, context_sampling_rate))
			{
				classified_data.put(name);
			}
			else
			{
				break;
			}
		}
		Log.i(TAG, "Recent sms details: " + classified_data.toString());
		return classified_data.toString();
	}

	private static boolean validateTime(long entry_time, int context_sampling_rate)
	{
		Calendar entry_time_calendar = Calendar.getInstance();
		entry_time_calendar.setTimeInMillis(entry_time);
		Calendar current_time_calendar = Calendar.getInstance();
		long csr_in_millis = context_sampling_rate * 60 * 1000;
		long error = 1000; // error tolerance of 1s
		long difference = current_time_calendar.getTimeInMillis() - entry_time_calendar.getTimeInMillis();	
		long expected_difference = csr_in_millis + error;	
		boolean result = (difference <= expected_difference) ? true : false;	
		return result;	
	}


}
