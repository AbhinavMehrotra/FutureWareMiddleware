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

import java.util.ArrayList;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.alarm.AlarmMgr;
import org.ubhave.anticipatorymiddleware.alarm.AlarmReceiver;
import org.ubhave.anticipatorymiddleware.classifiers.SensorDataClassifier;
import org.ubhave.anticipatorymiddleware.sensors.OneOffSampling;
import org.ubhave.anticipatorymiddleware.sensors.SensorManager;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.storage.DBHelper;
import org.ubhave.anticipatorymiddleware.storage.SQLiteUtils;
import org.ubhave.anticipatorymiddleware.time.Time;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.util.Log;

import com.ubhave.dataformatter.DataFormatter;
import com.ubhave.dataformatter.json.JSONFormatter;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pullsensor.LocationData;

public class DataCollectionService extends IntentService {

	private Context app_context;

	private SharedPreferences sp;

	//	private final FilePathGenerator file_path_generator = new FilePathGenerator(); 
	//
	//	private final FileManager file_manager = new FileManager();	
	//
	//	private String predictor_name_or_path;

	//	private SensorData data;


	private static final String TAG = "AnticipatoryManager";

	public DataCollectionService() {
		super("DataCollectionService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Inside DataCollectionService : onHandleIntent()");
		app_context = getApplicationContext();
		sp = new SharedPreferences(app_context);
		String alarm_type = intent.getExtras().getString("ALARM_TYPE");
		//		this.predictor_name_or_path = intent.getExtras().getString("PREDICTOR_NAME_OR_PATH");

		if(alarm_type == null){
			Log.e(TAG, "Why is the alarm type is null??");
		}
		else if(alarm_type.equals(AlarmMgr.ALARM_TYPE_DATA_COLLECTION)){
			Log.e(TAG, "Data collection alarm trigerred!!");	
			sampleSensorData();		
		}
		else{
			Log.e(TAG, "Unknown alarm type!!");
		}
		AlarmReceiver.completeWakefulIntent(intent);
		Log.d(TAG, "Wakeful Intent released for intent of alarm: "+ alarm_type);
		return; 
	}


	private void sampleSensorData(){
		SensorManager active_sensor_list = new SensorManager(app_context);
		Set<Integer> sensors = active_sensor_list.getActiveSensorList();
		//Start context sampling and store it in a file
		for(int sensor : sensors){
			try {
				new OneOffSampling(app_context, sensor){
					@Override
					public void onPostExecute(SensorData data){
						Log.d(TAG,"OneOffSensing- onPostExecute()");
						onNewSensorData(data);
					}
				}.execute();
			} catch (ESException e) {
				Log.e(TAG,"Error!! OneOffSensing- onPostExecute() Error: " + e.toString());
			}
		}
	}


