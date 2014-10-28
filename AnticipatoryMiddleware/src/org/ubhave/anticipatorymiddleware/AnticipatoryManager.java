/*******************************************************************************
 *
 * FutureWare Middleware
 *
 * Copyright (c) ${2014}, University of Birmingham
 * Abhinav Mehrotra, a.mehrotra@cs.bham.ac.uk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Birmingham 
 *       nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE ABOVE COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *******************************************************************************/
package org.ubhave.anticipatorymiddleware;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.classifiers.SensorDataClassifier;
import org.ubhave.anticipatorymiddleware.communication.CommunicationManager;
import org.ubhave.anticipatorymiddleware.event.EventManager;
import org.ubhave.anticipatorymiddleware.filter.Filter;
import org.ubhave.anticipatorymiddleware.predictor.Configuration;
import org.ubhave.anticipatorymiddleware.predictor.Predictor;
import org.ubhave.anticipatorymiddleware.predictor.PredictorCollection;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.privacy.PrivacyManager;
import org.ubhave.anticipatorymiddleware.sensors.SensorManager;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.storage.DBHelper;
import org.ubhave.anticipatorymiddleware.storage.FileManager;
import org.ubhave.anticipatorymiddleware.storage.SQLiteUtils;
import org.ubhave.anticipatorymiddleware.subscribe.Subscription;
import org.ubhave.anticipatorymiddleware.subscribe.SubscriptionList;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.database.Cursor;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AnticipatoryManager implements Serializable{

	/**
	 * Serial version UID for {@link AnticipatoryManager}
	 */
	private static final long serialVersionUID = 1L;

	private static final String TAG = "AnticipatoryManager";

	private static AnticipatoryManager anticipatoryManager;

	private static Object lock = new Object();

	private final SubscriptionList subscriptionList;

	private final EventManager event_manager;

	private final Context appContext;

	private final String user_id;

	private final SharedPreferences sp; 

	public static AnticipatoryManager getAnticipatoryManager(Context appContext) throws AMException {
		if (appContext == null) {
			throw new AMException(
					AMException.INVALID_PARAMETER,
					" Invalid parameter, context object passed is null");
		}
		if (anticipatoryManager == null) {
			synchronized (lock) {
				if (anticipatoryManager == null) {
					//check in file before creating anew object (required or not??)

					anticipatoryManager = new AnticipatoryManager(appContext.getApplicationContext());
				}
			}
		}
		return anticipatoryManager;
	}

	private AnticipatoryManager(Context appContext) {
		this.appContext = appContext.getApplicationContext();
		this.sp = new SharedPreferences(appContext);
		this.user_id = gerenrateUserId();
		sp.add(sp.USER_ID, user_id);
		sp.add(sp.IS_USER_REGISTERED, false);

		FileManager file_manager = new FileManager();
		List<Subscription> subscription_list = file_manager.getSubscriptionListFromFile(appContext);
		if(subscription_list == null)
		{
			this.subscriptionList = new SubscriptionList();
		}
		else
		{
			SubscriptionList tempSubscriptionList = new SubscriptionList();
			SubscriptionList tempSubscriptionList2 = new SubscriptionList();
			tempSubscriptionList2 = tempSubscriptionList.createSubscriptionList(subscription_list);
			if(tempSubscriptionList2 == null)
			{
				this.subscriptionList = new SubscriptionList();
			}
			else
			{
				this.subscriptionList = tempSubscriptionList2;
			}
		}
		
		this.event_manager = new EventManager(appContext, subscriptionList);
		SensorManager sm = new SensorManager(appContext);
		if(sm.getContextSamplingRate() == 0)
			sm.setContextSamplingRate(sm.DEFAULT_CONTEXT_SAMPLING_RATE);
		if(sm.getContextLifeCycleTimePeriod() == 0)
			sm.setContextLifeCycleTimePeriod(sm.DEFAULT_CONTEXT_LIFE_CYCLE_TIME_PERIOD);
		//		AlarmMgr alarm =  new AlarmMgr(appContext);
		//		alarm.setNewAlarm(AlarmMgr.ALARM_TYPE_APP_KEEP_ALIVE, -3, 0, 10, AlarmMgr.ALARM_EXPIRY_CONTINUOUS, null);
		registerUser();
	}



	private void registerUser(){
		SharedPreferences sp = new SharedPreferences(this.appContext);
		PrivacyManager pm = new PrivacyManager(this.appContext);
		if((!sp.getBoolean(sp.IS_USER_REGISTERED)) && pm.isDataTransmissionToServerEnabled()){
			CommunicationManager cm = new CommunicationManager(this.appContext);
			String user_id = sp.getString(sp.USER_ID);		
			JSONObject privacy_policy = pm.getPrivacyData();
			Set<String> user_osn_ids = pm.getMyOSNIds();
			SensorManager sm = new SensorManager(this.appContext);
			int context_sampling_rate = sm.getContextSamplingRate();
			int context_life_cycle_period = sm.getContextLifeCycleTimePeriod();
			cm.transmitUserRegistrationData(user_id, privacy_policy, user_osn_ids, context_sampling_rate, context_life_cycle_period);
			sp.add(sp.IS_USER_REGISTERED, true);
		}
	}

	private String gerenrateUserId(){
		String androidId = Secure.getString(appContext.getContentResolver(), Secure.ANDROID_ID);
		if ("9774d56d682e549c".equals(androidId)) {
			TelephonyManager tm = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
			androidId = tm.getDeviceId();
			if(androidId == null){
				androidId = UUID.randomUUID().toString();  
				androidId = androidId.replace("-", "");              		
			}
		}
		return "USER_"+androidId;
	}

	public synchronized int subscribe(List<PredictorData> required_data, AnticipatoryListener listener, Filter filter, Configuration configuration) throws AMException {
		if (listener == null) 
			throw new AMException(AMException.NULL_POINTER, "PredictionListener object is null");
		Log.d(TAG, "subscribe() - subscribing listener to event manager!!");
		Subscription subscription = new Subscription(required_data, listener, filter, configuration);
		int subscriptionId = this.subscriptionList.registerSubscription(subscription);
		FileManager file_manager = new FileManager();
		file_manager.addSubscriptionListToFile(subscriptionList.getAllSubscriptions(), appContext);
		event_manager.subscriptionRegistered(subscriptionId);
		return subscriptionId;

	}

	public synchronized int subscribeOnce(List<PredictorData> required_data, AnticipatoryListener listener, Filter filter, Configuration configuration) throws AMException {
		if (listener == null) 
			throw new AMException(AMException.NULL_POINTER, "PredictionListener object is null");
		Log.d(TAG, "subscribe() - subscribing listener to event manager!!");
		//set lease period equal to one prediction cycle
		configuration.put(Configuration.Subscription_Lease_Preiod, (long)0);
		Subscription subscription = new Subscription(required_data, listener, filter, configuration);
		int subscriptionId = this.subscriptionList.registerSubscription(subscription);
		FileManager file_manager = new FileManager();
		file_manager.addSubscriptionListToFile(subscriptionList.getAllSubscriptions(), appContext);
		event_manager.subscriptionRegistered(subscriptionId);
		return subscriptionId;
	}

	public synchronized void unsubscribe(int subscription_id) throws AMException {
		event_manager.subscriptionExpired(subscription_id);
		this.subscriptionList.removeSubscription(subscription_id);
	}

	public void notify(int subscription_id, Set<PredictorData> data)
	{
		try 
		{
			Subscription subscription;
			if(subscriptionList == null)
			{
				subscription = readSubscriptionFRomFile(subscription_id);
			}
			else
			{
				subscription = subscriptionList.getSubscription(subscription_id);
				if(subscription == null)
				{
					subscription = readSubscriptionFRomFile(subscription_id);
				}
			}
			if(subscription != null)
			{
				subscription.getListener().onNewEvent(this.appContext, subscription.getId(), data);
			}
		} 
		catch (ClassNotFoundException e) 
		{
			Log.e(TAG, e.toString());
		} 
		catch (InstantiationException e) 
		{
			Log.e(TAG, e.toString());
		} 
		catch (IllegalAccessException e) 
		{
			Log.e(TAG, e.toString());
		}
	}

	private Subscription readSubscriptionFRomFile(int subscription_id) 
	{
		FileManager file_manager = new FileManager();
		List<Subscription> subscription_list = file_manager
				.getSubscriptionListFromFile(appContext);
		SubscriptionList tempSubscriptionList = new SubscriptionList();
		SubscriptionList newSubscriptionList = tempSubscriptionList
				.createSubscriptionList(subscription_list);
		Subscription subscription = newSubscriptionList
				.getSubscription(subscription_id);
		return subscription;
	}

	public Subscription getSubscriptionById(int subscription_id){
		return subscriptionList.getSubscription(subscription_id);
	}

	public List<Subscription> getAllSubscriptions(){
		return subscriptionList.getAllSubscriptions();
	}

	/**
	 * This method allows developers to set their own data labels. 
	 * Note: it delete the previous labels.
	 * @param sensor_type
	 * @param label_data (JSONArray) Example: [{"SENSOR_DATA" = [52.01, -1.92], "LABEL" = "WORK"}, 
	 * {"SENSOR_DATA" = [51.91, -1.93], "LABEL" = "HOME"}, ...]
	 * @throws JSONException Throws exception if the {@link JSONArray} is not in the the right format. JSONArray should hold {@link JSONObject}s.
	 */
	public void setLabelledData(int sensor_type, JSONArray label_data) throws JSONException{
		Log.i(TAG, "Adding predifined label data: "+label_data);
		SQLiteUtils utils = new SQLiteUtils();
		String db_name = utils.getDbName();
		String label_data_table = utils.getLabelDataTableName(sensor_type);
		DBHelper db = new DBHelper(this.appContext, db_name);

		db.deleteTable(label_data_table);
		for(int i=0; i<label_data.length(); i++)
		{
			JSONObject label_json_object = label_data.getJSONObject(i);
			db.addRecord(label_data_table, label_json_object.toString());
		}
		Log.i(TAG, "Predifined label data size: "+label_data.length());
		Cursor c = db.selectRecords(label_data_table);
		if(c != null)
		{
			Log.i(TAG, "Label data size in DB: "+ c.getCount());
			c.close();
		}	
		else
		{
			Log.i(TAG, "Label data size in DB: 0");			
		}
		Log.i(TAG, "Added predifined label data");
		sp.add(sp.LABELLED_SENSOR_DATA(sensor_type), true);
		SensorDataClassifier sdc = new SensorDataClassifier(appContext, sensor_type);
		Log.i(TAG, "Labels: "+sdc.getAllLabels());		
	}


	/**
	 * Returns a JSONArray of labelled data. Each element is a JSONObject that contains: STATE and SENSOR_DATA. 
	 * @param sensor_type
	 * @return
	 * @throws JSONException
	 */
	public JSONArray getLabelledData(int sensor_type) throws JSONException{
		SQLiteUtils utils = new SQLiteUtils();
		String db_name = utils.getDbName();
		String label_data_table = utils.getLabelDataTableName(sensor_type);
		DBHelper db = new DBHelper(this.appContext, db_name);
		JSONArray labels_json_array = new JSONArray();
		String[] labels_array = db.getRecords(label_data_table);
		for(String label_string : labels_array)
		{
			JSONObject label_json = new JSONObject(label_string);
			labels_json_array.put(label_json);
		}
		return labels_json_array;
	}


	/**
	 * This method allows developers to set their own training data for the start.
	 * @param sensor_type
	 * @param stacked_data_json_array
	 * Example: [{"SATE" = "WORK", "TIME" = "1340"}, {"SATE" = "HOME", "TIME" = "2230"}, ....]
	 * @param predictor_name_or_path
	 * @throws AMException
	 * @throws JSONException
	 */

	public void setPredictorTrainingDataFile(int sensor_type, JSONArray stacked_data_json_array, ArrayList<String> predictor_name_or_paths) throws AMException, JSONException{
		SQLiteUtils utils = new SQLiteUtils();
		String db_name = utils.getDbName();
		String stacked_data_table = utils.getStackedSensorDatatableName(sensor_type);
		DBHelper db = new DBHelper(this.appContext, db_name);		
		db.deleteTable(stacked_data_table);
		for(int i=0; i<stacked_data_json_array.length(); i++)
		{
			JSONObject stacked_json_object = stacked_data_json_array.getJSONObject(i);
			db.addRecord(stacked_data_table, stacked_json_object.toString());
		}
		Log.i(TAG, "Added predifined stacked data");
		Log.i(TAG, "Stacked data: " + stacked_data_json_array);

		JSONObject stacked_dat_json_object = new JSONObject();
		stacked_dat_json_object.put("DATA", stacked_data_json_array);
		
		PredictorCollection pc = new PredictorCollection();
		SensorDataClassifier sdc = new SensorDataClassifier(this.appContext, sensor_type);
		
		for(String predictor : predictor_name_or_paths)
		{
			String model_data_table = utils.getModelDataTableName(sensor_type, pc.getName(predictor));
			Predictor p = new Predictor(this.appContext, Constants.getPredictor(sensor_type), predictor);

			JSONObject model = p.generateDataModel(stacked_dat_json_object, sdc.getAllLabels());
			JSONArray model_data_json_array = model.getJSONArray("DATA");
			Log.i(TAG, "Generated new model for the predifined stacked data");
			Log.i(TAG, "Model: " + model_data_json_array);
			db.deleteTable(model_data_table);
			for(int i=0; i<model_data_json_array.length(); i++)
			{
				JSONArray model_json_array = model_data_json_array.getJSONArray(i);
				db.addRecord(model_data_table, model_json_array.toString());
			}

			long current_time = Calendar.getInstance().getTimeInMillis();
			sp.add(sp.MODEL_UPDATE_TIME(Constants.getPredictor(sensor_type), predictor), current_time);
		}
	}


	public String getLatestClassifiedSensorData(int sensor)
	{
		return sp.getString(sp.LATEST_CLASSIFIED_SENSOR_DATA(sensor));
	}

	/*
	 * Only works for location
	 */
	public JSONObject getLatestRawSensorData(int sensor)
	{
		try 
		{
			return new JSONObject(sp.getString(sp.LATEST_RAW_SENSOR_DATA(sensor)));
		} 
		catch (JSONException e) 
		{
			return new JSONObject();
		}
	}



}
