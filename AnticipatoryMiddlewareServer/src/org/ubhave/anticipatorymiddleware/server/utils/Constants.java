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
package org.ubhave.anticipatorymiddleware.server.utils;


public class Constants {

	
	public final static int SENSOR_TYPE_ACCELEROMETER = 5001;
	public final static int SENSOR_TYPE_BLUETOOTH = 5003;
	public final static int SENSOR_TYPE_LOCATION = 5004;
	public final static int SENSOR_TYPE_MICROPHONE = 5005;
	public final static int SENSOR_TYPE_WIFI = 5010;
	
	public final static int PREDICTOR_TYPE_ACTIVITY = 15001;
	public final static int PREDICTOR_TYPE_LOCATION = 15004;
	public final static int PREDICTOR_TYPE_VOICE = 15005;
	public final static int PREDICTOR_TYPE_WIFI = 15010;
	public final static int PREDICTOR_TYPE_SOCIAL = 15011;
	
	public final static int[] ALL_PREDICTORS = new int[] { PREDICTOR_TYPE_ACTIVITY, PREDICTOR_TYPE_LOCATION,
		PREDICTOR_TYPE_VOICE, PREDICTOR_TYPE_WIFI, PREDICTOR_TYPE_SOCIAL	};
	
	public static Boolean isValidPredictor(int predictor){
		for(int  aPredictor: ALL_PREDICTORS){
			if(aPredictor == predictor)
				return true;
		}
		return false;
	}
	
	public static String getSensorName(int sensor){
		switch(sensor){
		case SENSOR_TYPE_ACCELEROMETER: return "SENSOR_TYPE_ACCELEROMETER";
		case SENSOR_TYPE_LOCATION: return "SENSOR_TYPE_LOCATION";
		default: return "UNKNOWN_SENSOR";
		}
	}
	
	public static String getPredictorName(int predictor){
		switch(predictor){
		case PREDICTOR_TYPE_ACTIVITY: return "PREDICTOR_TYPE_ACTIVITY";
		case PREDICTOR_TYPE_LOCATION: return "PREDICTOR_TYPE_LOCATION";
		default: return "UNKNOWN_PREDICTOR";
		}
	}

	public static int getSensor(int predictor){
		return predictor - 10000;
	}

	public static int getPredictor(int sensor){
		return sensor + 10000;
	}
	
}
