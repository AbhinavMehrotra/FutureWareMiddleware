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
package org.ubhave.anticipatorymiddleware.predictordata;

import java.io.Serializable;
import java.util.ArrayList;

import org.ubhave.anticipatorymiddleware.utils.Constants;


public abstract class PredictorData implements Serializable{

	private static final long serialVersionUID = -3095404128053961508L;
	public final static PredictorData ACTIVITY = new ActivityData();
	public final static PredictorData LOCATION = new LocationData();
	public final static PredictorData SOCIAL = new SocialData();
	public final static PredictorData APPLICATION = new ApplicationData();
	public final static PredictorData CALL = new CallData();
	public final static PredictorData SMS = new SMSData();
	public final static PredictorData WIFI = new WifiData();


	public abstract int getPredictorType();

	public abstract PredictionResult getMostProbableResult();

	public abstract ArrayList<PredictionResult> getResult();

	public abstract void setResult(ArrayList<PredictionResult> result);	

	public static PredictorData getInstance(int predictor_type){
		switch (predictor_type){
		case Constants.PREDICTOR_TYPE_ACTIVITY :
			return ACTIVITY;

		case Constants.PREDICTOR_TYPE_LOCATION :
			return LOCATION;

		case Constants.PREDICTOR_TYPE_SOCIAL :
			return SOCIAL;

		case Constants.PREDICTOR_TYPE_APPLICATION :
			return APPLICATION;

		case Constants.PREDICTOR_TYPE_CALL_LOGS :
			return CALL;

		case Constants.PREDICTOR_TYPE_SMS_LOGS :
			return SMS;
			
		default: 
			return null;
		}
	}
	

}
