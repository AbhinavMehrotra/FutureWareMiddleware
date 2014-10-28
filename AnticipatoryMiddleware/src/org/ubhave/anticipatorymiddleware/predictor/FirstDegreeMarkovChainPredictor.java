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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.communication.JSONKeys;
import org.ubhave.anticipatorymiddleware.predictordata.PredictionResult;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.time.Time;

import android.content.Context;
import android.util.Log;

public class FirstDegreeMarkovChainPredictor implements PredictorModule{

	private static final String TAG = "AnticipatoryManager";

	public FirstDegreeMarkovChainPredictor(){};

	@Override
	public PredictorData predictionRequest(int predictor_id, int context_sampling_rate, int context_life_cycle, 
			ArrayList<JSONObject> predictor_models, String current_state, String state_to_be_predicted){		

		PredictorData predictor_data = PredictorData.getInstance(predictor_id);
		ArrayList<PredictionResult> result = new ArrayList<PredictionResult>();


		try
		{
			JSONArray state_map = predictor_models.get(0).getJSONArray("DATA");
			Map<String, Integer> map = new HashMap<String, Integer>();
			int i;
			for(i=0; i<state_map.length(); i++)
			{
				JSONObject transission= state_map.getJSONObject(i);
				String from = transission.getString("FROM");
				if(from.equalsIgnoreCase(current_state))
				{
					String to = transission.getString("TO");
					int instances = transission.getInt("INSTANCES");
					map.put(to, instances);				
				}
			}

			int sum = 0;
			for(Integer v : map.values())
			{
				sum += v;				
			}

			for(Map.Entry<String, Integer> e : map.entrySet())
			{
				PredictionResult p_result = new PredictionResult();
				p_result.setPredictedState(e.getKey());			
				p_result.setPredictedTime(Time.getCurrentTime());
				p_result.setPredictionConfidenceLevel(100);
				p_result.setPredictionProbability((e.getValue() * 100) / sum);			
			}

		}
		catch(JSONException e){
			System.out.println("Error!! "+ e.toString());
		} 

		predictor_data.setResult(result);
		return predictor_data;
	}

	@Override
	public PredictorData predictionRequest(int predictor_id, int context_sampling_rate, int context_life_cycle, ArrayList<JSONObject> predictor_models, String current_state, long notification_period){
		PredictorData predictor_data = PredictorData.getInstance(predictor_id);
		ArrayList<PredictionResult> result = new ArrayList<PredictionResult>();



		try
		{
			JSONArray state_map_parent = predictor_models.get(0).getJSONArray("DATA");
			JSONArray state_map = state_map_parent.getJSONArray(0);
			Log.v(TAG, "Model: "+state_map);
			Map<String, Integer> map = new HashMap<String, Integer>();
			int i;
			for(i=0; i<state_map.length(); i++)
			{
				Log.v(TAG, "Looking for: "+current_state);
				JSONObject transission= state_map.getJSONObject(i);
				String from = transission.getString("FROM");
				Log.v(TAG, "Current from value: "+from);
				if(from.equalsIgnoreCase(current_state))
				{
					Log.e(TAG, "Match");
					String to = transission.getString("TO");
					int instances = transission.getInt("INSTANCES");
					map.put(to, instances);				
				}
			}

			Log.v(TAG, "Map: "+map);

			int sum = 0;
			for(Integer v : map.values())
			{
				sum += v;				
			}

			for(Map.Entry<String, Integer> e : map.entrySet())
			{
				if(sum > 0){
					PredictionResult p_result = new PredictionResult();
					p_result.setPredictedState(e.getKey());			
					p_result.setPredictedTime(Time.getCurrentTime());
					p_result.setPredictionConfidenceLevel(100);
					p_result.setPredictionProbability((e.getValue() * 100) / sum);	
					result.add(p_result);
				}
			}

		}
		catch(JSONException e){
			Log.e(TAG, e.toString());
		} 

		predictor_data.setResult(result);
		return predictor_data;
	}



	@Override
	public ArrayList<Integer> idsOfRequiredData(int predictor_type){		
		ArrayList<Integer> data_types = new ArrayList<Integer>();
		data_types.add(predictor_type);
		return data_types;
	}




	@Override
	public JSONObject generateDataModel(Context app_context, JSONObject data, ArrayList<String> state_list) {
		/**TODO: 
		 * 1. create state map
		 * 2. create transition data
		 */
		try{
			JSONObject model = new JSONObject();
			int number_of_states = state_list.size();

			//removing the duplicates
			Set<String> state_set = new HashSet<String>();
			state_set.addAll(state_list);
			state_list = new ArrayList<String>();
			state_list.addAll(state_set);

			//create state map
			JSONArray state_map_parent =  new JSONArray();
			JSONArray state_map =  new JSONArray();
			JSONObject state_transition;
			for(int i=0; i<number_of_states; i++){
				for(int j=0; j<number_of_states; j++){
					state_transition = new JSONObject();
					state_transition.put("FROM", state_list.get(i));
					state_transition.put("TO", state_list.get(j));
					state_transition.put("INSTANCES", 0);
					state_map.put(state_transition);
				}
			}
			state_map_parent.put(state_map);
			model.put("DATA", state_map_parent);
			Log.d(TAG,"STATE_MAP created: "+ state_map);


			if(data!=null){
				JSONArray data_array = data.getJSONArray("DATA");
				Log.d(TAG,"data array size: "+data_array.length());
				JSONObject from_state;
				JSONObject to_state;
				for(int i =0; i<data_array.length()-1; i++)
				{
					from_state = (JSONObject) data_array.get(i);
					String from_state_value = from_state.getString("STATE");
					to_state = (JSONObject) data_array.get(i+1);
					String to_state_value = to_state.getString("STATE");

					int j;
					int len = state_map.length();
					for(j=0; j<len; j++)
					{
						JSONObject transission= state_map.getJSONObject(j);
						String from = transission.getString("FROM");
						String to = transission.getString("TO");
						if(from.equalsIgnoreCase(from_state_value) && to.equalsIgnoreCase(to_state_value))
						{
							int instances = transission.getInt("INSTANCES");
							transission.put("INSTANCES", instances+1);
							state_map.put(j, transission);
							break;
						}						
					}					

				}		
				state_map_parent =  new JSONArray();
				state_map_parent.put(state_map);
				model.put("DATA", state_map_parent);

			}
			else{
				Log.e(TAG,"Data null: "+data);
			}
			Log.d(TAG,"Created model: "+ model);	
			return model;
		}
		catch(JSONException e){
			return null;
		}
	}



	@Override
	public String getNameOrPath() {
		return this.getClass().getName();
	}



}
