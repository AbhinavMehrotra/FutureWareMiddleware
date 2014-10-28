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
package org.ubhave.anticipatorymiddleware.filters.variables;

import org.ubhave.anticipatorymiddleware.utils.Constants;


public class UserContext {

	public static final String LOCATION = "LOCATION";
	public static final String ACTIVITY = "ACTIVITY";
	public static final String SOCIAL = "SOCIAL";

	public static ActivityVariable ACTIVITY_ATTRIBUTES;
	public static LocationVariable LOCATION_ATTRIBUTES;
	public static SocialVariable SOCIAL_ATTRIBUTES;

	public static Boolean isValidContextType(String condition_variable){
		if(getContextTypeByConditionVariable(condition_variable) != "UNKNOWN")
			return true;
		return false;
	}
	
	private static String getContextTypeByConditionVariable(String condition_variable){
		if(condition_variable.equalsIgnoreCase(LOCATION))
			return LOCATION;
		else if(condition_variable.equalsIgnoreCase(ACTIVITY))
			return ACTIVITY;
		else if(condition_variable.startsWith("friend_ids_")) //see social variable
			return SOCIAL;
		else 
			return "UNKNOWN";
	}
	
	public static int getSensorIdByConditionVariable(String context_varaible){
		if(context_varaible.equalsIgnoreCase(LOCATION))
			return Constants.SENSOR_TYPE_LOCATION;
		else if(context_varaible.equalsIgnoreCase(ACTIVITY))
			return Constants.SENSOR_TYPE_ACCELEROMETER;
		else 
			return -1;
	}
	
	public static int getPredictorIdByConditionVariable(String context_variable){
		if(context_variable.equalsIgnoreCase(LOCATION))
			return Constants.PREDICTOR_TYPE_LOCATION;
		else if(context_variable.equalsIgnoreCase(ACTIVITY))
			return Constants.PREDICTOR_TYPE_ACTIVITY;
		else if(context_variable.startsWith("friend_ids_"))
			return Constants.PREDICTOR_TYPE_SOCIAL;
		else 
			return -1;
	}
}
