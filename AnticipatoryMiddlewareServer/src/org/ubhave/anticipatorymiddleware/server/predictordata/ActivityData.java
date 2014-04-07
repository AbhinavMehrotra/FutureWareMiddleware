package org.ubhave.anticipatorymiddleware.server.predictordata;

import java.util.ArrayList;

import org.ubhave.anticipatorymiddleware.server.Constants;
import org.ubhave.anticipatorymiddleware.server.time.Time;

public class ActivityData  extends PredictorData{


	
	private static final long serialVersionUID = -6659364256202535088L;

	private final int predictor_type = Constants.PREDICTOR_TYPE_ACTIVITY;

	private Time predicted_time;

	private ArrayList<PredictionResult> result;

	protected ActivityData(){};

	public ActivityData(Time predicted_time, ArrayList<PredictionResult> result){
		this.predicted_time = predicted_time;
		this.result = result;
	}

	@Override
	public int getPredictorType() {
		return this.predictor_type;
	}

	@Override
	public Time getPredictedTime() {
		return this.predicted_time;
	}
	@Override
	public void setPredictedTime(Time time) {
		this.predicted_time = time;
	}

	@Override
	public PredictionResult getMostProbableResult() {
		PredictionResult most_probable_result = new PredictionResult();
		int max_probability = 0;
		for(PredictionResult pr : result){
			if(pr.getPredictionProbability() > max_probability){
				max_probability = pr.getPredictionProbability();
				most_probable_result = pr;
			}
		}
		return most_probable_result;
	}

	@Override
	public ArrayList<PredictionResult> getResult() {
		return result;
	}

	@Override
	public void setResult(ArrayList<PredictionResult> result) {
		this.result = result;
	}
	
}
