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
package org.ubhave.anticipatorymiddleware.predictor;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.classifiers.SensorDataClassifier;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.time.Time;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.util.Log;

public class PredictorModelManager {

	private static final String TAG = "AnticipatoryManager";

	private final SharedPreferences sp;
	
	private final Context app_context;

	public PredictorModelManager(Context app_context){
		this.sp = new SharedPreferences(app_context);
		this.app_context = app_context;
	}

	/**
	 * This method converts the predictor training data to model. 
	 * It should be called after the initial training stage (data collected for 7 days) is over.
	 * The training data file must be deleted after this methods is invoked.
	 * @param data (JSONObject)
	 * Example- {"DATA" = [
	 * {"SATE" = "WORK", "TIME" = "1340"}, 
	 * {"SATE" = "HOME", "TIME" = "2230"}, ....]}
	 * @return {@link JSONObject}
	 * @throws JSONException 
	 */
	public JSONObject createModelFromData(int predictor_type, JSONObject data) throws JSONException{
		/**TODO: 
		 * 1. create state map
		 * 2. create transition data
		 */
		JSONObject model = new JSONObject();

		int context_sampling_rate = sp.getInt(sp.CONTEXT_SAMPLING_RATE);
		int context_life_cycle = sp.getInt(sp.CONTEXT_LIFE_CYCLE_TIME_PERIOD);
		int number_of_samples_in_a_day = context_life_cycle / context_sampling_rate;
		// total number of transition matrices
		//		int transition_matrices_required = number_of_samples_in_a_day * number_of_samples_in_a_day;
		int transition_matrices_required = number_of_samples_in_a_day * (number_of_samples_in_a_day-1);

		SensorDataClassifier classifier = new SensorDataClassifier(this.app_context, Constants.getSensor(predictor_type));
		ArrayList<String> states = classifier.getAllLabels();
		int number_of_states = states.size();
		Log.d(TAG,"STATES: "+states);

		//create state map
		JSONArray state_map =  new JSONArray();
		JSONObject state_transition;
		for(int i=0; i<number_of_states; i++){
			for(int j=0; j<number_of_states; j++){
				state_transition = new JSONObject();
				state_transition.put("FROM", states.get(i));
				state_transition.put("TO", states.get(j));
				state_map.put(state_transition);
			}
		}
		model.put("STATE_MAP", state_map);
		Log.d(TAG,"STATE_MAP created: "+ state_map);

		JSONArray transition_matrices = new JSONArray();
		JSONArray transition_matrix;

		//initialise all transition matrices
		for(int i=0; i<transition_matrices_required; i++){
			transition_matrix = new JSONArray();
			//initialise the matrix
			for(int j=0; j<state_map.length(); j++){
				transition_matrix.put(0);
			}
			transition_matrices.put(transition_matrix);
		}
		Log.d(TAG,"transition matrices initialized");


//		JSONArray ja = new JSONArray();
		if(data!=null){
			JSONArray data_array = data.getJSONArray("DATA");
			Log.d(TAG,"data array size: "+data_array.length());
			JSONObject from_state;
			JSONObject to_state;
			//create transition matrices
			for(int i =0; i<data_array.length(); i++){
				from_state = (JSONObject) data_array.get(i);
				String from_state_value = from_state.getString("STATE");
				Time from_time = Time.stringToTime(from_state.getString("TIME"));
				for(int j=0; j< data_array.length(); j++){
					if(i == j){
						continue;
					}
					to_state = (JSONObject) data_array.get(j);
					String to_state_value = to_state.getString("STATE");
					Time to_time = Time.stringToTime(to_state.getString("TIME"));

					int _index_a;
					int _index_b;
					_index_a = (((from_time.get(Time.HOURS) * 60 ) + from_time.get(Time.MINUTES)) / context_sampling_rate) * (number_of_samples_in_a_day-1);
					if(i>j)
					_index_b = (((to_time.get(Time.HOURS) * 60 ) + to_time.get(Time.MINUTES)) / context_sampling_rate)+1;
					else
						_index_b = ((to_time.get(Time.HOURS) * 60 ) + to_time.get(Time.MINUTES)) / context_sampling_rate;
					
					int _index = _index_a + _index_b -1 ;

					if(_index < 0){
						Log.e(TAG,"Index is negative!!");
						continue; 
					}

					//get the transition matrix
					JSONArray matrix = transition_matrices.getJSONArray(_index);

					//get index of state_transition from state_map and increase the value by 1
					int state_map_index = indexOfStateTransition(state_map, from_state_value, to_state_value);
					//					Log.d(TAG,"state_map_index: "+state_map_index);

					if(state_map_index < 0){
						Log.e(TAG,"state_map_index is negative!!");
						continue;
					}

					int value = matrix.getInt(state_map_index);
					value = value + 1;
					matrix.put(state_map_index, value);		
					transition_matrices.put(_index, matrix);


//					JSONObject obj = new JSONObject();
//					obj.put("FROM_STATE", from_state);
//					obj.put("TO_STATE", to_state);
//					obj.put("INDEX", _index);
//					obj.put("INDEX_A", _index_a);
//					obj.put("INDEX_B", _index_b);
//					obj.put("STATE_MAP_INDEX", state_map_index);
//					ja.put(obj);
				}			
			}
		}
		else{
			Log.e(TAG,"Data null: "+data);
		}
//		Log.e(TAG,"JA: "+ja);
//
//		FilePathGenerator fpg = new FilePathGenerator();
//		String file_path = fpg.generateTrainingDataForLabelsFilePath(0);
//		FileManager fm = new FileManager();
//		try {
//			JSONObject jo = new JSONObject();
//			jo.put("DATA", ja);
//			fm.writeJSONObject(jo, file_path);
//		} catch (AMException e) {
//			Log.e(TAG,"TESTING ERROR: "+e.toString());
//		}

		Log.d(TAG,"transition matrices created");
		model.put("TRANSITION_MATRICES", transition_matrices);	
		Log.d(TAG,"Created model: "+ model);	
		return model;
	}


