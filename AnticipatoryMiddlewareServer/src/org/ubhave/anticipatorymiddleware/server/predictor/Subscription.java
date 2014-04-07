package org.ubhave.anticipatorymiddleware.server.predictor;

import java.util.ArrayList;
import java.util.List;

import org.ubhave.anticipatorymiddleware.AnticipatoryListener;
import org.ubhave.anticipatorymiddleware.data.PredictorData;
import org.ubhave.anticipatorymiddleware.server.filter.Filter;

import android.app.PendingIntent;



public class Subscription
{
	private final List<PredictorData> required_data; 
	private final AnticipatoryListener listener;
	private final Filter filter;
	private final Configuration configuration;
	private int id;
	private PendingIntent alarmIntent;
	
	public Subscription(List<PredictorData> required_data, AnticipatoryListener listener, 
			Filter filter, Configuration configuration)
	{
		this.required_data = required_data;
		this.listener = listener;
		this.filter = filter;
		this.configuration = configuration;
				
	}

	public List<PredictorData> getRequiredData() {
		return required_data;
	}

	public ArrayList<Integer> getPredictorListForRequiredData() {
		ArrayList<Integer> predictors = new ArrayList<Integer>();
		for(PredictorData data: required_data){
			predictors.add(data.getPredictorType());
		}
		return predictors;
	}
	
	public AnticipatoryListener getListener() {
		return listener;
	}

	public Filter getFilter() {
		return filter;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
}
