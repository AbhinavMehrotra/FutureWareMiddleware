package org.ubhave.anticipatorymiddleware.utils;

public class PredictorType {


	public final static int ACTIVITY_PREDICTOR = 15001;
	public final static int SOCIAL_PREDICTOR = 15003;
	public final static int LOCATION_PREDICTOR = 15004;
	
	public final static int[] ALL_PREDICTORS = new int[] { ACTIVITY_PREDICTOR, 
		LOCATION_PREDICTOR, SOCIAL_PREDICTOR};
	
	public static Boolean isValidPredictor(int predictor){
		for(int  aPredictor: ALL_PREDICTORS){
			if(aPredictor == predictor)
				return true;
		}
		return false;
	}
	
	public static String getPreditorName(int predictor){
		switch(predictor){
		case ACTIVITY_PREDICTOR: return "ACTIVITY_PREDICTOR";
		case LOCATION_PREDICTOR: return "LOCATION_PREDICTOR";
		case SOCIAL_PREDICTOR: return "SOCIAL_PREDICTOR";
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
