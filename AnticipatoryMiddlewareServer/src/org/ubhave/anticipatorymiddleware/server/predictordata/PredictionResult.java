package org.ubhave.anticipatorymiddleware.server.predictordata;

import java.io.Serializable;


public class PredictionResult implements Serializable{

	private static final long serialVersionUID = 4995448060127505104L;

	private String predicted_state;

	private int prediction_probability;

	private int prediction_confidence_level;

	public PredictionResult(){
		this.predicted_state = "UNKNOWN";
		this.prediction_confidence_level = 0;
		this.prediction_probability = 0;
	}

	public PredictionResult(String predicted_state, int prediction_probability, int prediction_confidence_level){
		this.predicted_state = predicted_state;
		this.prediction_confidence_level = prediction_confidence_level;
		this.prediction_probability = prediction_probability;
	}

	public String getPredictedState() {
		return predicted_state;
	}

	public void setPredictedState(String predicted_time) {
		this.predicted_state = predicted_time;
	}

	public int getPredictionProbability() {
		return prediction_probability;
	}

	public void setPredictionProbability(int prediction_probability) {
		this.prediction_probability = prediction_probability;
	}

	public int getPredictionConfidenceLevel() {
		return prediction_confidence_level;
	}

	public void setPredictionConfidenceLevel(int prediction_confidence_level) {
		this.prediction_confidence_level = prediction_confidence_level;
	}
	
	
	
}
