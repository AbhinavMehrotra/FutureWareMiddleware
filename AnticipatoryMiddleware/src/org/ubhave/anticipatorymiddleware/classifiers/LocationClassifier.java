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
package org.ubhave.anticipatorymiddleware.classifiers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.storage.DBHelper;
import org.ubhave.anticipatorymiddleware.storage.SQLiteUtils;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.util.Log;

import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pullsensor.LocationData;



public class LocationClassifier  implements Classifier{

	private static final String TAG = "AnticipatoryManager";

	private final int sensor_id;

	private double distance_range = 200;

	private final Context context;

	public LocationClassifier(Context context){
		this.context =  context;
		this.sensor_id = Constants.SENSOR_TYPE_LOCATION;
	}

	@Override
	public void trainClassifier(JSONArray sensor_data) {
		try
		{
			//create labels
			KNNMeansClustering knn = new KNNMeansClustering(sensor_id);
			JSONArray label_array = knn.createLabels(sensor_data);
			//save labels in a file
			SQLiteUtils utils = new SQLiteUtils();
			String db_name = utils.getDbName();
			String label_table = utils.getLabelDataTableName(sensor_id);
			DBHelper db = new DBHelper(context, db_name);
			db.deleteTable(label_table); //clear the table
			for(int i=0; i<label_array.length(); i++){
				JSONObject label = label_array.getJSONObject(i);
				db.addRecord(label_table, label.toString());
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.toString());
		}
	}



