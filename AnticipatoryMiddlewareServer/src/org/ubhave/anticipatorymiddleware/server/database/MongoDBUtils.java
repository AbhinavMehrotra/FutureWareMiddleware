package org.ubhave.anticipatorymiddleware.server.database;

public class MongoDBUtils {


	public static final String Collection_Type_User_Registration = "Collection_Type_User_Registration";
	
	public static final String Collection_Type_User_Context = "Collection_Type_User_Context";

	public static final String Collection_Type_User_Prediction_Model = "Collection_Type_User_Prediction_Model";

	public static final String Key_Type_User_Id = "Key_Type_User_Id";
	
	public static final String Key_Type_User_Context = "Key_Type_User_Context";
	
	public static final String Key_Type_User_Context_Sampling_Rate = "Key_Type_User_Context_Sampling_Rate";
	
	public static final String Key_Type_User_Context_Life_Cycle_Period = "Key_Type_User_Context_Life_Cycle_Period";
	
	public static final String Key_Type_User_Privacy_Policy = "Key_Type_User_Privacy_Policy";
	
	public static final String Key_Type_User_OSN_IDS = "Key_Type_User_OSN_IDS";
	
	public static final String Key_Type_User_Prediction_Data = "Key_Type_User_Prediction_Data";
	
	public static final String Key_Type_User_Friend_Ids = "Key_Type_User_Friend_Ids";
	
	public static final String Key_Type_Predictor_Type = "Key_Type_Predictor_Type";
	
	public static final String Key_Type_Subscription_Id = "Key_Type_Subscription_Id";
	
	public static final String Key_Type_Sensor_Type(int sensor_type){
		return "Key_Type_Sensor_Type_"+sensor_type;
	}
	
}
