package org.ubhave.anticipatorymiddleware.predictor;

import java.util.ArrayList;

import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;

import android.content.Context;

public interface PredictorModule{

	
	/*
	 * TODO: create a method that returns the ids for the data required 
	 * according to the prediction to be made.
	 */	
//Example code for Simple Markov Chain Predictor
//	public ArrayList<Integer> idsOfRequiredData(int predictor_type){		
//		ArrayList<Integer> data_types = new ArrayList<Integer>();
//		data_types.add(Constants.getSensor(predictor_type));
//		return data_types;
//	}

	public String getNameOrPath();
	
	/**
	 * return ids of the required predictor data
	 * @param predictor_type
	 * @return predictor ids
	 */
	public ArrayList<Integer> idsOfRequiredData(int predictor_type);
	
	public JSONObject generateDataModel(Context app_context, JSONObject data, ArrayList<String> states);
	
	public PredictorData predictionRequest(int predictor_id, int context_sampling_rate, int context_life_cycle, ArrayList<JSONObject> predictor_models, String current_state, String state_to_be_predicted);

	public PredictorData predictionRequest(int predictor_id, int context_sampling_rate, int context_life_cycle, ArrayList<JSONObject> predictor_models, String current_state, long notification_period);

}
