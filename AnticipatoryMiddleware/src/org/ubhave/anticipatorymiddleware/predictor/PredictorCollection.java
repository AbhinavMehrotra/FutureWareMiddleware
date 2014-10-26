package org.ubhave.anticipatorymiddleware.predictor;

import android.util.Log;


public class PredictorCollection {

	private static final String TAG = "AnticipatoryManager";
	
	public PredictorCollection(){};

	public static String SimpleMarkovChainPredictor = "org.ubhave.anticipatorymiddleware.predictor.MarkovChainPredictor";

	public static String SimpleTimeSeriesPredictor = "org.ubhave.anticipatorymiddleware.predictor.TimeSeriesPredictor";

	public static String FirstDegreeMarkovChainPredictor = "org.ubhave.anticipatorymiddleware.predictor.FirstDegreeMarkovChainPredictor";
	
	public static String[] Predictors = new String[] {SimpleMarkovChainPredictor, SimpleTimeSeriesPredictor, FirstDegreeMarkovChainPredictor};
	
	public static String[] PredictorNames 
			= new String[] {"SimpleMarkovChainPredictor", "SimpleTimeSeriesPredictor", "FirstDegreeMarkovChainPredictor", "UserDefined"};

	public String getName(String type)
	{
		if(type.equalsIgnoreCase(SimpleMarkovChainPredictor))
			return "SimpleMarkovChainPredictor";
		else if(type.equalsIgnoreCase(SimpleTimeSeriesPredictor))
			return "SimpleTimeSeriesPredictor";
		else if(type.equalsIgnoreCase(FirstDegreeMarkovChainPredictor))
			return "FirstDegreeMarkovChainPredictor";
		else 
			return "UserDefined";
	}
	
	public PredictorModule getPredictorModule(String predictor_name_or_path){
		PredictorModule predictor_module = null;
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName(predictor_name_or_path);
			predictor_module = (PredictorModule) c.newInstance();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
//			Log.e(TAG, "Error in creating the instance of the required predictor module. MarkovChainPredictor is used for now!!");
//			predictor_module =  new MarkovChainPredictor();
		}
		return predictor_module;
	}
}
