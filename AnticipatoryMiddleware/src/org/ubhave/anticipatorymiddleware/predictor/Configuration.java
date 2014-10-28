/*******************************************************************************
 *
 * FutureWare Middleware
 *
 * Copyright (c) ${2014}, University of Birmingham
 * Abhinav Mehrotra, a.mehrotra@cs.bham.ac.uk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Birmingham 
 *       nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE ABOVE COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *******************************************************************************/

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
