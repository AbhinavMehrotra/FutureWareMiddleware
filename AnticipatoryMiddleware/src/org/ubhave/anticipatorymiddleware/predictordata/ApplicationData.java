package org.ubhave.anticipatorymiddleware.predictordata;

import java.util.ArrayList;
import java.util.HashMap;

import org.ubhave.anticipatorymiddleware.utils.Constants;

public class ApplicationData extends PredictorData{

	private static final long serialVersionUID = 4483902769021354487L;

	private final int predictor_type = Constants.PREDICTOR_TYPE_APPLICATION;

	private ArrayList<PredictionResult> result;
	
	private HashMap<String,String> state_details;

	protected ApplicationData(){};

	public ApplicationData(ArrayList<PredictionResult> result){
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

	public HashMap<String,String> getAppDetails(){
		return state_details;
	}
	
	public void setAppDetails(HashMap<String,String> state_details){
		this.state_details = state_details;
	}
}
