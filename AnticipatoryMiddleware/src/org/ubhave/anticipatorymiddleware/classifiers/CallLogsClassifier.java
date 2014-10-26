package org.ubhave.anticipatorymiddleware.classifiers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.json.JSONArray;
import org.ubhave.anticipatorymiddleware.sensors.ContactDetails;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.util.Log;

import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pullsensor.AbstractContentReaderEntry;
import com.ubhave.sensormanager.data.pullsensor.CallContentListData;
import com.ubhave.sensormanager.data.pullsensor.SMSContentListData;

public class CallLogsClassifier  implements Classifier{

	private static final String TAG = "AnticipatoryManager";

	private final Context context;

	private final int sensor_id;

	public CallLogsClassifier (Context context){
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
		JSONArray classified_data = new JSONArray();
		ContactDetails contact_details = new ContactDetails();
		CallContentListData call_list_data = (CallContentListData) data;
		ArrayList<AbstractContentReaderEntry> call_list = call_list_data.getContentList();
		for(AbstractContentReaderEntry entry : call_list)
		{
			long time = Long.parseLong(entry.get("date"));
			long call_duration_in_millis = Integer.parseInt(entry.get("duration")) * 1000;
			String number = entry.get("number");
			String name = contact_details.getName(context, number);
			SharedPreferences sp = new SharedPreferences(context);
			int context_sampling_rate = sp.getInt(sp.CONTEXT_SAMPLING_RATE);
			if(validateTime((time + call_duration_in_millis), context_sampling_rate))
			{
				classified_data.put(name);
			}
			else
			{
				break;
			}
		}
		Log.i(TAG, "Recent call details: " + classified_data.toString());
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
