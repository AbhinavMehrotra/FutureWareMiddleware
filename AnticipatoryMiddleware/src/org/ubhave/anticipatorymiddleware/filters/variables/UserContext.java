package org.ubhave.anticipatorymiddleware.filters.variables;

import org.ubhave.anticipatorymiddleware.utils.Constants;


public class UserContext {

	public static final String LOCATION = "LOCATION";
	public static final String ACTIVITY = "ACTIVITY";
	public static final String SOCIAL = "SOCIAL";

	public static ActivityVariable ACTIVITY_ATTRIBUTES;
	public static LocationVariable LOCATION_ATTRIBUTES;
	public static SocialVariable SOCIAL_ATTRIBUTES;

	public static Boolean isValidContextType(String condition_variable){
		if(getContextTypeByConditionVariable(condition_variable) != "UNKNOWN")
			return true;
		return false;
	}
	
	private static String getContextTypeByConditionVariable(String condition_variable){
		if(condition_variable.equalsIgnoreCase(LOCATION))
			return LOCATION;
		else if(condition_variable.equalsIgnoreCase(ACTIVITY))
			return ACTIVITY;
		else if(condition_variable.startsWith("friend_ids_")) //see social variable
			return SOCIAL;
		else 
			return "UNKNOWN";
	}
	
	public static int getSensorIdByConditionVariable(String context_varaible){
		if(context_varaible.equalsIgnoreCase(LOCATION))
			return Constants.SENSOR_TYPE_LOCATION;
		else if(context_varaible.equalsIgnoreCase(ACTIVITY))
			return Constants.SENSOR_TYPE_ACCELEROMETER;
		else 
			return -1;
	}
	
	public static int getPredictorIdByConditionVariable(String context_variable){
		if(context_variable.equalsIgnoreCase(LOCATION))
			return Constants.PREDICTOR_TYPE_LOCATION;
		else if(context_variable.equalsIgnoreCase(ACTIVITY))
			return Constants.PREDICTOR_TYPE_ACTIVITY;
		else if(context_variable.startsWith("friend_ids_"))
			return Constants.PREDICTOR_TYPE_SOCIAL;
		else 
			return -1;
	}
}
