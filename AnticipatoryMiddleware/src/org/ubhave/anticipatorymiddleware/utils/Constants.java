package org.ubhave.anticipatorymiddleware.utils;


public class Constants {

	
	public final static int SENSOR_TYPE_ACCELEROMETER = 5001;
	public final static int SENSOR_TYPE_BLUETOOTH = 5003;
	public final static int SENSOR_TYPE_LOCATION = 5004;
	public final static int SENSOR_TYPE_MICROPHONE = 5005;
	public final static int SENSOR_TYPE_WIFI = 5010;
	public final static int SENSOR_TYPE_APPLICATION = 5012;
	public final static int SENSOR_TYPE_SMS_LOGS = 5013;
	public final static int SENSOR_TYPE_CALL_LOGS = 5014;
	
	public final static int PREDICTOR_TYPE_ACTIVITY = 15001;
	public final static int PREDICTOR_TYPE_LOCATION = 15004;
	public final static int PREDICTOR_TYPE_VOICE = 15005;
	public final static int PREDICTOR_TYPE_WIFI = 15010;
	public final static int PREDICTOR_TYPE_SOCIAL = 15011;
	public final static int PREDICTOR_TYPE_APPLICATION = 15012;
	public final static int PREDICTOR_TYPE_SMS_LOGS = 15013;
	public final static int PREDICTOR_TYPE_CALL_LOGS = 15014;
	
	public final static int[] ALL_PREDICTORS = new int[] { PREDICTOR_TYPE_ACTIVITY, PREDICTOR_TYPE_LOCATION,
		PREDICTOR_TYPE_VOICE, PREDICTOR_TYPE_WIFI, PREDICTOR_TYPE_SOCIAL	};
	
	public final static int[] ALL_SENSORS = new int[] {
		SENSOR_TYPE_ACCELEROMETER,
		SENSOR_TYPE_BLUETOOTH,
		SENSOR_TYPE_LOCATION,
		SENSOR_TYPE_MICROPHONE,
		SENSOR_TYPE_WIFI,
		SENSOR_TYPE_APPLICATION,
		SENSOR_TYPE_SMS_LOGS,
		SENSOR_TYPE_CALL_LOGS
	};
	
	public static Boolean isValidPredictor(int predictor){
		for(int  aPredictor: ALL_PREDICTORS){
			if(aPredictor == predictor)
				return true;
		}
		return false;
	}
	
	public static String getSensorName(int sensor){
		switch(sensor){
		case SENSOR_TYPE_ACCELEROMETER: return "SENSOR_TYPE_ACCELEROMETER";
		case SENSOR_TYPE_LOCATION: return "SENSOR_TYPE_LOCATION";
		default: return "UNKNOWN_SENSOR";
		}
	}
	
	public static String getPredictorName(int predictor){
		switch(predictor){
		case PREDICTOR_TYPE_ACTIVITY: return "PREDICTOR_TYPE_ACTIVITY";
		case PREDICTOR_TYPE_LOCATION: return "PREDICTOR_TYPE_LOCATION";
		default: return "UNKNOWN_PREDICTOR";
		}
	}

	public static int getSensor(int predictor){
		return predictor - 10000;
	}

	public static int getPredictor(int sensor){
		return sensor + 10000;
	}
	
	
	public static boolean isLabelrequired(int sensor){
		if(sensor == SENSOR_TYPE_APPLICATION || sensor == SENSOR_TYPE_BLUETOOTH || 
				sensor == SENSOR_TYPE_WIFI || sensor == SENSOR_TYPE_ACCELEROMETER|| 
				sensor == SENSOR_TYPE_CALL_LOGS || sensor == SENSOR_TYPE_SMS_LOGS){
			return false;
		}
		return true;
	}

}
