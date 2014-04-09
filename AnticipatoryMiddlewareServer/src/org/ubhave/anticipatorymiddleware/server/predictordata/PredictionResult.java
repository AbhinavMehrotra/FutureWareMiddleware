package org.ubhave.anticipatorymiddleware.server.predictordata;

import java.io.Serializable;

import org.ubhave.anticipatorymiddleware.server.time.Time;


public class PredictionResult implements Serializable{

	private static final long serialVersionUID = 4995448060127505104L;

	private String predicted_state;

	private Time predicted_time;

//	private Set<Time> predicted_time;

	private int prediction_probability;

	private int prediction_confidence_level;

	public PredictionResult(){
		this.predicted_state = "UNKNOWN";
		this.predicted_time = Time.getCurrentTime();
		this.prediction_confidence_level = 0;
		this.prediction_probability = 0;
	}

	public PredictionResult(String predicted_state, Time predicted_time, int prediction_probability, int prediction_confidence_level){
		this.predicted_state = predicted_state;
		this.predicted_time = predicted_time;
		this.prediction_confidence_level = prediction_confidence_level;
		this.prediction_probability = prediction_probability;
	}

//	public PredictionResult(){
//		this.predicted_state = "UNKNOWN";
//		this.predicted_time = new HashSet<Time>();
//		this.prediction_confidence_level = 0;
//		this.prediction_probability = 0;
//	}
//
//	public PredictionResult(String predicted_state, Set<Time> predicted_time, int prediction_probability, int prediction_confidence_level){
//		this.predicted_state = predicted_state;
//		this.predicted_time = predicted_time;
//		this.prediction_confidence_level = prediction_confidence_level;
//		this.prediction_probability = prediction_probability;
//	}

	public String getPredictedState() {
		return predicted_state;
	}

	public void setPredictedState(String predicted_state) {
		this.predicted_state = predicted_state;
	}

	public Time getPredictedTime() {
		return predicted_time;
	}

	public void setPredictedTime(Time predicted_time) {
		this.predicted_time = predicted_time;
	}

//	public Set<Time> getPredictedTime() {
//		return predicted_time;
//	}
//
//	public void setPredictedTime(Set<Time> predicted_time) {
//		this.predicted_time = predicted_time;
//	}

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