	public JSONObject mergeModels(JSONObject model_a, JSONObject model_b, int predictor_type) throws JSONException{
		JSONArray state_map = model_a.getJSONArray("STATE_MAP");
		JSONArray transition_matrices_a = model_a.getJSONArray("TRANSITION_MATRICES");
		JSONArray transition_matrices_b = model_b.getJSONArray("TRANSITION_MATRICES");
		JSONArray merged_transition_matrices = new JSONArray();
		JSONArray matrix_a, matrix_b, merged_matrix;
		for(int i=0; i< transition_matrices_a.length(); i++){
			merged_matrix = new JSONArray();
			matrix_a = transition_matrices_a.getJSONArray(i);
			matrix_b = transition_matrices_b.getJSONArray(i);
			for(int j=0; j<state_map.length(); j++){
				merged_matrix.put(matrix_a.getInt(j) + matrix_b.getInt(j));
			}
			merged_transition_matrices.put(merged_matrix);
		}
		JSONObject model = new JSONObject();
		model.put("STATE_MAP", state_map);
		model.put("TRANSITION_MATRICES", merged_transition_matrices);
		return model;
	}


	/**
	 * used when data logging is disabled
	 * @param model
	 * @param data
	 * @return {@link JSONObject}
	 * @throws JSONException 
	 */
	public JSONObject modifyExistingModelWithNewData(JSONObject model, JSONObject data, int predictor_type) throws JSONException{
		//TODO: update existing model

		JSONArray state_map =  model.getJSONArray("STATE_MAP");
		JSONArray transition_matrices = model.getJSONArray("TRANSITION_MATRICES");
		JSONArray transition_matrix;

		String to_state_value = data.getString("STATE");
		Time to_state_time = Time.stringToTime(data.getString("TIME"));

		ArrayList<Integer> modification_indices = indicesOfStateTransitionWithToState(state_map, to_state_value);
		int context_sampling_rate = sp.getInt(sp.CONTEXT_SAMPLING_RATE);
		int context_life_cycle = sp.getInt(sp.CONTEXT_LIFE_CYCLE_TIME_PERIOD);
		int number_of_samples_in_a_day = context_life_cycle / context_sampling_rate;		
		int start_index = ((to_state_time.get(Time.HOURS) * 60 ) + to_state_time.get(Time.MINUTES)) / context_sampling_rate;

		for(int i= start_index; i< transition_matrices.length(); i=i+number_of_samples_in_a_day){
			transition_matrix = transition_matrices.getJSONArray(i);
			for(int index : modification_indices){
				int previous_value = transition_matrix.getInt(index);
				int updated_value = previous_value + 1;
				transition_matrix.put(index, updated_value);
			}
			transition_matrices.put(i, transition_matrix);
		}
		model.put("TRANSITION_MATRICES", transition_matrices);		
		return model;
	}


	private static int indexOfStateTransition(JSONArray state_map, String from_state, String to_state) throws JSONException{
		for(int i=0 ; i<state_map.length(); i++){
			JSONObject object = state_map.getJSONObject(i);
			if(object.getString("FROM").equalsIgnoreCase(from_state) && object.getString("TO").equalsIgnoreCase(to_state)){
				return i;
			}
		}
		return -1; //not found
	}

	@SuppressWarnings("unused")  //can be useful??
	private static ArrayList<Integer> indicesOfStateTransitionWithFromState(JSONArray state_map, String from_state) throws JSONException{
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for(int i=0 ; i<state_map.length(); i++){
			JSONObject object = state_map.getJSONObject(i);
			if(object.getString("FROM").equalsIgnoreCase(from_state)){
				indices.add(i);
			}
		}
		return indices;
	}

	private static ArrayList<Integer> indicesOfStateTransitionWithToState(JSONArray state_map, String to_state) throws JSONException{
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for(int i=0 ; i<state_map.length(); i++){
			JSONObject object = state_map.getJSONObject(i);
			if(object.getString("TO").equalsIgnoreCase(to_state)){
				indices.add(i);
			}
		}
		return indices;
	}
}
