package org.ubhave.anticipatorymiddleware.server.predictor;

import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.server.predictordata.PredictorData;

public interface PredictorInterface{
		
	public PredictorData predictionRequest(JSONObject predictor_model, String state_to_be_predicted);
	
	public PredictorData predictionRequest(JSONObject predictor_model, String current_state, long notification_period);

}
