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

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.time.Time;
import org.ubhave.anticipatorymiddleware.utils.Constants;

public class KNNMeansClustering {

	private static final String TAG = "AnticipatoryManager";
	private int sensor_id;
	private Random keyGenerator = new Random();
	
	public KNNMeansClustering(int sensor_id){
		this.sensor_id = sensor_id;
	}
	
	public JSONArray createLabels(JSONArray sensor_data) throws JSONException {
		JSONArray label_array = new JSONArray();
		switch (sensor_id){
		case Constants.SENSOR_TYPE_LOCATION :
			label_array = createLocationLabels(sensor_data);
		}
		return label_array;
	}
	
	

	private JSONArray createLocationLabels(JSONArray sensor_data) throws JSONException{
		int n = sensor_data.length();
		JSONObject centroid_a = sensor_data.getJSONObject(keyGenerator.nextInt(n));
		JSONObject centroid_b = sensor_data.getJSONObject(keyGenerator.nextInt(n));
		for(int i=0; i<n; i++){
			//divide data in to 2 clusters
			for(int j=0; j<n; j++){
				JSONObject element = sensor_data.getJSONObject(j);
				JSONObject element_location = element.getJSONObject("SENSOR_DATA");
				JSONObject location_a = centroid_a.getJSONObject("SENSOR_DATA");
				JSONObject location_b = centroid_b.getJSONObject("SENSOR_DATA");
				double distance_from_a = calculateDistanceinMeters(element_location.getDouble("latitude"), element_location.getDouble("longitude"), location_a.getDouble("latitude"), location_a.getDouble("longitude"));
				double distance_from_b = calculateDistanceinMeters(element_location.getDouble("latitude"), element_location.getDouble("longitude"), location_b.getDouble("latitude"), location_b.getDouble("longitude"));
				if(distance_from_a < distance_from_b){
					//element belongs to cluster a
					element.put("STATE", "A");
				}
				else{
					//element belongs to cluster b
					element.put("STATE", "B");
				}				
			}

			//update the centroids 
			double sum_lat_a = 0;
			double sum_long_a = 0;
			double data_point_a = 0;
			double sum_lat_b = 0;
			double sum_long_b = 0;
			double data_point_b = 0;
			for(int j=0; j<n; j++){
				JSONObject element = sensor_data.getJSONObject(j);
				JSONObject element_location = element.getJSONObject("SENSOR_DATA");
				String element_state = element.getString("STATE");
				if(element_state.equals("A")){
					sum_lat_a += element_location.getDouble("latitude");
					sum_long_a += element_location.getDouble("longitude");
					data_point_a++;
				}
				else{
					sum_lat_b += element_location.getDouble("latitude");
					sum_long_b += element_location.getDouble("longitude");
					data_point_b++;
				}
			}
			double mean_lat_a = sum_lat_a/data_point_a;
			double mean_long_a = sum_long_a/data_point_a;
			double mean_lat_b = sum_lat_b/data_point_b;
			double mean_long_b = sum_long_b/data_point_b;
			int index_a=0, index_b=0;
			double new_distance, distance_a = 1000000, distance_b = 1000000;			
			for(int j=0; j<n; j++){
				JSONObject element = sensor_data.getJSONObject(j);
				JSONObject element_location = element.getJSONObject("SENSOR_DATA");
				String element_state = element.getString("STATE");
				if(element_state.equals("A")){
					new_distance = calculateDistanceinMeters(mean_lat_a, mean_long_a, element_location.getDouble("latitude"), element_location.getDouble("longitude"));
					if(new_distance < distance_a){
						distance_a = new_distance;
						index_a = j;
					}
				}
				else{
					new_distance = calculateDistanceinMeters(mean_lat_b, mean_long_b, element_location.getDouble("latitude"), element_location.getDouble("longitude"));
					if(new_distance < distance_b){
						distance_b = new_distance;
						index_b = j;
					}
				}	
			}
			centroid_a = sensor_data.getJSONObject(index_a);
			centroid_b = sensor_data.getJSONObject(index_b);
		}

		//replace A and B with HOME and WORK
		int home_count_for_a = 0;
		int work_count_for_a = 0;
		for(int i=0; i<n; i++){
			JSONObject element = sensor_data.getJSONObject(i);
			String element_state = element.getString("STATE");
			if(element_state.equals("A")){
				Time time = Time.stringToTime(element.getString("TIME"));
				if(time.get(Time.HOURS) <= 5){
					home_count_for_a++;
				}
				else if(time.get(Time.HOURS) <= 5){
					work_count_for_a++;
				}
			}
		}
		String state_value_of_a = "";
		String state_value_of_b = "";
		if(home_count_for_a > work_count_for_a){
			state_value_of_a = "HOME";
			state_value_of_b = "WORK";
		}
		else{
			state_value_of_a = "WORK";
			state_value_of_b = "HOME";
		}

		//create json_array of labels
		JSONArray labels_array = new JSONArray();
		JSONObject data_a = new JSONObject();
		data_a.put("SENSOR_DATA", centroid_a.getJSONObject("SENSOR_DATA"));
		data_a.put("STATE", state_value_of_a);
		JSONObject data_b = new JSONObject();
		data_b.put("SENSOR_DATA", centroid_b.getJSONObject("SENSOR_DATA"));
		data_b.put("STATE", state_value_of_b);
		labels_array.put(data_a);
		labels_array.put(data_b);
		return labels_array;
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
		return distance;
	}
	

//	private void trainLocationClassifier(JSONArray sensor_data) {
//		/* TODO
//		 * 1. create labels for data
//		 * 2. save it in a file 
//		 */
//		try {
//			//create labels
//			JSONObject labels = new JSONObject(); //contains labels_array as DATA
//			JSONArray labels_array = createLabels(sensor_data);
//			labels.put("DATA", labels_array);
//
//			//save labels in a file
//			FilePathGenerator file_path_generator = new FilePathGenerator();
//			FileManager file_manager = new FileManager();
//			String file_path = file_path_generator.generateLabelledDataFilePath(this.sensor_id);
//			file_manager.writeJSONObject(labels, file_path);
//		} catch (Exception e) {
//			Log.e(TAG, "Error whie training the classifier for sensor type: " + sensor_id);
//			Log.e(TAG, e.toString());
//		} 
//	}

}
