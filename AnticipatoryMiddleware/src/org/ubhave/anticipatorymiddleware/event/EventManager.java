package org.ubhave.anticipatorymiddleware.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.AMException;
import org.ubhave.anticipatorymiddleware.AnticipatoryManager;
import org.ubhave.anticipatorymiddleware.alarm.AlarmMgr;
import org.ubhave.anticipatorymiddleware.classifiers.SensorDataClassifier;
import org.ubhave.anticipatorymiddleware.communication.CommunicationManager;
import org.ubhave.anticipatorymiddleware.communication.JSONKeys;
import org.ubhave.anticipatorymiddleware.communication.ServerSettings;
import org.ubhave.anticipatorymiddleware.filter.Condition;
import org.ubhave.anticipatorymiddleware.filters.values.ActivityValue;
import org.ubhave.anticipatorymiddleware.filters.values.LocationValue;
import org.ubhave.anticipatorymiddleware.filters.variables.SocialVariable;
import org.ubhave.anticipatorymiddleware.predictor.Configuration;
import org.ubhave.anticipatorymiddleware.predictor.Predictor;
import org.ubhave.anticipatorymiddleware.predictor.PredictorCollection;
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

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class EventManager {

	private static final String TAG = "AnticipatoryManager";

	private final Context app_context;

	private final SubscriptionList subscriptionList;

	private final SharedPreferences sp;


	public EventManager(Context app_context, SubscriptionList subscription_list){
		this.app_context = app_context;
		if(subscription_list == null)
		{
			FileManager file_manager = new FileManager();
			List<Subscription> subscriptions = file_manager.getSubscriptionListFromFile(app_context);
			SubscriptionList tempSubscriptionList = new SubscriptionList();
			subscription_list = tempSubscriptionList.createSubscriptionList(subscriptions);
			if(subscription_list == null)
			{
				subscription_list = new SubscriptionList();
			}
		}
		this.subscriptionList = subscription_list;
		this.sp = new SharedPreferences(app_context);
		
	}


	public void subscriptionRegistered(int subscription_id){
		AlarmMgr alarm_mgr = new AlarmMgr(this.app_context);
		Subscription subscription = subscriptionList.getSubscription(subscription_id);
		if (subscription == null) // if subscription object is null then read it from file.
		{
			try 
			{
				subscription = readSubscriptionFRomFile(subscription_id);
			} 
			catch (AMException e) 
			{

				e.printStackTrace();
			}
		}

		if(subscription != null)
		{
			//create prediction alarm
			Log.d(TAG, "Subscription alarm started for: "+ subscription_id);
			Configuration config = subscription.getConfiguration();
			long pp = config.getLong(Configuration.Prediction_Preiod);
			long np = config.getLong(Configuration.Notification_Preiod);
			long pf = config.getLong(Configuration.Prediction_Frequency);	
			long et = config.getLong(Configuration.Subscription_Lease_Preiod);
			String predictor_name_or_path = config.getString(Configuration.Predictor_Name_Or_Path);

			SensorManager sm = new SensorManager(app_context);
			int csr = sm.getContextSamplingRate();

			alarm_mgr.setNewAlarm(AlarmMgr.ALARM_TYPE_SUBSCRIPTION, subscription_id, ((pp-np)*60)+csr, pf*60, et, predictor_name_or_path);

			
			//check if the subscription uses Social predictor
			boolean is_social_predictor = isSocialPredictorRequired();
			boolean is_server_update_required = sp.getBoolean(sp.IS_SERVER_UPDATE_REQUIRED);
			boolean server_status = serverSetupStatus();
			if(is_server_update_required && is_social_predictor && server_status){
				CommunicationManager cm = new CommunicationManager(app_context);
				String user_id = this.sp.getString(sp.USER_ID);
				Set<String> friend_ids = new HashSet<String>();
				Set<Condition> conditions = subscription.getFilter().getAllConditions();
				int predictor_type = 0;
				String state_to_be_predicted = "";
				for(Condition condition : conditions){
					String condition_variable = condition.getVariable();
					if(condition_variable.startsWith("friend_ids_")){
						friend_ids = SocialVariable.getFriendIdsByString(condition_variable);
						state_to_be_predicted = condition.getValue();
						if(ActivityValue.isValid(state_to_be_predicted)){
							predictor_type = Constants.PREDICTOR_TYPE_ACTIVITY;
						}
						else if(LocationValue.isValid(state_to_be_predicted)){
							predictor_type = Constants.PREDICTOR_TYPE_LOCATION;
						}
					}
				}
				//TODO: check the prediction type (time or state specific)
				cm.transmitGroupPredictionRequest(user_id, subscription_id, friend_ids, predictor_type, state_to_be_predicted);
			}
		}
	}

	private boolean serverSetupStatus(){
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
		return true;
	}

	public void subscriptionExpired(int subscription_id){


		Subscription subscription = subscriptionList.getSubscription(subscription_id);

		if (subscription == null) // if subscription object is null then read it from file.
		{
			try 
			{
				subscription = readSubscriptionFRomFile(subscription_id);
			} 
			catch (AMException e) 
			{

				e.printStackTrace();
			}
		}
		if (subscription != null) 
		{ 
			//remove the alarm
			AlarmMgr alarm_mgr = new AlarmMgr(this.app_context);
			alarm_mgr.removeAlarm(subscription.getId());
		}
	}

	private Boolean isSocialPredictorRequired(){
		Log.d(TAG, "Is social predictor required? Searching...");
		Boolean is_social_predictor = false;
		for(Subscription subscription: subscriptionList.getAllSubscriptions()){
			if(subscription != null && subscription.getFilter() != null){
				is_social_predictor = subscription.getFilter().getRequiredPredictors().contains(Constants.PREDICTOR_TYPE_SOCIAL);
				if(is_social_predictor)
					break;
			}
		}
		Log.d(TAG, "Social predictor required: "+is_social_predictor);
		return is_social_predictor;
	}


	//	public void sampleCurrentContextAndUpdatePredictorModelData(){
	//		ActiveSensorsList active_sensor_list = new ActiveSensorsList(app_context);
	//		Set<Integer> sensors = active_sensor_list.getActiveSensorList();
	//		//Start context sampling and store it in a file
	//		for(int sensor : sensors){
	//			try {
	//				if(sensor == Constants.SENSOR_TYPE_LOCATION){
	//					Log.d(TAG, "Sampling data for "+sensor+" with LocationTracker");				
	//					Location location = new LocationTracker(app_context).getLatestLocation();
	//					LocationData location_data = new LocationData(Time.getCurrentTimeInMilliseconds(), SensorUtils.getDefaultSensorConfig(sensor));
	//					location_data.setLocation(location);
	//					data =  location_data;
	//					Log.i(TAG, "New location by LocationHelper: "+location.getLatitude()+","+location.getLongitude());
	//					onNewSensorData(data);
	//				}
	//				else{
	//					new OneOffSampling(app_context, sensor){
	//						@Override
	//						public void onPostExecute(SensorData data){
	//							Log.d(TAG,"OneOffSensing- onPostExecute()");
	//							onNewSensorData(data);
	//						}
	//					}.execute();
	//				}
	//			} catch (ESException e) {
	//				Log.e(TAG,"Error!! OneOffSensing- onPostExecute() Error: " + e.toString());
	//			}
	//		}
	//	}
	//
	//	private void onNewSensorData(SensorData data){
	//		if(data!=null){
	//			try {
	//				PredictorModelManager predictor_model_manager = new PredictorModelManager(app_context);
	//				//case 1: data not labelled 
	//				if(!sp.getBoolean(sp.LABELLED_SENSOR_DATA(data.getSensorType()))){
	//					//Save training data for labels
	//					Log.d(TAG,"Data labels not present!!");
	//					String file_path = file_path_generator.generateTrainingDataForLabelsFilePath(data.getSensorType());
	//					JSONObject jsonObject = new JSONObject();
	//					JSONFormatter formatter = DataFormatter.getJSONFormatter(app_context, data.getSensorType());
	//					jsonObject.put("SENSOR_DATA", formatter.toJSON(data));
	//					jsonObject.put("TIME", Time.getCurrentTime().toString());
	//					file_manager.appendJSONObjectInJSONArray("DATA", jsonObject, file_path);
	//
	//					//check if the training data size for labelling has reached the mark ( 2 * 24 * 60 / CSR )									
	//					JSONArray array = file_manager.readJSONObject(file_path).getJSONArray("DATA");
	//					int context_sampling_rate = sp.getInt(sp.CONTEXT_SAMPLING_RATE);
	//					int context_life_cycle = sp.getInt(sp.CONTEXT_LIFE_CYCLE_TIME_PERIOD);
	//					//data size equal to 2days data collection
	//					int expected_data_size = (2 * context_life_cycle) / context_sampling_rate;
	//					if(array.length() >= expected_data_size){
	//						Log.d(TAG, "Labelled data ready!!");
	//						SensorDataClassifier classifier = new SensorDataClassifier(data.getSensorType());
	//						classifier.trainClassifier(array);
	//						JSONObject model = predictor_model_manager.createModelFromData(Constants.getPredictor(data.getSensorType()), null);
	//						String model_file_path = file_path_generator.generatePredictorModelDataFilePath(Constants.getPredictor(data.getSensorType()));
	//						file_manager.writeJSONObject(model, model_file_path);
	//						sp.add(sp.LABELLED_SENSOR_DATA(data.getSensorType()), true);
	//					}
	//				}
	//				//case 2: data labels ready (i.e. predictor is ready now)
	//				else{
	//					Log.d(TAG,"Data labels present!!");
	//					SensorDataClassifier classifier = new SensorDataClassifier(data.getSensorType());
	//					JSONFormatter formatter = DataFormatter.getJSONFormatter(app_context, data.getSensorType());
	//					String json = formatter.toJSON(data).toString();
	//					JSONObject json_object = new JSONObject(json);
	//					Log.d(TAG,"JSON Object of the sensor data: "+ json_object);
	//					String classified_data = classifier.classifySensorData(json_object);
	//					Log.d(TAG,"Classified data: "+classified_data);
	//
	//					//Update latest context in SP
	//					sp.add(sp.LATEST_SENSOR_DATA(data.getSensorType()), classified_data);
	//
	//					JSONObject jsonObject = new JSONObject();
	//					jsonObject.put("TIME", Time.getCurrentTime().toString());
	//					jsonObject.put("STATE", classified_data);
	//
	//
	//					//case 2.1: save it in file
	//					String data_file_path = file_path_generator.generateStackedPredictorDataFilePath((Constants.getPredictor(data.getSensorType())));
	//					file_manager.appendJSONObjectInJSONArray("DATA", jsonObject, data_file_path);
	//
	//					//case 2.2 update the latest context data on the server (removed for now)
	//					//					CommunicationManager cm = new CommunicationManager(app_context);
	//					//					String user_id = sp.getString(sp.USER_ID);
	//					//					cm.transmitUpdatedUserContext(user_id, data.getSensorType(), classified_data);
	//
	//					//case 2.3 if file size is equal to the data collected for a day then create model and merge it to the existing
	//					int context_sampling_rate = sp.getInt(sp.CONTEXT_SAMPLING_RATE);
	//					int context_life_cycle = sp.getInt(sp.CONTEXT_LIFE_CYCLE_TIME_PERIOD);
	//					int expected_data_size = context_life_cycle / context_sampling_rate;
	//					JSONObject raw_data = file_manager.readJSONObject(data_file_path);
	//					Log.d(TAG,"Expected size of the stacked data: "+expected_data_size);
	//					Log.d(TAG,"Actual size of the stacked data: "+raw_data.getJSONArray("DATA").length());
	//					if(raw_data.getJSONArray("DATA").length() >= expected_data_size){
	//						Log.d(TAG,"Actual size of the stacked data is greater than expected size!!");
	//
	//						JSONObject model_for_today = predictor_model_manager.createModelFromData(Constants.getPredictor(data.getSensorType()), raw_data);
	//						Log.d(TAG,"model_for_today: "+model_for_today);
	//						String model_file_path = file_path_generator.generatePredictorModelDataFilePath(Constants.getPredictor(data.getSensorType()));
	//						JSONObject historic_model = file_manager.readJSONObject(model_file_path);
	//						Log.d(TAG,"historic_model: "+historic_model);
	//						JSONObject merged_model = predictor_model_manager.mergeModels(model_for_today, historic_model, Constants.getPredictor(data.getSensorType()));
	//						Log.d(TAG,"merged_model: "+merged_model);
	//						//update the predictor model data file
	//						file_manager.deleteFile(model_file_path);
	//						file_manager.writeJSONObject(merged_model, model_file_path);
	//						Log.d(TAG,"model_updated");
	//
	//						//delete today's data file
	//						file_manager.deleteFile(data_file_path);
	//
	//						/**
	//						 * 1. Check if the model is required on the server
	//						 * 2. if true then send it to the server
	//						 */
	//						PrivacyManager privacy_manager = new PrivacyManager(app_context);
	//						if(privacy_manager.isDataTransmissionToServerEnabled()){
	//							CommunicationManager communication_manager = new CommunicationManager(app_context);
	//							String user_id = sp.getString(sp.USER_ID);
	//							communication_manager.transmitPredictionModelData(user_id, Constants.getPredictor(data.getSensorType()), merged_model);	
	//						}										
	//					}
	//				}
	//
	//			} catch (Exception e) {
	//				Log.e(TAG,"OneOffSensing- onPostExecute() Error: " + e.toString());
	//			}
	//		}
	//	}

	public void newRemotePredictionRequest(String requestor_id, int subscription_id, 
			int predictor_type, String preiction_type, String prediction_type_value, String predictor_name_or_path){
		//TODO:
		try {
			Predictor predictor = new Predictor(app_context, predictor_type, predictor_name_or_path);
			ArrayList<Integer>  predictor_ids_of_required_data = predictor.idsOfRequiredData(predictor_type);;
			ArrayList<JSONObject> models = new ArrayList<JSONObject>();
			JSONObject model;
			for(int p_id :predictor_ids_of_required_data){
				model = getModel(p_id, predictor_name_or_path);
				models.add(model);
			}
			SharedPreferences sp = new SharedPreferences(this.app_context);
			String current_state = sp.getString(sp.LATEST_CLASSIFIED_SENSOR_DATA(Constants.getSensor(predictor_type)));
			PredictorData predictor_data = predictor.predictionRequest(models, current_state, prediction_type_value);

			//transmit the prediction data to the server
			CommunicationManager cm = new CommunicationManager(this.app_context);
			cm.transmitRemoteQueryResponse(requestor_id, subscription_id, predictor_type, predictor_data);

		} catch (Exception e) {
			Log.e(TAG, "Error with remote predoction: "+ e.toString());
		}
	}

	public void newRemotePredictionRequest(String requestor_id, int subscription_id, 
			int predictor_type, String preiction_type, long prediction_type_value, String predictor_name_or_path){
		//TODO:
		try {
			PredictorCollection pc = new PredictorCollection();
			ArrayList<Integer>  predictor_ids_of_required_data = pc.getPredictorModule(predictor_name_or_path).idsOfRequiredData(predictor_type);
			ArrayList<JSONObject> models = new ArrayList<JSONObject>();
			JSONObject model;
			for(int p_id :predictor_ids_of_required_data){
				model = getModel(p_id, predictor_name_or_path);
				models.add(model);
			}
			SharedPreferences sp = new SharedPreferences(this.app_context);
			String current_state = sp.getString(sp.LATEST_CLASSIFIED_SENSOR_DATA(Constants.getSensor(predictor_type)));
			Predictor predictor = new Predictor(this.app_context, predictor_type, predictor_name_or_path);
			PredictorData predictor_data = predictor.predictionRequest(models, current_state, prediction_type_value);

			//transmit the prediction data to the server
			CommunicationManager cm = new CommunicationManager(this.app_context);
			cm.transmitRemoteQueryResponse(requestor_id, subscription_id, predictor_type, predictor_data);

		} catch (Exception e) {
			Log.e(TAG, "Error with remote predoction: "+ e.toString());
		}
	}

	public void onGroupPredictionResponse(int subscription_id, PredictorData predictor_data){
		//TODO:
		sp.add(sp.SOCIAL_PREDICTOR_STATUS(subscription_id), true);
		//save it in a file
		try {
			FilePathGenerator fpg = new FilePathGenerator();
			String fileLocation = fpg.generateGroupPredictionResultDataFilePath(subscription_id);
			FileManager fm = new FileManager();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(JSONKeys.GROUP_PREDICTION_DATA, ObjectSerializer.toString(predictor_data));
			fm.writeJSONObject(jsonObject, fileLocation);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private JSONObject getModel(int predictor_id, String predictor_name_or_path) throws AMException, JSONException
	{
		JSONObject model = new JSONObject();
		SharedPreferences sp = new SharedPreferences(this.app_context);
		SensorManager sm = new SensorManager(this.app_context);
		int context_sampling_rate = sm.getContextSamplingRate();
		int context_life_cycle = sm.getContextLifeCycleTimePeriod();
		int expected_data_size = context_life_cycle / context_sampling_rate;		
		SQLiteUtils utils = new SQLiteUtils();
		PredictorCollection pc = new PredictorCollection();
		String db_name = utils.getDbName();
		String model_data_table = utils.getModelDataTableName(Constants.getSensor(predictor_id), pc.getName(predictor_name_or_path));
		String stacked_data_table = utils.getStackedSensorDatatableName(Constants.getSensor(predictor_id));
		DBHelper db = new DBHelper(this.app_context, db_name);
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
			createDefaultModel(predictor_id, predictor_name_or_path);
		}
		double ratio = stacked_data_size / expected_data_size;
		double difference = ratio - Math.floor(ratio);
		Log.i(TAG, "stacked_data_size:" + stacked_data_size);
		Log.i(TAG, "expected_data_size:" + expected_data_size);
		Log.i(TAG, "ratio:" + ratio);
		Log.i(TAG, "difference:" + difference);
		if(stacked_data_size > 0 && stacked_data_size >= expected_data_size && difference < 0.005 && !Double.isInfinite(ratio))
		{
			Log.d(TAG,"Size of the stack is exact multiple of expected_size!!");
			updateModel(predictor_id, predictor_name_or_path);
			// Check if the model is required on the server if true then send it to the server
			PrivacyManager privacy_manager = new PrivacyManager(this.app_context);
			if(privacy_manager.isDataTransmissionToServerEnabled())
			{
				CommunicationManager communication_manager = new CommunicationManager(this.app_context);
				String user_id = sp.getString(sp.USER_ID);
				communication_manager.transmitPredictionModelData(user_id, predictor_id, model);	
			}										
		}
		else
		{
			JSONArray model_data_json_array = db.getRecordsAsJSONArray(model_data_table);
			model.put("DATA", model_data_json_array);						
		}
		return model;
	}

	private void createDefaultModel(int predictor_id, String predictor_name_or_path)
	{
		try
		{
			SensorDataClassifier classifier = new SensorDataClassifier(this.app_context, Constants.getSensor(predictor_id));
			ArrayList<String> states = classifier.getAllLabels();
			Predictor p = new Predictor(this.app_context, predictor_id, predictor_name_or_path);
			JSONObject model = p.generateDataModel(null, states);
			JSONArray model_data_json_array = model.getJSONArray("DATA");

			SQLiteUtils utils = new SQLiteUtils();
			PredictorCollection pc = new PredictorCollection();
			String db_name = utils.getDbName();
			String model_data_table = utils.getModelDataTableName(Constants.getSensor(predictor_id), pc.getName(predictor_name_or_path));
			DBHelper db = new DBHelper(this.app_context, db_name);
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


	private void updateModel(int predictor_id, String predictor_name_or_path) throws JSONException
	{
		SQLiteUtils utils = new SQLiteUtils();
		PredictorCollection pc = new PredictorCollection();
		String db_name = utils.getDbName();
		String model_data_table = utils.getModelDataTableName(Constants.getSensor(predictor_id), pc.getName(predictor_name_or_path));
		String stacked_data_table = utils.getStackedSensorDatatableName(Constants.getSensor(predictor_id));
		DBHelper db = new DBHelper(this.app_context, db_name);

		JSONArray stacked_data_json_array = db.getRecordsAsJSONArray(stacked_data_table);


		SensorDataClassifier classifier = new SensorDataClassifier(this.app_context, Constants.getSensor(predictor_id));
		ArrayList<String> states = classifier.getAllLabels();
		Predictor p = new Predictor(this.app_context, predictor_id, predictor_name_or_path);


		JSONObject stacked_data = new JSONObject();
		stacked_data.put("DATA", stacked_data_json_array);
		JSONObject model = p.generateDataModel(stacked_data, states);
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
		SharedPreferences sp = new SharedPreferences(this.app_context);
		PrivacyManager privacy_manager = new PrivacyManager(this.app_context);
		if (privacy_manager.isDataTransmissionToServerEnabled()) {
			CommunicationManager communication_manager = new CommunicationManager(this.app_context);
			String user_id = sp.getString(sp.USER_ID);
			communication_manager.transmitPredictionModelData(user_id,
					predictor_id, model);
		}
	}


	private Subscription readSubscriptionFRomFile(int subscription_id)
			throws AMException {
		Log.e(TAG, "Subscription object is null.");
		Log.e(TAG, "Subscription id: " + subscription_id);
		Log.e(TAG,
				"Subscription list: "
						+ AnticipatoryManager.getAnticipatoryManager(app_context).getAllSubscriptions());

		Log.e(TAG, "Reading SubscriptionList object from file.");
		FileManager file_manager = new FileManager();
		List<Subscription> subscription_list = file_manager
				.getSubscriptionListFromFile(app_context);
		Log.e(TAG, "Subscription list: " + subscription_list);
		SubscriptionList subscriptionList = new SubscriptionList();
		SubscriptionList newSubscriptionList = subscriptionList
				.createSubscriptionList(subscription_list);
		Subscription subscription = newSubscriptionList
				.getSubscription(subscription_id);
		Log.e(TAG, "Subscription: " + subscription);
		return subscription;
	}
}
