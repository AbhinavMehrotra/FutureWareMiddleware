package org.ubhave.anticipatorymiddleware.predictordata;

import java.util.ArrayList;

import org.ubhave.anticipatorymiddleware.utils.Constants;

public class SMSData extends PredictorData{

	private static final long serialVersionUID = -285988658633461344L;

	private final int predictor_type = Constants.PREDICTOR_TYPE_SMS_LOGS;

	private ArrayList<PredictionResult> result;

	protected SMSData(){};
	
	public SMSData(ArrayList<PredictionResult> result)
	{
		this.result = result;
	}
	
	@Override
	public int getPredictorType() 
	{
		return predictor_type;
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
