
package org.ubhave.anticipatorymiddleware.server.predictor;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.ubhave.anticipatorymiddleware.AMException;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;

import android.util.AndroidException;


public class Configuration {

	Map<String, Long> configs;
	
	public Configuration(){
		configs = new HashMap<String, Long>();
		configs.put(Prediction_Preiod, (long) 0);
		configs.put(Notification_Preiod, (long) 0);
		configs.put(Subscription_Lease_Preiod, (long) -1);
		configs.put(Prediction_Frequency, (long) 10);
	}
	
	public static String Prediction_Preiod = "Prediction_Preiod";
	public static String Notification_Preiod = "Notification_Preiod";
	
	/**
	 *  0 : once only,
	 * -1 : continuous
	 */
	public static String Subscription_Lease_Preiod = "Subscription_Lease_Preiod";
	public static String Prediction_Frequency = "Prediction_Frequency";
	
	//value in minutes
	public void put(String key, Long value) throws AMException{
		if(key == Prediction_Frequency && SharedPreferences.getInt("CONTEXT_SAMPLING_RATE") > value)
			throw new AMException(AMException.PREDICTION_FREQUENCY_GRANULARITY_EXCEPTION, "PREDICTION_FREQUENCY_GRANULARITY_EXCEPTION : Prediction frequency cannot be " +
					"smaller than the context sampling rate!");
		if((key == Prediction_Preiod && configs.get(Notification_Preiod) > value) || 
				(key == Notification_Preiod && configs.get(Prediction_Preiod) < value))
			throw new AMException(AMException.TIME_TRAVEL_EXCEPTION, "TIME_TRAVEL_EXCEPTION : Prediction period cannot be " +
					"smaller than the notification period!");
		if(key == Subscription_Lease_Preiod)
			value = value + Calendar.getInstance().getTimeInMillis()/(1000*60);
		configs.put(key, value); 
	}
	
	public Long get(String key){
		return configs.get(key);
	}
	
	public Map<String, Long> getAll(){
		return configs;
	}
	
}
