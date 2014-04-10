package org.ubhave.anticipatorymiddleware.server.predictor;


import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.server.predictordata.PredictorData;

public class Predictor{

	private final int predictor_id;
	private final int context_sampling_rate;
	private final int context_life_cycle;

	public Predictor(int predictor_id, int context_sampling_rate, int context_life_cycle){
		this.predictor_id = predictor_id;
		this.context_sampling_rate = context_sampling_rate;
		this.context_life_cycle = context_life_cycle;
	}

	public PredictorData predictionRequest(JSONObject predictor_model, String current_state, long notification_period){
		MarkovChainPredictor makrok_chain_predictor = new MarkovChainPredictor(predictor_id, context_sampling_rate, context_life_cycle);
		PredictorData predictor_data = makrok_chain_predictor.predictionRequest(predictor_model, current_state, notification_period);
		if(predictor_data == null){
			System.out.println("MarkovChainPredictor returned null value!");
		}		
		return predictor_data;
	}


	public PredictorData predictionRequest(JSONObject predictor_model, String state_to_be_predicted){
		MarkovChainPredictor makrok_chain_predictor = new MarkovChainPredictor(predictor_id, context_sampling_rate, context_life_cycle);
		PredictorData predictor_data = makrok_chain_predictor.predictionRequest(predictor_model, state_to_be_predicted);
		if(predictor_data == null){
			System.out.println("MarkovChainPredictor returned null value!");
		}		
		return predictor_data;
	}


	public int getId() {
		return predictor_id;
	}


}
