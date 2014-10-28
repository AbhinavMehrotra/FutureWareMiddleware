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
package org.ubhave.anticipatorymiddleware.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.AMException;
import org.ubhave.anticipatorymiddleware.AnticipatoryManager;
import org.ubhave.anticipatorymiddleware.alarm.AlarmMgr;
import org.ubhave.anticipatorymiddleware.alarm.AlarmReceiver;
import org.ubhave.anticipatorymiddleware.classifiers.SensorDataClassifier;
import org.ubhave.anticipatorymiddleware.communication.CommunicationManager;
import org.ubhave.anticipatorymiddleware.communication.JSONKeys;
import org.ubhave.anticipatorymiddleware.filter.Filter;
import org.ubhave.anticipatorymiddleware.predictor.Configuration;
import org.ubhave.anticipatorymiddleware.predictor.Predictor;
import org.ubhave.anticipatorymiddleware.predictor.PredictorCollection;
import org.ubhave.anticipatorymiddleware.predictordata.PredictionResult;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.privacy.PrivacyManager;
import org.ubhave.anticipatorymiddleware.sensors.SensorManager;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.storage.DBHelper;
import org.ubhave.anticipatorymiddleware.storage.FileManager;
import org.ubhave.anticipatorymiddleware.storage.FilePathGenerator;
import org.ubhave.anticipatorymiddleware.storage.SQLiteUtils;
import org.ubhave.anticipatorymiddleware.subscribe.Subscription;
import org.ubhave.anticipatorymiddleware.subscribe.SubscriptionList;
import org.ubhave.anticipatorymiddleware.utils.Constants;
import org.ubhave.anticipatorymiddleware.utils.ObjectSerializer;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class SubscriptionService extends IntentService {

	public SubscriptionService() {
		super("SubscriptionService");
	}

	private static final String TAG = "AnticipatoryManager";

	private String predictor_name_or_path;

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Inside SubscriptionService : onHandleIntent()");
		int subscription_id = intent.getExtras().getInt("SUBSCRIPTION_ID");
		String alarm_type = intent.getExtras().getString("ALARM_TYPE");
		long alarm_expiry_time = intent.getExtras()
				.getLong("ALARM_EXPIRY_TIME");
		long trigger_start_time = intent.getExtras().getLong(
				"TRIGGER_START_TIME");
		long trigger_interval_time = intent.getExtras().getLong(
				"TRIGGER_INTERVAL_TIME");
		predictor_name_or_path = intent.getExtras().getString(
				"PREDICTOR_NAME_OR_PATH");
		//		updateModelUpdateTime();
		Log.d(TAG, "alarm_type : " + alarm_type);
		Log.d(TAG, "alarm_expiry_time : " + alarm_expiry_time);
		Log.d(TAG, "trigger_start_time : " + trigger_start_time);
		Log.d(TAG, "trigger_interval_time : " + trigger_interval_time);
		Log.d(TAG, "predictor_name_or_path : " + predictor_name_or_path);

		if (alarm_type == null) 
		{
			Log.e(TAG, "Why is the alarm type is null??");
		} 
		else if (alarm_type.equals(AlarmMgr.ALARM_TYPE_SUBSCRIPTION)) 
		{
			try 
			{
				Subscription subscription = AnticipatoryManager
						.getAnticipatoryManager(getApplicationContext())
						.getSubscriptionById(subscription_id);
				if (subscription == null) // if subscription object is null then read it from file.
				{
					subscription = readSubscriptionFRomFile(subscription_id);
					if (subscription == null) { 
						Log.e(TAG, "Subscription: " + subscription
								+ " is even null in the suscription-list file.");
						return;
					}
				}
				// validate the filter result
				boolean stream_filer_result = validateFilter(subscription,
						intent, predictor_name_or_path);
				// if filter is satisfied
				if (stream_filer_result) {
					Set<PredictorData> required_predictor_data = makePrediction(subscription, predictor_name_or_path);
					Log.i(TAG, "Predicted data: " + required_predictor_data);
					if (isTriggerRequired(required_predictor_data)) 
					{
						Log.i(TAG, "Sending a trigger");
						// notify the registered listener
						AnticipatoryManager.getAnticipatoryManager(
								getApplicationContext()).notify(
										subscription_id, required_predictor_data);
						// reset alarm
						AlarmMgr alarm_mgr = new AlarmMgr(
								getApplicationContext());
						if (alarm_expiry_time == 0
								|| (alarm_expiry_time != -1 && alarm_expiry_time <= Calendar
								.getInstance().getTimeInMillis()
								/ (1000 * 60))) {
							// alarm expired
							Log.e(TAG,
									"Removing alarm because the subscription expired: "
											+ subscription_id);
							alarm_mgr.removeAlarm(subscription_id);
						} else {
							alarm_mgr.resetAlarm(
									AlarmMgr.ALARM_TYPE_SUBSCRIPTION,
									subscription_id, trigger_start_time,
									trigger_interval_time, alarm_expiry_time,
									predictor_name_or_path);
						}
					} else {
						Log.i(TAG, "Trigger not required");
					}
				}
			} catch (AMException e) {
				Log.e(TAG,
						"Inside SubscriptionService : Null context passed to getSubscription() \n"
								+ e.toString());
			}
		} else {
			Log.e(TAG, "Alarm type not recognised!! " + alarm_type);
		}
		// Release the wake lock provided by the BroadcastReceiver.
		AlarmReceiver.completeWakefulIntent(intent);
		Log.d(TAG, "Wakeful Intent released for intent of alarm: " + alarm_type);
		return;
	}

	private Subscription readSubscriptionFRomFile(int subscription_id)
			throws AMException {
		Log.e(TAG, "Subscription object is null.");
		Log.e(TAG, "Subscription id: " + subscription_id);
		Log.e(TAG,
				"Subscription list: "
						+ AnticipatoryManager.getAnticipatoryManager(
								getApplicationContext()).getAllSubscriptions());

		Log.e(TAG, "Reading SubscriptionList object from file.");
		FileManager file_manager = new FileManager();
		List<Subscription> subscription_list = file_manager
				.getSubscriptionListFromFile(getApplicationContext());
		Log.e(TAG, "Subscription list: " + subscription_list);
		SubscriptionList subscriptionList = new SubscriptionList();
		SubscriptionList newSubscriptionList = subscriptionList
				.createSubscriptionList(subscription_list);
		Subscription subscription = newSubscriptionList
				.getSubscription(subscription_id);
		Log.e(TAG, "Subscription: " + subscription);
		return subscription;
	}

	private boolean validateFilter(Subscription subscription, Intent intent,
			String predictor_name_or_path) {
		Filter filter = subscription.getFilter();
		if (filter == null) {
			return true;
		}

		Boolean stream_filer_result = true;
		Predictor predictor;
		JSONObject model;
		long notification_period = subscription.getConfiguration().getLong(
				Configuration.Notification_Preiod);

		Set<PredictorData> predictor_data = new HashSet<PredictorData>();
		Set<Integer> predictor_ids = new HashSet<Integer>();

		// check if all predictors are ready
		predictor_ids = filter.getRequiredPredictors();
		for (PredictorData pd : subscription.getRequiredData()) {
			predictor_ids.add(pd.getPredictorType());
		}

		// if any predictor is not ready then don't make any prediction for this
		// subscription
		for (int predictor_id : predictor_ids) {
			if (!new Predictor(getApplicationContext(), predictor_id,
					predictor_name_or_path).isReady(subscription.getId())) {
				Log.d(TAG, "Predictor " + predictor_id + " is not ready!");
				AlarmReceiver.completeWakefulIntent(intent);
				return false;
			}
		}

		predictor_ids.clear();
		predictor_ids = new HashSet<Integer>();
		predictor_ids = filter.getRequiredPredictors();
		for (int predictor_id : predictor_ids) {
			// only for social prediction
			if (predictor_id == Constants.PREDICTOR_TYPE_SOCIAL) {
				try {
					FilePathGenerator fpg = new FilePathGenerator();
					String fileLocation = fpg
							.generateGroupPredictionResultDataFilePath(subscription
									.getId());
					FileManager fm = new FileManager();
					JSONObject jsonObject = fm.readJSONObject(fileLocation);
					PredictorData group_prediction_data = (PredictorData) ObjectSerializer
							.fromString(jsonObject
									.getString(JSONKeys.GROUP_PREDICTION_DATA));
					predictor_data.add(group_prediction_data);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				continue;
			}

			// get predictor model
			try {
				predictor = new Predictor(getApplicationContext(),
						predictor_id, predictor_name_or_path);
				ArrayList<Integer> predictor_ids_of_required_data = predictor
						.idsOfRequiredData(predictor_id);
				ArrayList<JSONObject> models = new ArrayList<JSONObject>();
				for (int p_id : predictor_ids_of_required_data) {
					model = getModel(p_id, predictor_name_or_path);
					models.add(model);
				}
				SharedPreferences sp = new SharedPreferences(
						getApplicationContext());
				String current_state = sp.getString(sp
						.LATEST_CLASSIFIED_SENSOR_DATA(Constants.getSensor(predictor_id)));
				predictor_data.add(predictor.predictionRequest(models,
						current_state, notification_period));
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}

		stream_filer_result = filter.validateFilter(predictor_data); // TODO:check
		// it
		// for
		// social
		// predictor
		return stream_filer_result;
	}

	private Set<PredictorData> makePrediction(Subscription subscription,
			String predictor_name_or_path) {
		long notification_period = subscription.getConfiguration().getLong(
				Configuration.Notification_Preiod);
		// collect data
		Set<PredictorData> required_predictor_data = new HashSet<PredictorData>();
		ArrayList<Integer> required_predictors = subscription.getPredictorListForRequiredData();
		for (int rp : required_predictors) 
		{
			// only for social prediction
			if (rp == Constants.PREDICTOR_TYPE_SOCIAL) 
			{
				try {
					FilePathGenerator fpg = new FilePathGenerator();
					String fileLocation = fpg
							.generateGroupPredictionResultDataFilePath(subscription
									.getId());
					FileManager fm = new FileManager();
					JSONObject jsonObject = fm.readJSONObject(fileLocation);
					PredictorData group_prediction_data = (PredictorData) ObjectSerializer
							.fromString(jsonObject
									.getString(JSONKeys.GROUP_PREDICTION_DATA));
					required_predictor_data.add(group_prediction_data);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} 
				continue;
			}

			try 
			{
				Predictor predictor = new Predictor(getApplicationContext(),
						rp, predictor_name_or_path);
				ArrayList<Integer> predictor_ids_of_required_data = predictor
						.idsOfRequiredData(rp);
				ArrayList<JSONObject> models = new ArrayList<JSONObject>();
				Log.i(TAG, "Required model for predictor: " + rp + ", are: "+ predictor_ids_of_required_data);
				for (int p_id : predictor_ids_of_required_data) 
				{
					JSONObject model = getModel(p_id, predictor_name_or_path);
					models.add(model);
				}
//				Log.i(TAG, "Models: " + models);
				SharedPreferences sp = new SharedPreferences(
						getApplicationContext());
				String current_state = sp.getString(sp
						.LATEST_CLASSIFIED_SENSOR_DATA(Constants.getSensor(rp)));
				Log.i(TAG, "Current state: " + current_state);
				PredictorData pd = predictor.predictionRequest(models,
						current_state, notification_period);
				required_predictor_data.add(pd);
			} catch (JSONException e) {
				Log.e(TAG, "Error inside makePrediction()!! " + e.toString());
			} catch (AMException e) {
				Log.e(TAG, "Error inside makePrediction()!! " + e.toString());
			}
		}
		return required_predictor_data;
	}

	//	private JSONObject getModel(int predictor_id, String predictor_name_or_path)
	//			throws AMException, JSONException {
	//		JSONObject model = new JSONObject();
	//		SQLiteUtils utils = new SQLiteUtils();
	//		String db_name = utils.getDbName();
	//		String model_data_table = utils.getModelDataTableName(Constants.getSensor(predictor_id));
	//		DBHelper db = new DBHelper(getApplicationContext(), db_name);
	//		JSONArray model_data_json_array = db.getRecordsAsJSONArray(model_data_table);
	//		model.put("DATA", model_data_json_array);
	//		return model;
	//	}
	private JSONObject getModel(int predictor_id, String predictor_name_or_path) throws AMException, JSONException
	{
		Log.v(TAG, "Get model request, id: "+ predictor_id + " module: "+ predictor_name_or_path);
		JSONObject model = new JSONObject();
		SharedPreferences sp = new SharedPreferences(getApplicationContext());
		SensorManager sm = new SensorManager(getApplicationContext());
		int context_sampling_rate = sm.getContextSamplingRate();
		int context_life_cycle = sm.getContextLifeCycleTimePeriod();
		int expected_data_size = context_life_cycle / context_sampling_rate;		
		SQLiteUtils utils = new SQLiteUtils();
		PredictorCollection pc = new PredictorCollection();
		String db_name = utils.getDbName();
		String model_data_table = utils.getModelDataTableName(Constants.getSensor(predictor_id), pc.getName(predictor_name_or_path));
		String stacked_data_table = utils.getStackedSensorDatatableName(Constants.getSensor(predictor_id));
		DBHelper db = new DBHelper(getApplicationContext(), db_name);
		
		int stacked_data_size = 0;		
		Cursor stacked_data_cursor = db.selectRecords(stacked_data_table);
		if(stacked_data_cursor != null)
		{
			stacked_data_size = stacked_data_cursor.getCount();
			stacked_data_cursor.close();
		}
		
		int model_data_size = 0;		
		Cursor model_data_cursor = db.selectRecords(model_data_table);
		if(model_data_cursor != null)
		{
			model_data_size = model_data_cursor.getCount();
			model_data_cursor.close();
		}
		
		if(model_data_size == 0)
		{
			createDefaultModel(predictor_id);
			Log.i(TAG,"Default model created.");
			long current_time = Calendar.getInstance().getTimeInMillis();
			sp.add(sp.MODEL_UPDATE_TIME(predictor_id, pc.getName(predictor_name_or_path)), current_time);
		}
		
		long last_update_time = sp.getLong(sp.MODEL_UPDATE_TIME(predictor_id, pc.getName(predictor_name_or_path)));
		long current_time = Calendar.getInstance().getTimeInMillis();
		long time_difference = current_time - last_update_time;
		long expected_time_difference =  context_life_cycle * 60 * 1000;
		Log.v(TAG, "context_life_cycle: "+context_life_cycle);
		if(time_difference > expected_time_difference)		
//		double ratio = stacked_data_size / expected_data_size;
//		double difference = ratio - Math.floor(ratio);
//		Log.i(TAG, "stacked_data_size:" + stacked_data_size);
//		Log.i(TAG, "expected_data_size:" + expected_data_size);
//		Log.i(TAG, "ratio:" + ratio);
//		Log.i(TAG, "difference:" + difference);
//		if(stacked_data_size > 0 && stacked_data_size >= expected_data_size && difference < 0.005 && !Double.isInfinite(ratio))
		{
			sp.add(sp.MODEL_UPDATE_TIME(predictor_id, pc.getName(predictor_name_or_path)), current_time);
			Log.d(TAG,"Size of the stack is exact multiple of expected_size!!");
			updateModel(predictor_id);
			JSONArray model_data_json_array = db.getRecordsAsJSONArray(model_data_table);
			model.put("DATA", model_data_json_array);	
			// Check if the model is required on the server if true then send it to the server
			PrivacyManager privacy_manager = new PrivacyManager(getApplicationContext());
			if(privacy_manager.isDataTransmissionToServerEnabled())
			{
				CommunicationManager communication_manager = new CommunicationManager(getApplicationContext());
				String user_id = sp.getString(sp.USER_ID);
				communication_manager.transmitPredictionModelData(user_id, predictor_id, model);	
			}										
		}
		else
		{
			Log.d(TAG,"Size of the stack is not an exact multiple of expected_size!!");
			JSONArray model_data_json_array = db.getRecordsAsJSONArray(model_data_table);
			model.put("DATA", model_data_json_array);						
		}
		return model;
	}


	private boolean isTriggerRequired(Set<PredictorData> required_predictor_data) {
		if (required_predictor_data == null
				|| required_predictor_data.size() == 0) {
			return false;
		}
		for (PredictorData pd : required_predictor_data) {
			if(pd != null){
				ArrayList<PredictionResult> result = pd.getResult();
				if (result.size() != 0) {
					return true;
				}
			}
		}
		return false;
	}


	private void createDefaultModel(int predictor_id)
	{
		try
		{
			SensorDataClassifier classifier = new SensorDataClassifier(getApplicationContext(), Constants.getSensor(predictor_id));
			ArrayList<String> states = classifier.getAllLabels();
			Predictor p = new Predictor(getApplicationContext(),
					predictor_id, predictor_name_or_path);
			JSONObject model = p.generateDataModel(null, states);
			JSONArray model_data_json_array = model.getJSONArray("DATA");

			SQLiteUtils utils = new SQLiteUtils();
			PredictorCollection pc = new PredictorCollection();
			String db_name = utils.getDbName();
			String model_data_table = utils.getModelDataTableName(Constants.getSensor(predictor_id), pc.getName(predictor_name_or_path));
			DBHelper db = new DBHelper(getApplicationContext(), db_name);
			db.deleteTable(model_data_table);
			for(int i=0; i<model_data_json_array.length(); i++)
			{
				JSONArray json_array = model_data_json_array.getJSONArray(i);
				db.addRecord(model_data_table, json_array.toString());
			}	
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.toString());
		}
	}


	private void updateModel(int predictor_id) throws JSONException
	{
		Log.i(TAG, "Updating model");
		SQLiteUtils utils = new SQLiteUtils();
		PredictorCollection pc = new PredictorCollection();
		String db_name = utils.getDbName();
		String model_data_table = utils.getModelDataTableName(Constants.getSensor(predictor_id), pc.getName(predictor_name_or_path));
		String stacked_data_table = utils.getStackedSensorDatatableName(Constants.getSensor(predictor_id));
		DBHelper db = new DBHelper(getApplicationContext(), db_name);

		JSONArray stacked_data_json_array = db.getRecordsAsJSONArray(stacked_data_table);


		SensorDataClassifier classifier = new SensorDataClassifier(getApplicationContext(), Constants.getSensor(predictor_id));
		ArrayList<String> labels = classifier.getAllLabels();
		Log.i(TAG, "Labels: "+labels);
		Predictor p = new Predictor(getApplicationContext(), predictor_id, predictor_name_or_path);


		JSONObject stacked_data = new JSONObject();
		stacked_data.put("DATA", stacked_data_json_array);
		Log.i(TAG, "Stacked data: "+stacked_data);
		JSONObject model = p.generateDataModel(stacked_data, labels);
		Log.i(TAG, "New model: "+model);
		JSONArray model_data_json_array = model.getJSONArray("DATA");

		db.deleteTable(model_data_table);
		for(int i=0; i<model_data_json_array.length(); i++)
		{
			JSONArray json_array = model_data_json_array.getJSONArray(i);
			db.addRecord(model_data_table, json_array.toString());
		}

		/**
		 * 1. Check if the model is required on the server 
		 * 2. if true then send it to the server
		 */
		SharedPreferences sp = new SharedPreferences(getApplicationContext());
		PrivacyManager privacy_manager = new PrivacyManager(
				getApplicationContext());
		if (privacy_manager.isDataTransmissionToServerEnabled()) {
			CommunicationManager communication_manager = new CommunicationManager(
					getApplicationContext());
			String user_id = sp.getString(sp.USER_ID);
			communication_manager.transmitPredictionModelData(user_id,
					predictor_id, model);
		}
	}




	//	private void updateModelUpdateTime(){
	//		SensorManager sm = new SensorManager(getApplicationContext());
	//		int context_life_cycle = sm.getContextLifeCycleTimePeriod();
	//		SharedPreferences sp = new SharedPreferences(getApplicationContext());
	//		long update_time =  sp.getLong(sp.MODEL_UPDATE_TIME);
	//		long current_time = Time.getCurrentTimeInMilliseconds();
	//		long context_life_cycle_in_miliseconds = context_life_cycle * 60 * 1000;
	//		Log.i(TAG, "update_time:"+update_time);
	//		Log.i(TAG, "current_time:"+current_time);
	//		Log.i(TAG, "context_life_cycle_in_miliseconds:"+context_life_cycle_in_miliseconds);
	//		if(update_time == 0){
	//			Log.i(TAG, "update_time is 0. Creating default model.");
	//			//create default model for first run
	//			for(int sensor_id : sm.getActiveSensorList()){
	//				createDefaultModel(Constants.getPredictor(sensor_id));				
	//			}			
	//
	//			//set new update time
	//			Log.i(TAG, "New update_time:"+ (current_time + context_life_cycle_in_miliseconds));
	//			sp.add(sp.MODEL_UPDATE_TIME, current_time + context_life_cycle_in_miliseconds);
	//		}
	//		else if(update_time - current_time < 0){
	//			Log.i(TAG, "New model required!");
	//			//reset the MODEL_UPDATE_TIME
	//			Log.i(TAG, "New update_time:"+ (current_time + context_life_cycle_in_miliseconds));
	//			sp.add(sp.MODEL_UPDATE_TIME, current_time + context_life_cycle_in_miliseconds);
	//
	//			//update all models
	//			for(int sensor_id : sm.getActiveSensorList())
	//			{
	//				try 
	//				{
	//					updateModel(Constants.getPredictor(sensor_id));
	//				} catch (JSONException e) {
	//					Log.e(TAG, e.toString());
	//				}				
	//			}			
	//		}
	//		else{
	//			Log.i(TAG, "update_time - current_time :"+ (update_time - current_time));			
	//		}
	//
	//	}

}
