package org.ubhave.anticipatorymiddleware.server.predictor;


import java.util.ArrayList;

import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.server.predictordata.PredictionResult;
import org.ubhave.anticipatorymiddleware.server.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.server.time.Time;

public class Predictor{

	private static final String TAG = "AnticipatoryManager";

	private int predictor_id;

	public Predictor(int predictor_id){
		this.predictor_id = predictor_id;
	}

	public PredictorData predictionRequest(JSONObject predictor_model){
		
		return null;
	}

	/**
	 * Returns the {@link PredictorData} for the predictor.
	 * Prediction is made for the time t = current time + notification period.
	 * @param notification_period (in minutes)
	 * @return
	 */
	public PredictorData predictionRequest(long notification_period){
		PredictorData predictor_data = PredictorData.getInstance(predictor_id);		
		Time current_time = Time.getCurrentTime();
		Time notification_period_in_terms_of_time = Time.getTime(notification_period);
		Time prediction_time = Time.add(current_time, notification_period_in_terms_of_time);
		Log.d(TAG, "New prediction request for predictor: " + Constants.getPredictorName(predictor_id));
		Log.d(TAG, "Current time: " + Time.getCurrentTime());
		Log.d(TAG, "Predicting for time: " + prediction_time);

		//if notification_period == 0 then return the current context
		if(notification_period == 0){
			PredictionResult p_result = new PredictionResult();
			p_result.setPredictedState(SharedPreferences.get(SharedPreferences.LATEST_SENSOR_DATA(Constants.getSensor(predictor_id))));
			p_result.setPredictionConfidenceLevel(100);
			p_result.setPredictionProbability(100);
			ArrayList<PredictionResult> result = new ArrayList<PredictionResult>();
			result.add(p_result);
			predictor_data.setPredictedTime(Time.getCurrentTime());
			predictor_data.setResult(result);
			return predictor_data;
		}

		//a => {current time}: [(hours * 60 / csr)  + (minutes/csr)] * number_of_samples_in_a_day
		//b => {to_state_time}: (hours * 60 / csr)  + (minutes/csr)
		//index = a + b -1
		int context_sampling_rate = SharedPreferences.getInt(SharedPreferences.CONTEXT_SAMPLING_RATE);
		int number_of_samples_in_a_day = (24 * 60) / context_sampling_rate;
		int _index_a = (((current_time.get(Time.HOURS) * 60 ) + current_time.get(Time.MINUTES)) / context_sampling_rate) * number_of_samples_in_a_day;
		int _index_b = ((notification_period_in_terms_of_time.get(Time.HOURS) * 60 ) + notification_period_in_terms_of_time.get(Time.MINUTES)) / context_sampling_rate;
		int _index = _index_a + _index_b -1 ;

		try{
			//get predictor model
			String model_file_path = FilePathGenerator.generatePredictorModelFilePath(predictor_id);
			JSONObject model = FileManager.readJSONObject(model_file_path);
			JSONArray state_map = model.getJSONArray("STATE_MAP");
			JSONArray transition_matrices = model.getJSONArray("TRANSITION_MATRICES");
			JSONArray transition_matrix = transition_matrices.getJSONArray(_index);
			String current_state = SharedPreferences.get(SharedPreferences.LATEST_SENSOR_DATA(Constants.getSensor(predictor_id)));
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
				p_result.setPredictionProbability((int)mc_transition_matrix_of_probabilities[i]);
				p_result.setPredictionConfidenceLevel(95); //TODO: find confidence level
				result.add(p_result);
			}
			predictor_data.setPredictedTime(Time.getCurrentTime());
			predictor_data.setResult(result);
			return predictor_data;
		}
		catch(JSONException e){
			Log.e(TAG, "Error!! "+ e.toString());
		} catch (AMException e) {
			Log.e(TAG, "Error!! "+ e.toString());
		}
		return null;
	}

	public Boolean isReady(){
		//check if labelled data is present
		return SharedPreferences.getBoolean(SharedPreferences.LABELLED_SENSOR_DATA(Constants.getSensor(predictor_id)));	
	}

	public int getId() {
		return predictor_id;
	}


	@SuppressWarnings("unused")
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
}
