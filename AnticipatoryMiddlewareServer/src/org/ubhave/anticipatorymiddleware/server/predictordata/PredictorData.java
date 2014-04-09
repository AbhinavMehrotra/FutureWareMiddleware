package org.ubhave.anticipatorymiddleware.server.predictordata;

import java.io.Serializable;
import java.util.ArrayList;

import org.ubhave.anticipatorymiddleware.server.Constants;


public abstract class PredictorData implements Serializable{

	private static final long serialVersionUID = -3095404128053961508L;
	public final static PredictorData ACTIVITY = new ActivityData();
	public final static PredictorData LOCATION = new LocationData();
	public final static PredictorData SOCIAL = new SocialData();


	public abstract int getPredictorType();

	public abstract PredictionResult getMostProbableResult();

	public abstract ArrayList<PredictionResult> getResult();

	public abstract void setResult(ArrayList<PredictionResult> result);	

	public static PredictorData getInstance(int predictor_type){
		switch (predictor_type){
		case Constants.PREDICTOR_TYPE_ACTIVITY :
			return ACTIVITY;

		case Constants.PREDICTOR_TYPE_LOCATION :
			return LOCATION;

		case Constants.PREDICTOR_TYPE_SOCIAL :
			return SOCIAL;
		default: 
			return null;
		}
	}


}