	@Override
	public ArrayList<String> getAllLabels() {
		ArrayList<String> labels_list = new ArrayList<String>();
		SQLiteUtils utils = new SQLiteUtils();
		String db_name = utils.getDbName();
		String label_table = utils.getLabelDataTableName(sensor_id);
		DBHelper db = new DBHelper(context, db_name);
		String[] labels_array = db.getRecords(label_table);
		for(String label_string : labels_array){
			try {
				JSONObject label_json = new JSONObject(label_string);
				labels_list.add(label_json.getString("STATE"));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
			labels_list.add("UNKNOWN");
		}
		return labels_list;
	}

	@Override
	public String classifySensorData(SensorData data) 
	{
		String classified_sensor_data = "UNKNOWN";
		try
		{
			double sensorData_latitude = ((LocationData) data).getLocation().getLatitude();
			double sensorData_longitude = ((LocationData) data).getLocation().getLongitude();
			SQLiteUtils utils = new SQLiteUtils();
			String db_name = utils.getDbName();
			String label_table = utils.getLabelDataTableName(sensor_id);
			DBHelper db = new DBHelper(context, db_name);
			String[] labels_array = db.getRecords(label_table);
			JSONArray labels_json_array = new JSONArray();
			for(String label_string : labels_array){
				JSONObject label_json = new JSONObject(label_string);
				labels_json_array.put(label_json);
			}

			String state;
			double latitude, longitude;
			JSONObject sensor_data_json_object;
			for(int i=0; i<labels_json_array.length(); i++){
				state = labels_json_array.getJSONObject(i).getString("STATE");
				sensor_data_json_object = labels_json_array.getJSONObject(i).getJSONObject("SENSOR_DATA");
				latitude = sensor_data_json_object.getDouble("latitude");
				longitude = sensor_data_json_object.getDouble("longitude");
				Log.d(TAG, "State: "+ state+ ", Latitude: "+ latitude+ ", Longitude: "+longitude);
				double distance = calculateDistanceinMeters(sensorData_latitude, sensorData_longitude, latitude, longitude);
				if(distance < distance_range){
					classified_sensor_data = state;
					distance_range = distance;
				}	
				else{
					Log.d(TAG, "State: "+ state+ "Distance: "+ distance);
				}			
			}
		} 
		catch (Exception e) 
		{
			Log.e(TAG, e.toString());
		} 		
		return classified_sensor_data;
	}

	private double calculateDistanceinMeters(double latitude_a, double longitude_a, double latitude_b, double longitude_b){
		//Haversine formula
		double radius = 6371 * 1000;
		double rlat_a = Math.toRadians(latitude_a);
		double rlat_b = Math.toRadians(latitude_b);
		double rlong_a = Math.toRadians(longitude_a);
		double rlong_b = Math.toRadians(longitude_b);
		double a = Math.pow(Math.sin((rlat_a-rlat_b)/2),2) 
				+ (Math.cos(rlat_a) * Math.cos(rlat_a) * Math.pow(Math.sin((rlong_a-rlong_b)/2),2));
		double distance = 2 * radius * Math.asin(Math.sqrt(a));
		Log.d(TAG, "Calculating distance by the Haversine formula!");
		Log.d(TAG, "Location A: "+ latitude_a+ ","+ longitude_a);
		Log.d(TAG, "Location B: "+ latitude_b+ ","+ longitude_b);
		Log.d(TAG, "Distance: "+ distance);
		return distance;
	}


	//	@Override
	//	public ArrayList<String> getAllLabels() {
	//		// TODO get data labels file and return the list of labels
	//		ArrayList<String> labels_list = new ArrayList<String>();
	//		try{
	//			FilePathGenerator file_path_generator = new FilePathGenerator();
	//			FileManager file_manager = new FileManager();
	//			String file_path = file_path_generator.generateLabelledDataFilePath(this.sensor_id);
	//			JSONObject labels = file_manager.readJSONObject(file_path);
	//			JSONArray labels_array = labels.getJSONArray("DATA");
	//			Log.e(TAG, "Labels file: " + labels);
	//			for(int i=0; i<labels_array.length(); i++){
	//				labels_list.add(labels_array.getJSONObject(i).getString("STATE"));
	//			}
	//			labels_list.add("UNKNOWN");
	//		} catch (JSONException e) {
	//			Log.e(TAG, "Error whie training the classifier for sensor type: " + sensor_id);
	//		}
	//		return labels_list;
	//	}
	//
	//	@Override
	//	public String classifySensorData(SensorData data) {
	//		String classified_sensor_data = "UNKNOWN";
	//		try{
	//			double sensorData_latitude = ((LocationData)data).getLocation().getLatitude();
	//			double sensorData_longitude = ((LocationData)data).getLocation().getLongitude();
	//			Log.d(TAG, "Data to be classified: Latitude: "+ sensorData_latitude+ ", Longitude: "+sensorData_longitude);
	//
	//			FilePathGenerator file_path_generator = new FilePathGenerator();
	//			FileManager file_manager = new FileManager();
	//			String file_path = file_path_generator.generateLabelledDataFilePath(this.sensor_id);
	//			JSONObject labels = file_manager.readJSONObject(file_path);
	//			JSONArray labels_array = labels.getJSONArray("DATA");	
	//			Log.d(TAG,"Labelled data: "+ labels_array);			
	//			String state;
	//			double latitude, longitude;
	//			JSONObject sensor_data_json_object;
	//			for(int i=0; i<labels_array.length(); i++){
	//				state = labels_array.getJSONObject(i).getString("STATE");
	//				sensor_data_json_object = labels_array.getJSONObject(i).getJSONObject("SENSOR_DATA");
	//				latitude = sensor_data_json_object.getDouble("latitude");
	//				longitude = sensor_data_json_object.getDouble("longitude");
	//				Log.d(TAG, "State: "+ state+ ", Latitude: "+ latitude+ ", Longitude: "+longitude);
	//				double distance = calculateDistanceinMeters(sensorData_latitude, sensorData_longitude, latitude, longitude);
	//				if(distance < distance_range){
	//					classified_sensor_data = state;
	//					distance_range = distance;
	//					//break;
	//				}	
	//				else{
	//					Log.d(TAG, "State: "+ state+ "Distance: "+ distance);
	//				}
	//				//				Location location2 = new Location(LocationManager.GPS_PROVIDER);
	//				//				location2.setLatitude(latitude);
	//				//				location2.setLongitude(longitude);
	//				//				float distance = location.distanceTo(location2);				
	//			}
	//		} catch (Exception e) {
	//			Log.e(TAG, "Error whie classifying data of sensor type: " + sensor_id);
	//			Log.e(TAG, e.toString());
	//		} 
	//		return classified_sensor_data.toUpperCase(Locale.ENGLISH);
	//	}



}

