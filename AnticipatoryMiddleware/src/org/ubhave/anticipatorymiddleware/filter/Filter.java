package org.ubhave.anticipatorymiddleware.filter;

import java.io.Serializable;
import java.util.Set;

import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;

import android.util.Log;


public class Filter  implements Serializable{

	private static final long serialVersionUID = -1080247041978676299L;
	private static final String TAG = "AnticipatoryManager";	
	private int root_id;
	private String expression_string;
	private FilterParser parser;
	
	public Filter(String expression_string) {
		this.expression_string = expression_string;
		parser = new FilterParser(this.expression_string);
		Log.d(TAG, "Filter create: " + this.expression_string);
	}

	public int getExpressionTreeRootId(){		
		return root_id;
	}

	public Set<Integer> getRequiredPredictors(){
		return parser.getSetOfRequiredPredictors();
	}
	
	public Set<Condition> getAllConditions(){
		return parser.getSetOfConditions();
	}

	
	public String getExpressionString(){
		return expression_string;
	}
	
	public Boolean validateFilter(Set<PredictorData> predictor_data){
		return parser.validateFilter(predictor_data);
	}
	
	@Override
	public String toString(){
		return expression_string;
	}
}
