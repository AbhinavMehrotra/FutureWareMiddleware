package org.ubhave.anticipatorymiddleware.server;


public class Constants {

	
	public final static int SENSOR_TYPE_ACCELEROMETER = 5001;
	public final static int SENSOR_TYPE_BLUETOOTH = 5003;
	public final static int SENSOR_TYPE_LOCATION = 5004;
	public final static int SENSOR_TYPE_MICROPHONE = 5005;
	public final static int SENSOR_TYPE_WIFI = 5010;
	
	public final static int PREDICTOR_TYPE_ACTIVITY = 15001;
	public final static int PREDICTOR_TYPE_LOCATION = 15004;
	public final static int PREDICTOR_TYPE_VOICE = 15005;
	public final static int PREDICTOR_TYPE_WIFI = 15010;
	public final static int PREDICTOR_TYPE_SOCIAL = 15011;
	
	public final static int[] ALL_PREDICTORS = new int[] { PREDICTOR_TYPE_ACTIVITY, PREDICTOR_TYPE_LOCATION,
		PREDICTOR_TYPE_VOICE, PREDICTOR_TYPE_WIFI, PREDICTOR_TYPE_SOCIAL	};
	
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
	
}
