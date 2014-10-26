package org.ubhave.anticipatorymiddleware.predictor;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.sensors.SensorManager;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.util.Log;

public class Predictor{

	private static final String TAG = "AnticipatoryManager";

	private int predictor_id;

	private final SharedPreferences sp;
	
	private final int context_sampling_rate;
	
	private final int context_life_cycle;
	
	private PredictorModule predictor_module;
	
	private final Context app_context;
	
	public Predictor(Context app_context, int predictor_id, String predictor_name_or_path){
		this.app_context = app_context;
		this.predictor_id = predictor_id;
		this.sp = new SharedPreferences(app_context);
		SensorManager sm = new SensorManager(app_context);
		context_sampling_rate = sm.getContextSamplingRate();
		context_life_cycle = sm.getContextLifeCycleTimePeriod();
		PredictorCollection pc = new PredictorCollection();
		predictor_module = pc.getPredictorModule(predictor_name_or_path);
	}


	public PredictorData predictionRequest(ArrayList<JSONObject> predictor_models, String current_state, long notification_period){
		Log.i(TAG, "Predictor id: "+predictor_id);
		Log.i(TAG, "Context_sampling_rate: "+context_sampling_rate);
		Log.i(TAG, "Context_life_cycle: "+context_life_cycle);
		Log.i(TAG, "Number of predictor_models obtained: "+predictor_models.size());
//		Log.i(TAG, "Models obtained: "+predictor_models);
		Log.i(TAG, "current_state: "+current_state);
		Log.i(TAG, "notification_period: "+notification_period);
		Log.i(TAG, "Making a prediction with PredictorModule: "+predictor_module.getNameOrPath());
		
		PredictorData predictor_data = predictor_module.predictionRequest(predictor_id, context_sampling_rate, context_life_cycle, predictor_models, current_state, notification_period);
		if(predictor_data == null)
		{
			Log.e(TAG, "PredictorModule("+predictor_module.getNameOrPath()+") returned null value!");
		}		
		return predictor_data;
	}


	public PredictorData predictionRequest(ArrayList<JSONObject> predictor_models, String current_state, String state_to_be_predicted){
		Log.i(TAG, "Predictor id: "+predictor_id);
		Log.i(TAG, "Context_sampling_rate: "+context_sampling_rate);
		Log.i(TAG, "Context_life_cycle: "+context_life_cycle);
		Log.i(TAG, "predictor_models: "+predictor_models);
		Log.i(TAG, "current_state: "+current_state);
		Log.i(TAG, "state_to_be_predicted: "+state_to_be_predicted);
		Log.i(TAG, "Making a prediction with PredictorModule: "+predictor_module.getNameOrPath());
		PredictorData predictor_data = predictor_module.predictionRequest(predictor_id, context_sampling_rate, context_life_cycle, predictor_models, current_state, state_to_be_predicted);
		if(predictor_data == null){
			Log.e(TAG, "MarkovChainPredictor returned null value!");
		}		
		return predictor_data;
	}

	public JSONObject generateDataModel(JSONObject data, ArrayList<String> states) {
		JSONObject model = predictor_module.generateDataModel(app_context, data, states);
		//Log.i(TAG, "New model: "+model.toString());;
		return model;
	}

	public ArrayList<Integer> idsOfRequiredData(int predictor_type){
		return predictor_module.idsOfRequiredData(predictor_type);
	}
	
