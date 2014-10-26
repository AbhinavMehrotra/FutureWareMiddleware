package org.ubhave.anticipatorymiddleware.sensors;

import java.util.ArrayList;

import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.time.Time;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pullsensor.AccelerometerData;
import com.ubhave.sensormanager.sensors.SensorUtils;

import android.content.Context;

public class ActivityTracker {
	
	private final Context context;

	private final int sampling_rate;
	
	private final long time_interval;
	
	private final SharedPreferences sp;
	
	private static ActivityTracker instance;
	
	public static ActivityTracker getInstance(Context context)
	{
		if(instance == null)
		{
			return new ActivityTracker(context);
		}
		else
		{
			return instance;
		}
	}
	
	private ActivityTracker(Context app_context) 
	{
		instance = this;
		this.context = app_context;
		this.sp = new SharedPreferences(app_context);
		SensorManager sm = new SensorManager(app_context);
		this.sampling_rate = sm.getContextSamplingRate() ;
		this.time_interval = this.sampling_rate * 60 * 1000;
		GoogleActivityRecognition.getInstance(app_context, this.time_interval).startSensing();
	}
	
	
	public SensorData getSensorData()
	{
		String activity = GoogleActivityRecognition.getActivityResult();
		ArrayList<float[]> sensorReadings = new ArrayList<float[]>();
		float[] values = new float[1];
		values[0] = getValue(activity);
		sensorReadings.add(values);
		AccelerometerData data = new AccelerometerData(Time.getCurrentTimeInMilliseconds(), SensorUtils.getDefaultSensorConfig(Constants.SENSOR_TYPE_ACCELEROMETER));	
		data.setSensorReadings(sensorReadings);
		return data;
	}
	
	public String decodedData(ArrayList<float[]> data)
	{
		return getNameFromType(data.get(0)[0]);
	}
	
	private float getValue(String activity) {
        if(activity.equalsIgnoreCase("in_vehicle")) {
        	return 1.1f;
        }
        else if(activity.equalsIgnoreCase("on_bicycle")) {
        	return 2.1f;
        }
        else if(activity.equalsIgnoreCase("on_foot")) {
        	return 3.1f;
        }
        else if(activity.equalsIgnoreCase("still")) {
        	return 4.1f;
        }
        else if(activity.equalsIgnoreCase("tilting")) {
        	return 5.1f;
        }
        return 0.1f; //unknown
    }
	
	public String getNameFromType(float value) {
		if(value == 1.1f) {
        	return "in_vehicle";
        }
        else if(value == 2.1f) {
        	return "on_bicycle";
        }
        else if(value == 3.1f) {
        	return "on_foot";
        }
        else if(value == 4.1f) {
        	return "still";
        }
        else if(value == 5.1f) {
        	return "tilting";
        }
        return "unknown";
    }
	
	public static ArrayList<String> getAllLabels() {
		ArrayList<String> labels_list = new ArrayList<String>();
		labels_list.add("in_vehicle");
		labels_list.add("on_bicycle");
		labels_list.add("on_foot");
		labels_list.add("still");
		labels_list.add("tilting");
		labels_list.add("unknown");
		return labels_list;
	}
}
