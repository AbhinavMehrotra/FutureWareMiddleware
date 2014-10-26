
package org.ubhave.anticipatorymiddleware.predictor;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.ubhave.anticipatorymiddleware.AMException;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;

import android.content.Context;


public class Configuration  implements Serializable{

	private static final long serialVersionUID = -6111906419430445430L;
	private final SharedPreferences	sp;
	private Map<String, Object> configs;
	
	public Configuration(Context app_context){
		this.sp = new SharedPreferences(app_context);
		configs = new HashMap<String, Object>();
		configs.put(Prediction_Preiod, (long) 0);
		configs.put(Notification_Preiod, (long) 0);
		configs.put(Subscription_Lease_Preiod, (long) -1);
		configs.put(Prediction_Frequency, (long) 10);
		configs.put(Predictor_Name_Or_Path, (String) PredictorCollection.SimpleTimeSeriesPredictor);
	}
	
	public static String Prediction_Preiod = "Prediction_Preiod";
	public static String Notification_Preiod = "Notification_Preiod";
	
	/**
	 *  0 : once only,
	 * -1 : continuous
	 */
	public static String Subscription_Lease_Preiod = "Subscription_Lease_Preiod";
	public static String Prediction_Frequency = "Prediction_Frequency";
	
	public static String Predictor_Name_Or_Path = "Predictor_Name_Or_Path";
	
	public void put(String key, String value) throws AMException{
		boolean flag = false;
		PredictorCollection pc = new PredictorCollection();
		for(String predictor_name : pc.PredictorNames){
			if(predictor_name.equalsIgnoreCase(value)){
				flag = true;
			}
		}
		if(flag == false){
			try {
				@SuppressWarnings("rawtypes")
				Class c = Class.forName(value);
				@SuppressWarnings("unused")  
				PredictorModule c_instance = (PredictorModule) c.newInstance();
				configs.put(key, value);
			} catch (ClassNotFoundException e) {
				throw new AMException(AMException.INVALID_PREDICTOR, "The value does not " +
						"respond to any predictor name in PredictorCOllection. If the value " +
						"correspond to the path of a new predictor class then make sure " +
						"the path is correct and it implements PredictorInterface.\n" + e.toString());
			} catch (InstantiationException e) {
				throw new AMException(AMException.INVALID_PREDICTOR, "The value does not " +
						"respond to any predictor name in PredictorCOllection. If the value " +
						"correspond to the path of a new predictor class then make sure " +
						"the path is correct and it implements PredictorInterface.\n" + e.toString());
			} catch (IllegalAccessException e) {
				throw new AMException(AMException.INVALID_PREDICTOR, "The value does not " +
						"respond to any predictor name in PredictorCOllection. If the value " +
						"correspond to the path of a new predictor class then make sure " +
						"the path is correct and it implements PredictorInterface.\n" + e.toString());
			}		
		}
		else{
			configs.put(key, value);
		}
	}
	
	//value in minutes
	public void put(String key, Long value) throws AMException{
		if(key == Prediction_Frequency && sp.getInt("CONTEXT_SAMPLING_RATE") > value)
			throw new AMException(AMException.PREDICTION_FREQUENCY_GRANULARITY_EXCEPTION, "PREDICTION_FREQUENCY_GRANULARITY_EXCEPTION : Prediction frequency cannot be " +
					"smaller than the context sampling rate!");
		if((key == Prediction_Preiod && (Long)configs.get(Notification_Preiod) > value) || 
				(key == Notification_Preiod && (Long)configs.get(Prediction_Preiod) != 0 && (Long)configs.get(Prediction_Preiod) < value))
			throw new AMException(AMException.TIME_TRAVEL_EXCEPTION, "TIME_TRAVEL_EXCEPTION : Prediction period cannot be " +
					"smaller than the notification period!");
		if(key == Subscription_Lease_Preiod)
			value = value + Calendar.getInstance().getTimeInMillis()/(1000*60);
		configs.put(key, value); 
	}
	
	public Long getLong(String key){
		return (Long)configs.get(key);
	}
	
	public String getString(String key){
		return (String)configs.get(key);
	}
	
	public Map<String, Object> getAll(){
		return configs;
	}


}
