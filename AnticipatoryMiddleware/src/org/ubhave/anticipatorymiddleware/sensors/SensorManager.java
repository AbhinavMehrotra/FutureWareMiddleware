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
package org.ubhave.anticipatorymiddleware.sensors;

import java.util.HashSet;
import java.util.Set;

import org.ubhave.anticipatorymiddleware.alarm.AlarmMgr;
import org.ubhave.anticipatorymiddleware.communication.CommunicationManager;
import org.ubhave.anticipatorymiddleware.privacy.PrivacyManager;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;

import android.content.Context;
import android.util.Log;

public class SensorManager {


	private static final String TAG = "AnticipatoryManager";
	private final Context app_context;
	private final SharedPreferences sp;
	public final int DEFAULT_CONTEXT_SAMPLING_RATE = 10;
	public final int DEFAULT_CONTEXT_LIFE_CYCLE_TIME_PERIOD = 24*60;

	public SensorManager(Context app_context){
		this.app_context =  app_context;
		this.sp = new SharedPreferences(app_context);
	}

	public Set<Integer> getActiveSensorList(){
		Set<Integer> sensor_set = new HashSet<Integer>();
		Set<String> sensors = sp.getStringSet("ACTIVE_SENSORS");
		for(String sensor : sensors)
			sensor_set.add(Integer.parseInt(sensor));
		Log.d(TAG, "Active sensor set: "+sensor_set);
		return sensor_set;
	}


	public void addSensor(int sensor_id){
		Log.d(TAG, "New sensor: "+ sensor_id);
		Set<String> sensors = sp.getStringSet(sp.ACTIVE_SENSORS);
		sensors.add(String.valueOf(sensor_id));
		sp.addStringSet(sp.ACTIVE_SENSORS, sensors);
		checkDataCollectionAlarm(false);
	}

	public void removeSensor(int sensor_id){
		Log.d(TAG, "Sensor removed: "+ sensor_id);
		Set<String> sensors = sp.getStringSet(sp.ACTIVE_SENSORS);
		sensors.remove(String.valueOf(sensor_id));
		sp.addStringSet(sp.ACTIVE_SENSORS, sensors);
	}


	public void addSensors(Set<Integer> sensor_ids){
		Log.d(TAG, "New sensors: "+ sensor_ids);
		Set<String> sensors = sp.getStringSet(sp.ACTIVE_SENSORS);
		for(int sensor_id : sensor_ids){
			sensors.add(String.valueOf(sensor_id));
		}
		sp.addStringSet(sp.ACTIVE_SENSORS, sensors);
		checkDataCollectionAlarm(false);
	}

	public void removeSensors(Set<Integer> sensor_ids){
		Log.d(TAG, "Sensors removed: "+ sensor_ids);
		Set<String> sensors = sp.getStringSet(sp.ACTIVE_SENSORS);
		for(int sensor_id : sensor_ids){
			sensors.remove(String.valueOf(sensor_id));
		}
		sp.addStringSet(sp.ACTIVE_SENSORS, sensors);
		checkDataCollectionAlarm(false);
	}


	public void setContextSamplingRate(int minutes){
		Log.d(TAG, "Context sampling rate: "+ minutes);
		sp.add(sp.CONTEXT_SAMPLING_RATE, minutes);
		PrivacyManager pm = new PrivacyManager(this.app_context);
		if(pm.isDataTransmissionToServerEnabled()){
			CommunicationManager cm = new CommunicationManager(this.app_context);
			cm.transmitContextSamplingRate(sp.getString(sp.USER_ID), minutes);
		}
		checkDataCollectionAlarm(true);
	}

	public void setContextLifeCycleTimePeriod(int minutes){
		Log.d(TAG, "Context life cycle: "+ minutes);
		sp.add(sp.CONTEXT_LIFE_CYCLE_TIME_PERIOD, minutes);
		PrivacyManager pm = new PrivacyManager(this.app_context);
		if(pm.isDataTransmissionToServerEnabled()){
			CommunicationManager cm = new CommunicationManager(this.app_context);
			cm.transmitContextLifeCyclePeriod(sp.getString(sp.USER_ID), minutes);
		}
	}
	public int getContextSamplingRate(){
		return sp.getInt(sp.CONTEXT_SAMPLING_RATE);
	}

	public int getContextLifeCycleTimePeriod(){
		return sp.getInt(sp.CONTEXT_LIFE_CYCLE_TIME_PERIOD);
	}



	private void checkDataCollectionAlarm(boolean isSamplingRateChanged){
		AlarmMgr alarm_mgr = new AlarmMgr(app_context);
		if(isSamplingRateChanged){
			Log.d(TAG, "Sampling rate modified");
			if(sp.getBoolean(sp.SAMPLING_STARTED_FOR_SENSORS)){
				Log.d(TAG, "Removed the old alarm");
				alarm_mgr.removeAlarm(alarm_mgr.DATA_COLLECTION_ALARM_SUBSCRIPTION_ID);
				sp.add(sp.SAMPLING_STARTED_FOR_SENSORS, false);
			}
			if(getActiveSensorList().size() > 0){
				Log.d(TAG, "Data sampling alarm started");
				long csr = (sp.getInt(sp.CONTEXT_SAMPLING_RATE))*60;
				alarm_mgr.setNewAlarm(AlarmMgr.ALARM_TYPE_DATA_COLLECTION, alarm_mgr.DATA_COLLECTION_ALARM_SUBSCRIPTION_ID, csr, csr, -1, null);
				sp.add(sp.SAMPLING_STARTED_FOR_SENSORS, true);
			}
		}
		else{
			if(getActiveSensorList().size() == 0){
				Log.d(TAG, "Data sampling alarm removed");
				alarm_mgr.removeAlarm(alarm_mgr.DATA_COLLECTION_ALARM_SUBSCRIPTION_ID);
				sp.add(sp.SAMPLING_STARTED_FOR_SENSORS, false);
				return;
			}

			if(!sp.getBoolean(sp.SAMPLING_STARTED_FOR_SENSORS)){
				Log.d(TAG, "Data sampling alarm started");
				//set alarm for data sampling
				long csr = (sp.getInt(sp.CONTEXT_SAMPLING_RATE))*60;
				alarm_mgr.setNewAlarm(AlarmMgr.ALARM_TYPE_DATA_COLLECTION, alarm_mgr.DATA_COLLECTION_ALARM_SUBSCRIPTION_ID, csr, csr, -1, null);

				//set SAMPLING_STARTED_FOR_SENSORS to true
				sp.add(sp.SAMPLING_STARTED_FOR_SENSORS, true);
			}
		}
	}




}
