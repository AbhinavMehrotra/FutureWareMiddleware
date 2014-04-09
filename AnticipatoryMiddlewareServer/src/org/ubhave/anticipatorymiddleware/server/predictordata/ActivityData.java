package org.ubhave.anticipatorymiddleware.server.predictordata;

import java.util.ArrayList;

import org.ubhave.anticipatorymiddleware.server.utils.Constants;

public class ActivityData  extends PredictorData{


	
	private static final long serialVersionUID = -6659364256202535088L;

	private final int predictor_type = Constants.PREDICTOR_TYPE_ACTIVITY;

	private ArrayList<PredictionResult> result;

	protected ActivityData(){};

	public ActivityData(ArrayList<PredictionResult> result){
		this.result = result;
	}

	@Override
	public int getPredictorType() {
		return this.predictor_type;
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
