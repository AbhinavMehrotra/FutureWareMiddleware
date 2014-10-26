package org.ubhave.anticipatorymiddleware.predictor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.communication.JSONKeys;
import org.ubhave.anticipatorymiddleware.predictordata.PredictionResult;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.sensors.SensorManager;
import org.ubhave.anticipatorymiddleware.time.Time;

import android.content.Context;
import android.util.Log;

public class MarkovChainPredictor implements PredictorModule{

	private static final String TAG = "AnticipatoryManager";

	public MarkovChainPredictor(){};

	@Override
	public PredictorData predictionRequest(int predictor_id, int context_sampling_rate, int context_life_cycle, 
			ArrayList<JSONObject> predictor_models, String current_state, String state_to_be_predicted){		

		PredictorData predictor_data = PredictorData.getInstance(predictor_id);	

		//use 1st from state and rest {1 to (number_of_samples_in_a_day-1)} from state

		int number_of_samples_in_a_day = context_life_cycle / context_sampling_rate;

		try{
			JSONArray state_map = predictor_models.get(0).getJSONArray("STATE_MAP");
			JSONArray transition_matrices = predictor_models.get(0).getJSONArray("TRANSITION_MATRICES");
			//to and from indices in the state map that contain the "state to be predicted"
			int[] to_indeces = new int[(int) Math.sqrt(state_map.length())];
			int[] from_indeces = new int[(int) Math.sqrt(state_map.length())];			
			int to_count = 0;
			int from_count = 0;
			for(int i=0; i<state_map.length(); i++){
				if(state_map.getJSONObject(i).getString(JSONKeys.STATE_MAP_FROM).equalsIgnoreCase(state_to_be_predicted)){
					from_indeces[from_count++] = i;
				}
				if(state_map.getJSONObject(i).getString(JSONKeys.STATE_MAP_TO).equalsIgnoreCase(state_to_be_predicted)){
					to_indeces[to_count++] = i;
				}
			}
			//provides the total number of context life cycle by this time instance
			int total_counts_for_a_time_instance = 0;

			//this will contain total number of time states found at each time interval
			int[] counter_of_state_found = new int[number_of_samples_in_a_day];
			for(int i=0; i<number_of_samples_in_a_day; i++){
				JSONArray transition_matrix = transition_matrices.getJSONArray(i);
				if(i==0){
					for(int j=0; j<transition_matrix.length(); j++){
						total_counts_for_a_time_instance += transition_matrix.getInt(j);
					}

					//use the from state
					for(int j=0; j<from_indeces.length; j++){
						int index = from_indeces[j];
						counter_of_state_found[i] += transition_matrix.getInt(index);
					}
				}
				//use to states
				for(int j=0; j<to_indeces.length; j++){
					int index = to_indeces[j];
					counter_of_state_found[i+1] += transition_matrix.getInt(index);
				}				
			}


			//get probability of each possible state and the state name
			ArrayList<PredictionResult> result = new ArrayList<PredictionResult>();

			int total_counts_of_state_found = 0;
			for(int value :counter_of_state_found){
				total_counts_of_state_found += value;
			}
			Set<Time> predicted_time = new HashSet<Time>();
			for(int i=0; i<counter_of_state_found.length; i++){
				int time_in_minutes = i*context_sampling_rate;
				Time time = Time.getTime(time_in_minutes);
				if(counter_of_state_found[i] > total_counts_of_state_found/(counter_of_state_found.length*2)){ //TODO:  will 1/2 mean work here??
					predicted_time.add(time);
				}
			}
			int count=0;
			for(Time time : predicted_time){
				PredictionResult p_result = new PredictionResult();			
				p_result.setPredictedState(state_to_be_predicted);	
				p_result.setPredictedTime(time);
				int probability = 100*counter_of_state_found[count++]/total_counts_for_a_time_instance;
				p_result.setPredictionProbability(probability);
				p_result.setPredictionConfidenceLevel(95); //TODO: find confidence level
				result.add(p_result);
			}
			predictor_data.setResult(result);
			return predictor_data;
		}
		catch(JSONException e){
			System.out.println("Error!! "+ e.toString());
		} 

		return predictor_data;
	}

