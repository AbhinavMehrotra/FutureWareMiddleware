package org.ubhave.anticipatorymiddleware.classifiers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.ubhave.anticipatorymiddleware.sensors.ActivityTracker;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;

import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pullsensor.AccelerometerData;

public class AccelerometerClassifier implements Classifier{

	private static final String TAG = "AnticipatoryManager";

	private final Context context;

	private final int sensor_id;
	
	public AccelerometerClassifier (Context context){
		this.context =  context;
		this.sensor_id = Constants.SENSOR_TYPE_LOCATION;
	}
	
	@Override
	public void trainClassifier(JSONArray array) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<String> getAllLabels() 
	{
		return ActivityTracker.getAllLabels();
	}

	@Override
	public String classifySensorData(SensorData data) 
	{
		ArrayList<float[]> activity_data = ((AccelerometerData)data).getSensorReadings();
		float activity_value = activity_data.get(0)[0];
		String activity = ActivityTracker.getInstance(context).getNameFromType(activity_value);
		return activity;
	}

	
	
}
