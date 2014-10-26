package org.ubhave.anticipatorymiddleware.classifiers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;

import com.ubhave.sensormanager.data.SensorData;

public class MircophoneClassifier  implements Classifier{

	private static final String TAG = "AnticipatoryManager";

	private final Context context;

	private final int sensor_id;
	
	public MircophoneClassifier (Context context){
		this.context =  context;
		this.sensor_id = Constants.SENSOR_TYPE_LOCATION;
	}

	@Override
	public void trainClassifier(JSONArray array) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<String> getAllLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String classifySensorData(SensorData data) {
		
		return null;
	}

}
