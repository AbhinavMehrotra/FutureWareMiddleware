package org.ubhave.anticipatorymiddleware.privacy;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.communication.CommunicationManager;
import org.ubhave.anticipatorymiddleware.communication.JSONKeys;
import org.ubhave.anticipatorymiddleware.communication.MQTTService;
import org.ubhave.anticipatorymiddleware.communication.ServerSettings;
import org.ubhave.anticipatorymiddleware.sensors.SensorManager;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.storage.FileManager;
import org.ubhave.anticipatorymiddleware.storage.FilePathGenerator;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PrivacyManager {

	private static final String TAG = "AnticipatoryManager";
	private final Context app_context;
	private final FileManager f_manager;
	private final FilePathGenerator file_path_generator; 
	private final String file_path;
	private JSONObject privacy_data;
	private final String Enable_Data_Transmission_To_Server = "Enable_Data_Transmission_To_Server";
	private final String Friends_List = JSONKeys.Access_Granted_Friends_List;
	private final String Predictors_List = JSONKeys.Access_Granted_Predictors_List;
	private final String USER_OSN_IDS = JSONKeys.USER_OSN_IDS;

	public PrivacyManager(Context app_context){
		this.app_context = app_context;
		this.f_manager = new FileManager();
		this.file_path_generator = new FilePathGenerator();
		this.file_path = file_path_generator.generatePrivacyDataFilePath(app_context);
		this.privacy_data = f_manager.readJSONObject(file_path);
		try {
			if(this.privacy_data.getJSONArray("DATA") != null){
				this.privacy_data.remove("DATA");
				setBackToDefault();
				updateFile();
			}
		} catch (JSONException e) {
			//do nothing
		}
	}

	private void setBackToDefault(){
		enableDataTransmissionToServer(false);
		allowPredictorAccess(new HashSet<Integer>());
		givePredictionModelReadAccessToFriends(new HashSet<String>());
	}


	//this is required to make changes 
	public void commitChanges(){
		SharedPreferences sp = new SharedPreferences(this.app_context);
		PrivacyManager pm = new PrivacyManager(this.app_context);
		if((!sp.getBoolean(sp.IS_USER_REGISTERED)) && pm.isDataTransmissionToServerEnabled()){
			CommunicationManager cm = new CommunicationManager(this.app_context);
			String user_id = sp.getString(sp.USER_ID);		
			JSONObject privacy_policy = pm.getPrivacyData();
			Set<String> user_osn_ids = pm.getMyOSNIds();
			SensorManager sm = new SensorManager(this.app_context);
			int context_sampling_rate = sm.getContextSamplingRate();
			int context_life_cycle_period = sm.getContextLifeCycleTimePeriod();
			cm.transmitUserRegistrationData(user_id, privacy_policy, user_osn_ids, context_sampling_rate, context_life_cycle_period);
			sp.add(sp.IS_USER_REGISTERED, true);
		}

		if(isDataTransmissionToServerEnabled()){
			CommunicationManager communication_manager = new CommunicationManager(app_context);		
			String user_id = sp.getString(sp.USER_ID);
			Set<Integer> access_granted_predictor_types = this.getAccessGrantedPredictor();
			Set<String> access_granted_friend_ids = this.getAccessGrantedFriends();
			communication_manager.transmitPrivacyData(user_id, access_granted_friend_ids, access_granted_predictor_types);		
		}
		startMqttService();
	}

	public JSONObject getPrivacyData(){
		return this.privacy_data;
	}

	private void updateFile(){
		f_manager.deleteFile(file_path);
		f_manager.writeJSONObject(privacy_data, file_path);		
	}

	public void setMyOSNAccounts(Set<String> my_osn_accounts){
		try {
			privacy_data.put(USER_OSN_IDS, my_osn_accounts);
			updateFile();
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
	}

	public Set<String> getMyOSNIds(){		
		Set<String> my_osn_ids = new HashSet<String>();
		try {			
			JSONArray predictors_array = privacy_data.getJSONArray(USER_OSN_IDS);
			for(int i =0; i<predictors_array.length(); i++){
				my_osn_ids.add(predictors_array.getString(i));
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
		return my_osn_ids;
	}

	public void enableDataTransmissionToServer(boolean value){
		try {
			SharedPreferences sp = new SharedPreferences(this.app_context);
			sp.add(sp.IS_SERVER_UPDATE_REQUIRED, true);
			privacy_data.put(Enable_Data_Transmission_To_Server, value);
			updateFile();
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
	}

	public boolean isDataTransmissionToServerEnabled(){
		try {
			return privacy_data.getBoolean(Enable_Data_Transmission_To_Server);
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
		return false;
	}

	public void allowPredictorAccess(Set<Integer> predictor_types){
		try {			
			JSONArray predictors_array = privacy_data.getJSONArray(Predictors_List);
			for(int id: predictor_types){
				predictors_array.put(id);
			}
			privacy_data.remove(Predictors_List);
			privacy_data.put(Predictors_List, predictors_array);
			updateFile();
		} catch (JSONException e) {
			try {
				privacy_data.put(Predictors_List, new JSONArray());
				updateFile();
			} catch (JSONException e1) {
				Log.e(TAG, e1.toString());
			}
		}
	}

	public Set<Integer> getAccessGrantedPredictor(){
		Set<Integer> predictor_types = new HashSet<Integer>();
		try {			
			JSONArray predictors_array = privacy_data.getJSONArray(Predictors_List);
			for(int i =0; i<predictors_array.length(); i++){
				predictor_types.add(predictors_array.getInt(i));
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
		return predictor_types;
	}

	public void givePreddictionModelReadAccessToFriend(String friend__osn_id){
		try {			
			JSONArray friends_array = privacy_data.getJSONArray(Friends_List);
			friends_array.put(friend__osn_id);
			privacy_data.remove(Friends_List);
			privacy_data.put(Friends_List, friends_array);
			updateFile();
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
	}

	public void givePredictionModelReadAccessToFriends(Set<String> friend__osn_ids){
		try {			
			JSONArray friends_array = privacy_data.getJSONArray(Friends_List);
			for(String id: friend__osn_ids){
				friends_array.put(id);
			}
			privacy_data.remove(Friends_List);
			privacy_data.put(Friends_List, friends_array);
			updateFile();
		} catch (JSONException e) {
			try {
				privacy_data.put(Friends_List, new JSONArray());
				updateFile();
			} catch (JSONException e1) {
				Log.e(TAG, e1.toString());
			}
		}
	}

	public Set<String> getAccessGrantedFriends(){
		Set<String> friend_osn_ids = new HashSet<String>();
		try {			
			JSONArray friends_array = privacy_data.getJSONArray(Friends_List);
			for(int i =0; i<friends_array.length(); i++){
				friend_osn_ids.add(friends_array.getString(i));
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
		return friend_osn_ids;
	}

	public boolean hasReadAccess(String friend_id, int predictor_type){
		Boolean friend_result = false;
		Boolean predictor_result = false;
		try {			
			JSONArray friends_array = privacy_data.getJSONArray(Friends_List);
			JSONArray predictor_array = privacy_data.getJSONArray(Predictors_List);
			for(int i =0; i<friends_array.length(); i++){
				if(friend_id.equalsIgnoreCase(friends_array.getString(i)))
					friend_result = true;
			}
			for(int i =0; i<predictor_array.length(); i++){
				if(predictor_type == predictor_array.getInt(i))
					predictor_result = true;
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
		return friend_result && predictor_result;
	}



	private boolean startMqttService(){
		//check for all  req before starting
		ServerSettings ss = new ServerSettings(this.app_context);
		int server_port = ss.getInt(ss.SERVER_PORT);
		String server_ip = ss.getString(ss.SERVER_IP);
		String mqtt_broker_url = ss.getString(ss.MQTT_BROKER_URL);
		String mqtt_user_name = ss.getString(ss.MQTT_USER_NAME);
		String mqtt_user_password = ss.getString(ss.MQTT_USER_PASSWORD);
		if(server_port == 0 || server_ip.equals("") || mqtt_broker_url.equalsIgnoreCase("")
				|| mqtt_user_name.equals("") || mqtt_user_password.equals("")){
			return false;
		}

		SharedPreferences sp = new SharedPreferences(this.app_context);
		PrivacyManager pm = new PrivacyManager(this.app_context);

		if((!sp.getBoolean(sp.IS_MQTT_SERVICE_STARTED)) && pm.isDataTransmissionToServerEnabled()){
			this.app_context.startService(new Intent(this.app_context, MQTTService.class));
			sp.add(sp.IS_USER_REGISTERED, true);
			return true;
		}
		return false;
	}

	//	public void enableGroupPredictions(boolean value){
	//	try {
	//		privacy_data.put(Enable_Group_Predictions, value);
	//		updateFile();
	//	} catch (JSONException e) {
	//		Log.e(TAG, e.toString());
	//	}
	//}
	//
	//public boolean isGroupPredictionEnabled(){
	//	try {
	//		return privacy_data.getBoolean(Enable_Group_Predictions);
	//	} catch (JSONException e) {
	//		Log.e(TAG, e.toString());
	//	}
	//	return false;
	//}

	//	public void setDataSharingConditions(Filter filter){
	//		try {
	//			privacy_data.put(Data_Sharing_Conditions, filter.getExpressionString());
	//			updateFile();
	//		} catch (JSONException e) {
	//			Log.e(TAG, e.toString());
	//		}
	//	}
	//
	//	public Filter getDataSharingConditions(){
	//		Filter filter = new Filter("");
	//		try {
	//			filter = new Filter(privacy_data.getString(Data_Sharing_Conditions));
	//		} catch (JSONException e) {
	//			Log.e(TAG, e.toString());
	//		}
	//		return filter;
	//	}


}
