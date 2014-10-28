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
package org.ubhave.anticipatorymiddleware.classifiers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.util.Log;

import com.ubhave.sensormanager.data.SensorData;

public class SensorDataClassifier {

	private static final String TAG = "AnticipatoryManager";
	
	private final int sensor_type;
	
	private final Classifier classifier;
	
	public SensorDataClassifier(Context context, int sensor_type){
		this.sensor_type = sensor_type;
		switch (this.sensor_type){
		case Constants.SENSOR_TYPE_ACCELEROMETER :
			classifier = new AccelerometerClassifier(context);
			break;
		case Constants.SENSOR_TYPE_APPLICATION :
			classifier = new ApplicationClassifier(context);
			break;
		case Constants.SENSOR_TYPE_BLUETOOTH :
			classifier = new BluetoothClassifier(context);
			break;
		case Constants.SENSOR_TYPE_CALL_LOGS :
			classifier = new CallLogsClassifier(context);
			break;
		case Constants.SENSOR_TYPE_LOCATION :
			classifier = new LocationClassifier(context);
			break;
		case Constants.SENSOR_TYPE_MICROPHONE :
			classifier = new MircophoneClassifier(context);
			break;
		case Constants.SENSOR_TYPE_SMS_LOGS :
			classifier = new SMSLogsClassifier(context);
			break;
		case Constants.SENSOR_TYPE_WIFI :
			classifier = new WiFiClassifier(context);
			break;
		default :
			classifier = null;
			Log.e(TAG, "SensorDataClassifier sensor: " + sensor_type + " not recognised.");
		}
	}
	
	public void trainClassifier(JSONArray array){
		classifier.trainClassifier(array);
	}
	
	public ArrayList<String> getAllLabels(){			
		return classifier.getAllLabels();
	}

	public String classifySensorData(SensorData sensorData){
		String classified_sensor_data = classifier.classifySensorData(sensorData);		
		Log.d(TAG, "SensorDataClassifier classified_sensor_data: " + classified_sensor_data);
		return classified_sensor_data;
	}



}
