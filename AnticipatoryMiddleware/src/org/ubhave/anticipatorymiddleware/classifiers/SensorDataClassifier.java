package org.ubhave.anticipatorymiddleware.classifiers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.util.Log;

import com.ubhave.sensormanager.data.SensorData;

public class SensorDataClassifier {

	private static final String TAG = "AnticipatoryManager";
	
	private final int sensor_type;
	
	private final Classifier classifier;
	
	public SensorDataClassifier(Context context, int sensor_type){
		this.sensor_type = sensor_type;
		switch (this.sensor_type){
		case Constants.SENSOR_TYPE_ACCELEROMETER :
			classifier = new AccelerometerClassifier(context);
			break;
		case Constants.SENSOR_TYPE_APPLICATION :
			classifier = new ApplicationClassifier(context);
			break;
		case Constants.SENSOR_TYPE_BLUETOOTH :
			classifier = new BluetoothClassifier(context);
			break;
		case Constants.SENSOR_TYPE_CALL_LOGS :
			classifier = new CallLogsClassifier(context);
			break;
		case Constants.SENSOR_TYPE_LOCATION :
			classifier = new LocationClassifier(context);
			break;
		case Constants.SENSOR_TYPE_MICROPHONE :
			classifier = new MircophoneClassifier(context);
			break;
		case Constants.SENSOR_TYPE_SMS_LOGS :
			classifier = new SMSLogsClassifier(context);
			break;
		case Constants.SENSOR_TYPE_WIFI :
			classifier = new WiFiClassifier(context);
			break;
		default :
			classifier = null;
			Log.e(TAG, "SensorDataClassifier sensor: " + sensor_type + " not recognised.");
		}
	}
	
	public void trainClassifier(JSONArray array){
		classifier.trainClassifier(array);
	}
	
	public ArrayList<String> getAllLabels(){			
		return classifier.getAllLabels();
	}

	public String classifySensorData(SensorData sensorData){
		String classified_sensor_data = classifier.classifySensorData(sensorData);		
		Log.d(TAG, "SensorDataClassifier classified_sensor_data: " + classified_sensor_data);
		return classified_sensor_data;
	}



}
