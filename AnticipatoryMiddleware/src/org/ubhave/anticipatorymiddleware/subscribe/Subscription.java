package org.ubhave.anticipatorymiddleware.subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ubhave.anticipatorymiddleware.AnticipatoryListener;
import org.ubhave.anticipatorymiddleware.filter.Filter;
import org.ubhave.anticipatorymiddleware.predictor.Configuration;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.utils.Constants;



public class Subscription implements Serializable
{

	private static final long serialVersionUID = 2719548867625835020L;
	private final List<PredictorData> required_data; 
//	private final AnticipatoryListener listener;
	private final String listener_canonical_name;
	private final ClassReferenceHelper class_reference;
	private final Filter filter;
	private final Configuration configuration;
	private int id;
	
	public Subscription(List<PredictorData> required_data, AnticipatoryListener listener, 
			Filter filter, Configuration configuration)
	{
		this.required_data = required_data;
		class_reference = new ClassReferenceHelper();
		this.listener_canonical_name = class_reference.getClassCanonicalName(listener);
		this.filter = filter;
		this.configuration = configuration;
				
	}

	public List<PredictorData> getRequiredData() {
		return required_data;
	}

	public ArrayList<Integer> getPredictorListForRequiredData() {
		ArrayList<Integer> predictors = new ArrayList<Integer>();
		for(PredictorData data: required_data){
			if(data.getPredictorType() == Constants.PREDICTOR_TYPE_SOCIAL){
				continue; 
			}
			predictors.add(data.getPredictorType());
		}
		return predictors;
	}
	
	public AnticipatoryListener getListener() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		AnticipatoryListener listener = (AnticipatoryListener) class_reference.getClassReference(listener_canonical_name);
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