	/**
	 * Returns the {@link PredictorData} for the predictor.
	 * Prediction is made for the time t = current time + notification period.
	 * @param notification_period (in minutes)
	 * @return
	 */
//	public PredictorData predictionRequest(long notification_period){
//		PredictorData predictor_data = PredictorData.getInstance(predictor_id);		
//		Time current_time = Time.getCurrentTime();
//		Time notification_period_in_terms_of_time = Time.getTime(notification_period);
//		Time prediction_time = Time.add(current_time, notification_period_in_terms_of_time);
//		Log.d(TAG, "New prediction request for predictor: " + Constants.getPredictorName(predictor_id));
//		Log.d(TAG, "Current time: " + Time.getCurrentTime());
//		Log.d(TAG, "Predicting for time: " + prediction_time);
//
//		//if notification_period == 0 then return the current context
//		if(notification_period == 0){
//			PredictionResult p_result = new PredictionResult();
//			p_result.setPredictedState(sp.get(sp.LATEST_SENSOR_DATA(Constants.getSensor(predictor_id))));
//			p_result.setPredictionConfidenceLevel(100);
//			p_result.setPredictionProbability(100);
//			ArrayList<PredictionResult> result = new ArrayList<PredictionResult>();
//			result.add(p_result);
//			predictor_data.setPredictedTime(Time.getCurrentTime());
//			predictor_data.setResult(result);
//			return predictor_data;
//		}
//
//		//a => {current time}: [(hours * 60 / csr)  + (minutes/csr)] * number_of_samples_in_a_day
//		//b => {to_state_time}: (hours * 60 / csr)  + (minutes/csr)
//		//index = a + b -1
//		int context_sampling_rate = sp.getInt(sp.CONTEXT_SAMPLING_RATE);
//		int context_life_cycle = sp.getInt(sp.CONTEXT_LIFE_CYCLE_TIME_PERIOD);
//		int number_of_samples_in_a_day = context_life_cycle / context_sampling_rate;
//		int _index_a = (((current_time.get(Time.HOURS) * 60 ) + current_time.get(Time.MINUTES)) / context_sampling_rate) * number_of_samples_in_a_day;
//		int _index_b = ((notification_period_in_terms_of_time.get(Time.HOURS) * 60 ) + notification_period_in_terms_of_time.get(Time.MINUTES)) / context_sampling_rate;
//		int _index = _index_a + _index_b -1 ;
//
//		try{
//			//get predictor model
//			String model_file_path = new FilePathGenerator().generatePredictorModelDataFilePath(predictor_id);
//			JSONObject model = new FileManager().readJSONObject(model_file_path);
//			JSONArray state_map = model.getJSONArray("STATE_MAP");
//			JSONArray transition_matrices = model.getJSONArray("TRANSITION_MATRICES");
//			JSONArray transition_matrix = transition_matrices.getJSONArray(_index);
//			String current_state = sp.get(sp.LATEST_SENSOR_DATA(Constants.getSensor(predictor_id)));
//			ArrayList<Integer> indices = indicesOfStateTransitionWithFromState(state_map, current_state);
//			//create probabilities matrix	
//			int number_of_states = (int) Math.sqrt(transition_matrix.length());
//			double[] mc_transition_matrix_of_probabilities = new double[number_of_states * number_of_states];
//			double sum_of_row;
//			for(int i=0; i<number_of_states*number_of_states; i=i+number_of_states){
//				sum_of_row = 0;
//				for(int j=i; j<i+number_of_states; j++){
//					sum_of_row += transition_matrix.getDouble(j);
//				}
//				for(int j=i; j<i+number_of_states; j++){
//					mc_transition_matrix_of_probabilities[j] = transition_matrix.getDouble(j) / sum_of_row;
//				}
//			}
//
//			//get probability of each possible state and the state name
//			ArrayList<PredictionResult> result = new ArrayList<PredictionResult>();
//			PredictionResult p_result;
//			for(int i :indices){
//				p_result = new PredictionResult();
//				p_result.setPredictedState(state_map.getJSONObject(i).getString("TO"));
//				p_result.setPredictionProbability((int)mc_transition_matrix_of_probabilities[i]);
//				p_result.setPredictionConfidenceLevel(95); //TODO: find confidence level
//				result.add(p_result);
//			}
//			predictor_data.setPredictedTime(Time.getCurrentTime());
//			predictor_data.setResult(result);
//			return predictor_data;
//		}
//		catch(JSONException e){
//			Log.e(TAG, "Error!! "+ e.toString());
//		} catch (AMException e) {
//			Log.e(TAG, "Error!! "+ e.toString());
//		}
//		return null;
//	}

	public Boolean isReady(int subscription_id){
		//check if labelled data is present
		if(predictor_id == Constants.PREDICTOR_TYPE_SOCIAL){
			return sp.getBoolean(sp.SOCIAL_PREDICTOR_STATUS(subscription_id));
		}
		return sp.getBoolean(sp.LABELLED_SENSOR_DATA(Constants.getSensor(predictor_id)));	
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

	@SuppressWarnings("unused")
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
