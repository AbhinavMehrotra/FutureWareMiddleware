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
package org.ubhave.anticipatorymiddleware.server.database;

public class MongoDBUtils {


	public static final String Collection_Type_User_Registration = "Collection_Type_User_Registration";
	
	public static final String Collection_Type_User_Context = "Collection_Type_User_Context";

	public static final String Collection_Type_User_Prediction_Model = "Collection_Type_User_Prediction_Model";

	public static final String Key_Type_User_Id = "Key_Type_User_Id";
	
	public static final String Key_Type_User_Context = "Key_Type_User_Context";
	
	public static final String Key_Type_User_Context_Sampling_Rate = "Key_Type_User_Context_Sampling_Rate";
	
	public static final String Key_Type_User_Context_Life_Cycle_Period = "Key_Type_User_Context_Life_Cycle_Period";
	
	public static final String Key_Type_User_Privacy_Policy = "Key_Type_User_Privacy_Policy";
	
	public static final String Key_Type_User_OSN_IDS = "Key_Type_User_OSN_IDS";
	
	public static final String Key_Type_User_Prediction_Data = "Key_Type_User_Prediction_Data";
	
	public static final String Key_Type_User_Friend_Ids = "Key_Type_User_Friend_Ids";
	
	public static final String Key_Type_Predictor_Type = "Key_Type_Predictor_Type";
	
	public static final String Key_Type_Subscription_Id = "Key_Type_Subscription_Id";
	
	public static final String Key_Type_Sensor_Type(int sensor_type){
		return "Key_Type_Sensor_Type_"+sensor_type;
	}
	
}
