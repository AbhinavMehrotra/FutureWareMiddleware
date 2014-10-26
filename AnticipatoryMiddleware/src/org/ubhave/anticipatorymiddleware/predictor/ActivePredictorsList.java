package org.ubhave.anticipatorymiddleware.predictor;

import java.util.HashSet;
import java.util.Set;

import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;

import android.content.Context;

public class ActivePredictorsList {
	
	private final SharedPreferences sp;
	
	public ActivePredictorsList(Context app_context){
		this.sp = new SharedPreferences(app_context);
	}

	public Set<Integer> getActivePredictorsList(){
		Set<Integer> predictor_set = new HashSet<Integer>();
		Set<String> predictors = sp.getStringSet("ACTIVE_PREDICTORS");
		for(String predictor : predictors)
			predictor_set.add(Integer.parseInt(predictor));
		return predictor_set;
	}
	

	public void addPredictorInActivePredictorsList(int predictor_id){
		Set<String> predictors = sp.getStringSet("ACTIVE_PREDICTORS");
		predictors.add(String.valueOf(predictor_id));
		sp.addStringSet("ACTIVE_PREDICTORS", predictors);
	}
	
}
