package org.ubhave.anticipatorymiddleware.sharedpreferences;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class SharedPreferences implements Serializable{
	

	private static final long serialVersionUID = 7640032638767648839L;
	private final transient Context app_context;
	private final static String SHARED_PREFERENCE_NAME = "FUTUREWARE_MIDDLEWARE_V9";
	
	public SharedPreferences(Context app_context){
		this.app_context = app_context;
	}

	public final String MODEL_UPDATE_TIME(int predictor_id, String predictor_name)
	{
		return "MODEL_UPDATE_TIME_"+predictor_id+"_"+predictor_name; 
	}
	public final String CONTEXT_SAMPLING_RATE = "CONTEXT_SAMPLING_RATE"; 
	public final String CONTEXT_LIFE_CYCLE_TIME_PERIOD = "CONTEXT_LIFE_CYCLE_TIME_PERIOD"; 
	public final String OSN_ACCOUNTS = "OSN_ACCOUNTS"; 
	public final String SERVER_PORT = "SERVER_PORT"; 
	public final String SERVER_IP = "SERVER_IP"; 
	public final String MQTT_BROKER_URL = "MQTT_BROKER_URL"; 
	public final String MQTT_USER_NAME = "MQTT_USER_NAME"; 
	public final String MQTT_USER_PASSWORD = "MQTT_USER_PASSWORD"; 
	public final String USER_ID = "USER_ID"; 
	public final String APPS_IGNORED = "APPS_IGNORED"; 
	public final String PREVIOUS_APP_STACK = "PREVIOUS_APP_STACK"; 
	public final String PREVIOUS_CALL_LIST = "PREVIOUS_CALL_LIST"; 
	public final String PREVIOUS_SMS_LIST = "PREVIOUS_SMS_LIST"; 
	public final String IS_USER_REGISTERED = "IS_USER_REGISTERED"; 
	public final String IS_MQTT_SERVICE_STARTED = "IS_MQTT_SERVICE_STARTED"; 
	public final String IS_SERVER_UPDATE_REQUIRED = "IS_SERVER_UPDATE_REQUIRED"; 	
	public final String ACTIVE_SENSORS = "ACTIVE_SENSORS"; 	
	public final String LABELLED_SENSOR_DATA(int sensor_type){
		return "IS_"+ Constants.getSensorName(sensor_type) + "_DATA_LABELLED"; 
	}
 	
	public final String SOCIAL_PREDICTOR_STATUS(int subscription_id){
		return "SOCIAL_PREDICTOR_STATUS_"+ subscription_id; 
	}
	
	public final String SAMPLING_STARTED_FOR_SENSORS = "SAMPLING_STARTED_FOR_SENSORS";	

	public final String LATEST_CLASSIFIED_SENSOR_DATA(int sensor_type){
		return "LATEST_CLASSIFIED_DATA_FOR_"+ Constants.getSensorName(sensor_type); 
	}

	public final String LATEST_RAW_SENSOR_DATA(int sensor_type){
		return "LATEST_RAW_DATA_FOR_"+ Constants.getSensorName(sensor_type); 
	}
		
	public void add(String key, String value){
		if(value == null) value = "";
		android.content.SharedPreferences sp = app_context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putString(key, value);
		ed.commit();
	}

	public void add(String key, int value){
		android.content.SharedPreferences sp = app_context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putInt(key, value);
		ed.commit();
	}

	public void add(String key, long value){
		android.content.SharedPreferences sp = app_context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putLong(key, value);
		ed.commit();
	}
	
	public void add(String key, Boolean value){
		android.content.SharedPreferences sp = app_context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putBoolean(key, value);
		ed.commit();
	}

	public void addStringSet(String key, Set<String> value){
		android.content.SharedPreferences sp = app_context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putStringSet(key, value);
		ed.commit();
	}
	
	public String getString(String key){
		android.content.SharedPreferences sp = app_context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}

	
	public int getInt(String key){
		android.content.SharedPreferences sp = app_context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		return sp.getInt(key, 0);
	}

	
	public long getLong(String key){
		android.content.SharedPreferences sp = app_context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		return sp.getLong(key, (long) 0);
	}
	
	public Boolean getBoolean(String key){
		android.content.SharedPreferences sp = app_context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}


	public Set<String> getStringSet(String key){
		android.content.SharedPreferences sp = app_context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		return sp.getStringSet(key, new HashSet<String>());
	}
	

	public String osnMapToJSONString(Map<String,String> map) throws JSONException{
		JSONArray json_array  = new JSONArray();
		JSONObject json;
		for(Map.Entry<String,String> entry : map.entrySet()){
			json = new JSONObject();
			json.put(entry.getKey(), entry.getValue());
			json_array.put(json);
		}
		json = new JSONObject();
		json.put("DATA", json_array);
		return json.toString();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String> osnJSONStringToMap(String json_string) throws JSONException{
		Map<String,String> map = new HashMap<String, String>();
		JSONObject json = new JSONObject(json_string);
		JSONArray json_array = json.getJSONArray("DATA");
		for(int i=0; i<json_array.length(); i++){
			JSONObject entry = json_array.getJSONObject(i);
			Iterator<String> it = entry.keys();
			String key = it.next();
			map.put(key, entry.getString(key));
		}
		return map;
	}
}
