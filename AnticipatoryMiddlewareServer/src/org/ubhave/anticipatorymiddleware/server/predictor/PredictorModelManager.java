package org.ubhave.anticipatorymiddleware.server.predictor;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.classifiers.SensorDataClassifier;
import org.ubhave.anticipatorymiddleware.server.time.Time;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.utils.Constants;

public class PredictorModelManager {


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
	public static JSONObject createModelFromData(int predictor_type, JSONObject data) throws JSONException{
		/**TODO: 
		 * 1. create state map
		 * 2. create transition data
		 */
		JSONObject model = new JSONObject();

		int context_sampling_rate = SharedPreferences.getInt(SharedPreferences.CONTEXT_SAMPLING_RATE);
		int number_of_samples_in_a_day = (24 * 60) / context_sampling_rate;
		// total number of transition matrices
		int transition_matrices_required = number_of_samples_in_a_day * number_of_samples_in_a_day;

		SensorDataClassifier classifier = new SensorDataClassifier(Constants.getSensor(predictor_type));
		ArrayList<String> states = classifier.getAllLabels();
		int number_of_states = states.size();

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

		JSONArray transition_matrices = new JSONArray();
		JSONArray transition_matrix = new JSONArray();
		//initialise the matrix
		for(int i=0; i<state_map.length(); i++){
			transition_matrix.put(0);
		}
		//initialise all transition matrices
		for(int i=0; i<transition_matrices_required; i++){
			transition_matrices.put(transition_matrix);
		}


		if(data!=null){
			JSONArray data_array = data.getJSONArray("DATA");
			JSONObject from_state;
			JSONObject to_state;
			//create transition matrices
			for(int i =0; i<data_array.length(); i++){
				from_state = (JSONObject) data_array.get(i);
				String from_state_value = from_state.getString("STATE");
				Time from_time = Time.stringToTime(from_state.getString("TIME"));
				for(int j=1; j< (i+number_of_samples_in_a_day); j++){
					to_state = (JSONObject) data_array.get(j);
					String to_state_value = to_state.getString("STATE");
					Time to_time = Time.stringToTime(to_state.getString("TIME"));

					//a => {from_state time}: [(hours * 60 / csr)  + (minutes/csr)] * number_of_samples_in_a_day
					//b => {to_state_time}: (hours * 60 / csr)  + (minutes/csr)
					//index = a + b -1
					int _index_a = (((from_time.get(Time.HOURS) * 60 ) + from_time.get(Time.MINUTES)) / context_sampling_rate) * number_of_samples_in_a_day;
					int _index_b = ((to_time.get(Time.HOURS) * 60 ) + to_time.get(Time.MINUTES)) / context_sampling_rate;
					int _index = _index_a + _index_b -1 ;

					//get the transition matrix
					JSONArray matrix = transition_matrices.getJSONArray(_index);

					//get index of state_transition from state_map and increase the value by 1
					int state_map_index = indexOfStateTransition(state_map, from_state_value, to_state_value);
					int value = matrix.getInt(state_map_index);
					value = value + 1;
					matrix.put(state_map_index, value);				
				}			
			}
		}
		model.put("TRANSITION_MATRICES", transition_matrices);		
		return model;
	}


	public static JSONObject mergeModels(JSONObject model_a, JSONObject model_b, int predictor_type) throws JSONException{
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
	public static JSONObject modifyExistingModelWithNewData(JSONObject model, JSONObject data, int predictor_type) throws JSONException{
		//TODO: update existing model

		JSONArray state_map =  model.getJSONArray("STATE_MAP");
		JSONArray transition_matrices = model.getJSONArray("TRANSITION_MATRICES");
		JSONArray transition_matrix;

		String to_state_value = data.getString("STATE");
		Time to_state_time = Time.stringToTime(data.getString("TIME"));

		ArrayList<Integer> modification_indices = indicesOfStateTransitionWithToState(state_map, to_state_value);
		int context_sampling_rate = SharedPreferences.getInt(SharedPreferences.CONTEXT_SAMPLING_RATE);
		int number_of_samples_in_a_day = (24 * 60) / context_sampling_rate;		
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