	@Override
	public PredictorData predictionRequest(int predictor_id, int context_sampling_rate, int context_life_cycle, ArrayList<JSONObject> predictor_models, String current_state, long notification_period){
		PredictorData predictor_data = PredictorData.getInstance(predictor_id);
		Time current_time = Time.getCurrentTime();
		Time notification_period_in_terms_of_time = Time.getTime(notification_period);	


		//if notification_period == 0 then return the current context
		if(notification_period == 0){
			PredictionResult p_result = new PredictionResult();
			p_result.setPredictedState(current_state);			
			Set<Time> predicted_time = new HashSet<Time>();
			predicted_time.add(notification_period_in_terms_of_time);
			p_result.setPredictedTime(notification_period_in_terms_of_time);
			//			p_result.setPredictedTime(predicted_time);
			p_result.setPredictionConfidenceLevel(100);
			p_result.setPredictionProbability(100);
			ArrayList<PredictionResult> result = new ArrayList<PredictionResult>();
			result.add(p_result);
			predictor_data.setResult(result);
			return predictor_data;
		}

		//a => {current time}: [(hours * 60 / csr)  + (minutes/csr)] * number_of_samples_in_a_day
		//b => {to_state_time}: (hours * 60 / csr)  + (minutes/csr)
		//index = a + b -1
		int number_of_samples_in_a_day = context_life_cycle / context_sampling_rate;
		int _index_a = (((current_time.get(Time.HOURS) * 60 ) + current_time.get(Time.MINUTES)) / context_sampling_rate) * number_of_samples_in_a_day;
		int _index_b = ((notification_period_in_terms_of_time.get(Time.HOURS) * 60 ) + notification_period_in_terms_of_time.get(Time.MINUTES)) / context_sampling_rate;
		int _index = _index_a + _index_b -1 ;

		try{
			JSONArray state_map = predictor_models.get(0).getJSONArray("STATE_MAP");
			JSONArray transition_matrices = predictor_models.get(0).getJSONArray("TRANSITION_MATRICES");
			JSONArray transition_matrix = transition_matrices.getJSONArray(_index);
			ArrayList<Integer> indices = indicesOfStateTransitionWithFromState(state_map, current_state);
			//create probabilities matrix	
			int number_of_states = (int) Math.sqrt(transition_matrix.length());
			double[] mc_transition_matrix_of_probabilities = new double[number_of_states * number_of_states];
			double sum_of_row;
			for(int i=0; i<number_of_states*number_of_states; i=i+number_of_states){
				sum_of_row = 0;
				for(int j=i; j<i+number_of_states; j++){
					sum_of_row += transition_matrix.getDouble(j);
				}
				for(int j=i; j<i+number_of_states; j++){
					mc_transition_matrix_of_probabilities[j] = transition_matrix.getDouble(j) / sum_of_row;
				}
			}

			//get probability of each possible state and the state name
			ArrayList<PredictionResult> result = new ArrayList<PredictionResult>();
			PredictionResult p_result;
			for(int i :indices){
				p_result = new PredictionResult();
				p_result.setPredictedState(state_map.getJSONObject(i).getString("TO"));		
				Set<Time> predicted_time = new HashSet<Time>();
				predicted_time.add(notification_period_in_terms_of_time);
				p_result.setPredictionProbability((int)mc_transition_matrix_of_probabilities[i]);
				p_result.setPredictionConfidenceLevel(95); //TODO: find confidence level
				result.add(p_result);
			}
			predictor_data.setResult(result);
			return predictor_data;
		}
		catch(JSONException e){
			System.out.println("Error!! "+ e.toString());
		} 
		return null;
	}

	private int indexOfStateTransition(JSONArray state_map, String from_state, String to_state) throws JSONException{
		for(int i=0 ; i<state_map.length(); i++){
			JSONObject object = state_map.getJSONObject(i);
			if(object.getString("FROM").equalsIgnoreCase(from_state) && object.getString("TO").equalsIgnoreCase(to_state)){
				return i;
			}
		}
		return -1; //not found
	}

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

	@SuppressWarnings("unused")
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


	@Override
	public ArrayList<Integer> idsOfRequiredData(int predictor_type){		
		ArrayList<Integer> data_types = new ArrayList<Integer>();
		data_types.add(predictor_type);
		return data_types;
	}




	@Override
	public JSONObject generateDataModel(Context app_context, JSONObject data, ArrayList<String> states) {
		/**TODO: 
		 * 1. create state map
		 * 2. create transition data
		 */
		try{
			JSONObject model = new JSONObject();
			int number_of_states = states.size();

			SensorManager sm = new SensorManager(app_context);
			int context_sampling_rate = sm.getContextSamplingRate();
			int context_life_cycle = sm.getContextLifeCycleTimePeriod();
			int number_of_samples_in_a_day = context_life_cycle / context_sampling_rate;
			// total number of transition matrices
			int transition_matrices_required = number_of_samples_in_a_day * (number_of_samples_in_a_day-1);

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

						if(state_map_index < 0){
							Log.e(TAG,"state_map_index is negative!!");
							continue;
						}

						int value = matrix.getInt(state_map_index);
						value = value + 1;
						matrix.put(state_map_index, value);		
						transition_matrices.put(_index, matrix);
					}			
				}
			}
			else{
				Log.e(TAG,"Data null: "+data);
			}
			Log.d(TAG,"transition matrices created");
			model.put("TRANSITION_MATRICES", transition_matrices);	
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
