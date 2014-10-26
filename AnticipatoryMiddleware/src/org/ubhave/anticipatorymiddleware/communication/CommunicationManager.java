
package org.ubhave.anticipatorymiddleware.communication;

import java.io.IOException;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.utils.ObjectSerializer;

import android.content.Context;
import android.util.Log;

public class CommunicationManager {

	private final String TAG = "AnticipatoryManager";
	private final SharedPreferences sp;
	private final String ip;
	private final int port;

	public CommunicationManager(Context app_context){
		this.sp = new SharedPreferences(app_context);
		this.ip = sp.getString(sp.SERVER_IP);
		this.port = sp.getInt(sp.SERVER_PORT);
	}

	public void transmitPredictionModelData(String user_id, int predictor_type, JSONObject merged_model){
		//TODO: Transmit model		
		try {
			JSONObject message = new JSONObject();
			message.put(JSONKeys.DATA_TYPE, MessageType.UPDATED_PREDICTION_MODEL);
			message.put(JSONKeys.USER_ID, user_id);
			message.put("PREDICTOR_TYPE", predictor_type);
			message.put("MODEL", merged_model);
			transmitData(message);
		} catch (JSONException e) {
			Log.e(TAG, "Transmit model: unable to write in JSON object: "+e.toString());
		}
	}

	public void transmitPrivacyData(String user_id, Set<String> access_granted_friend_ids, Set<Integer> access_granted_predictor_types){
		//TODO: Transmit privacy data		
		try {
			JSONObject obj = new JSONObject();
			obj.put(JSONKeys.DATA_TYPE, MessageType.UPDATED_PRIVACY_POLICY);
			obj.put(JSONKeys.USER_ID, user_id);
			obj.put(JSONKeys.Access_Granted_Friends_List, access_granted_friend_ids);
			obj.put(JSONKeys.Access_Granted_Predictors_List, access_granted_predictor_types);
			transmitData(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void transmitUpdatedUserContext(String user_id, int sensor_type, JSONObject user_context){
		try {
			JSONObject obj = new JSONObject();
			obj.put(JSONKeys.DATA_TYPE, MessageType.USER_CONTEXT);
			obj.put(JSONKeys.USER_ID, user_id);
			obj.put(JSONKeys.SENSOR_TYPE, sensor_type);
			obj.put(JSONKeys.USER_CONTEXT, user_context); //TODO:changed user_context from string to jsonobject, make changes on server side
			transmitData(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	public void transmitUserRegistrationData(String user_id, JSONObject privacy_policy, 
			Set<String> user_osn_ids, int context_sampling_rate, int context_life_cycle_period){
		try {
			JSONObject obj = new JSONObject();
			obj.put(JSONKeys.DATA_TYPE, MessageType.REGISTRATION);
			obj.put(JSONKeys.USER_ID, user_id);
			obj.put(JSONKeys.PRIVACY_POLICY, privacy_policy);
			obj.put(JSONKeys.USER_OSN_IDS, user_osn_ids);
			obj.put(JSONKeys.CONTEXT_SAMPLING_RATE, context_sampling_rate);
			obj.put(JSONKeys.CONTEXT_LIFE_CYCLE_PERIOD, context_life_cycle_period);
			transmitData(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}

	public void transmitGroupPredictionRequest(String user_id, int subscription_id, Set<String> friend_ids,
			int predictor_type, String state_to_be_predicted){
		try {
			JSONObject obj = new JSONObject();
			obj.put(JSONKeys.DATA_TYPE, MessageType.GROUP_PREDICTION_REQUEST);
			obj.put(JSONKeys.USER_ID, user_id);
			obj.put(JSONKeys.SUBSCRIPTION_ID, subscription_id);
			obj.put(JSONKeys.FRIEND_IDS, friend_ids);
			obj.put(JSONKeys.PREDICTOR_TYPE, predictor_type);
			obj.put(JSONKeys.PREDICTION_TYPE, JSONKeys.PREDICTION_TYPE_STATE_SPECIFIC);
			obj.put(JSONKeys.PREDICTION_TYPE_VALUE, state_to_be_predicted);
			transmitData(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void transmitGroupPredictionRequest(String user_id, int subscription_id, Set<String> friend_ids,
			int predictor_type, long required_prediction_time){
		try {
			JSONObject obj = new JSONObject();
			obj.put(JSONKeys.DATA_TYPE, MessageType.GROUP_PREDICTION_REQUEST);
			obj.put(JSONKeys.USER_ID, user_id);
			obj.put(JSONKeys.SUBSCRIPTION_ID, subscription_id);
			obj.put(JSONKeys.FRIEND_IDS, friend_ids);
			obj.put(JSONKeys.PREDICTOR_TYPE, predictor_type);
			obj.put(JSONKeys.PREDICTION_TYPE, JSONKeys.PREDICTION_TYPE_TIME_SPECIFIC);
			obj.put(JSONKeys.PREDICTION_TYPE_VALUE, required_prediction_time);
			transmitData(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void transmitRemoteQueryResponse(String user_id, int subscription_id, int predictor_type, 
			PredictorData predictor_data){
		try {
			JSONObject obj = new JSONObject();
			obj.put(JSONKeys.DATA_TYPE, MessageType.GROUP_PREDICTION_REQUEST);
			obj.put(JSONKeys.USER_ID, user_id);
			obj.put(JSONKeys.SUBSCRIPTION_ID, subscription_id);
			obj.put(JSONKeys.PREDICTOR_TYPE, predictor_type);
			obj.put(JSONKeys.PREDICTOR_DATA, ObjectSerializer.toString(predictor_data));
			transmitData(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public void transmitContextSamplingRate(String user_id, int context_sampling_rate){
		try {
			JSONObject obj = new JSONObject();
			obj.put(JSONKeys.DATA_TYPE, MessageType.Context_SAMPLING_RATE);
			obj.put(JSONKeys.USER_ID, user_id);
			obj.put(JSONKeys.CONTEXT_SAMPLING_RATE, context_sampling_rate);
			transmitData(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
	}
	
	public void transmitContextLifeCyclePeriod(String user_id, int context_life_cycle_period){
		try {
			JSONObject obj = new JSONObject();
			obj.put(JSONKeys.DATA_TYPE, MessageType.Context_Life_Cycle_Period);
			obj.put(JSONKeys.USER_ID, user_id);
			obj.put(JSONKeys.CONTEXT_LIFE_CYCLE_PERIOD, context_life_cycle_period);
			transmitData(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
	}

	private void transmitData(JSONObject obj){
		TCPClient tcp_client = TCPClient.getInstance(ip, port);
		tcp_client.startSending(obj.toString());		
	}


}
