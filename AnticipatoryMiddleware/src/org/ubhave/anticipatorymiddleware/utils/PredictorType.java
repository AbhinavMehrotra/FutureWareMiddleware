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
package org.ubhave.anticipatorymiddleware.utils;

public class PredictorType {


	public final static int ACTIVITY_PREDICTOR = 15001;
	public final static int SOCIAL_PREDICTOR = 15003;
	public final static int LOCATION_PREDICTOR = 15004;
	
	public final static int[] ALL_PREDICTORS = new int[] { ACTIVITY_PREDICTOR, 
		LOCATION_PREDICTOR, SOCIAL_PREDICTOR};
	
	public static Boolean isValidPredictor(int predictor){
		for(int  aPredictor: ALL_PREDICTORS){
			if(aPredictor == predictor)
				return true;
		}
		return false;
	}
	
	public static String getPreditorName(int predictor){
		switch(predictor){
		case ACTIVITY_PREDICTOR: return "ACTIVITY_PREDICTOR";
		case LOCATION_PREDICTOR: return "LOCATION_PREDICTOR";
		case SOCIAL_PREDICTOR: return "SOCIAL_PREDICTOR";
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