	private void onNewSensorData(SensorData data)
	{
		if(data == null)
			return;

		int sensor_id  = data.getSensorType();
		SQLiteUtils sql_utils = new SQLiteUtils();
		String db_name = sql_utils.getDbName();
		DBHelper db = new DBHelper(getApplicationContext(), db_name);
		String stacked_data_table = sql_utils.getStackedSensorDatatableName(sensor_id);
		String label_data_table = sql_utils.getLabelDataTableName(sensor_id);
		//		String model_data_table = sql_utils.getModelDataTableName(sensor_id);



		//case 1: data labels required and data not labelled 
		if(Constants.isLabelrequired(sensor_id) && !sp.getBoolean(sp.LABELLED_SENSOR_DATA(sensor_id)))
		{
			try
			{
				JSONObject jsonObject = new JSONObject();
				JSONFormatter formatter = DataFormatter.getJSONFormatter(app_context, data.getSensorType());
				jsonObject.put("SENSOR_DATA", formatter.toJSON(data));
				jsonObject.put("TIME", Time.getCurrentTime().toString());
				db.addRecord(label_data_table, jsonObject.toString());		

				//check if the training data size for labelling has reached the mark ( life cycle / CSR )
				SensorManager sm = new SensorManager(app_context);
				int context_sampling_rate = sm.getContextSamplingRate();
				int context_life_cycle = sm.getContextLifeCycleTimePeriod();
				int expected_data_size = (2 * context_life_cycle) / context_sampling_rate; //for 2days data collection
				int records_count;
				Cursor c = db.selectRecords(label_data_table);
				if(c != null)
				{
					records_count = c.getCount();
					c.close();
				}
				else
				{
					records_count = 0;
				}
				if(records_count >= expected_data_size){
					JSONArray label_data_array = new JSONArray();
					for(String label_data_string : db.getRecords(label_data_table))
					{
						JSONObject label_data_json = new JSONObject(label_data_string);
						label_data_array.put(label_data_json);
					}
					SensorDataClassifier classifier = new SensorDataClassifier(app_context, sensor_id);
					classifier.trainClassifier(label_data_array);		
					sp.add(sp.LABELLED_SENSOR_DATA(sensor_id), true);
				}				
			}
			catch(JSONException e)
			{
				Log.e(TAG, e.toString());
			}
		}
		//case 2: data labels ready or not required (i.e. predictor is ready now)
		else
		{
			try 
			{
				ArrayList<JSONObject> json_object_list = generateClassifiedData(data);
				for(JSONObject json_object : json_object_list)
				{
					db.addRecord(stacked_data_table, json_object.toString());
					Log.i(TAG, "New data record added to DB: "+ json_object.toString());
				}				
				int records_count;
				Cursor c = db.selectRecords(stacked_data_table);
				if(c != null)
				{
					records_count = c.getCount();
					c.close();
				}
				else
				{
					records_count = 0;
				}
				Log.i(TAG, "Total number of records in stacked data table for sensor "+data.getSensorType()+" are: "+ records_count);
			} 
			catch (JSONException e) 
			{
				Log.e(TAG, e.toString());
			}
		}
	}

	private ArrayList<JSONObject> generateClassifiedData(SensorData data) throws JSONException{
		ArrayList<JSONObject> json_object_list = new ArrayList<JSONObject>();
		SensorDataClassifier classifier = new SensorDataClassifier(app_context, data.getSensorType());
		String classified_data = classifier.classifySensorData(data);

		if(classified_data.length()>1)
		{

			if(data.getSensorType() == Constants.SENSOR_TYPE_APPLICATION
					|| data.getSensorType() == Constants.SENSOR_TYPE_CALL_LOGS
					|| data.getSensorType() == Constants.SENSOR_TYPE_SMS_LOGS)
			{

				JSONArray classified_data_array = new JSONArray(classified_data);

				/**  Update latest context in SharedPreferences  **/
				setLatestDataInSharedPreferences(data, classified_data_array.getString(0));
				
				for(int i=0; i<classified_data_array.length(); i++)
				{
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("TIME", Time.getCurrentTime().toString());
					jsonObject.put("STATE", classified_data_array.get(i));
					json_object_list.add(jsonObject);
				}
			}
			else
			{
				/**  Update latest context in SharedPreferences  **/
				setLatestDataInSharedPreferences(data, classified_data);
				
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("TIME", Time.getCurrentTime().toString());
				jsonObject.put("STATE", classified_data);
				if(data.getSensorType() == Constants.SENSOR_TYPE_LOCATION && classified_data.equalsIgnoreCase("UNKNOWN"))
				{
					jsonObject.put("RAW_DATA", ((LocationData)data).getLocation());
				}
				json_object_list.add(jsonObject);
			}	
		}
		return json_object_list;
	}

	private void setLatestDataInSharedPreferences(SensorData data, String classified_data)
	{
		if(data.getSensorType() == Constants.SENSOR_TYPE_APPLICATION)
		{
			if(classified_data.length() > 1)
			{
				sp.add(sp.LATEST_CLASSIFIED_SENSOR_DATA(data.getSensorType()), classified_data);
			}			
		}
		else
		{
			sp.add(sp.LATEST_CLASSIFIED_SENSOR_DATA(data.getSensorType()), classified_data);
		}

		if(data.getSensorType() == Constants.SENSOR_TYPE_LOCATION)
		{
			try
			{
				Location location = ((LocationData) data).getLocation();
				JSONObject raw_data = new JSONObject();
				raw_data.put("LATITUDE", location.getLatitude());
				raw_data.put("LONGITUDE", location.getLongitude());
				sp.add(sp.LATEST_RAW_SENSOR_DATA(data.getSensorType()), raw_data.toString());
			}
			catch(JSONException e)
			{
				Log.e(TAG, e.toString());
			}
		}
	}

}
