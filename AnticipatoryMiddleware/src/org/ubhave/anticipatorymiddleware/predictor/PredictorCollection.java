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

import android.util.Log;


public class PredictorCollection {

	private static final String TAG = "AnticipatoryManager";
	
	public PredictorCollection(){};

	public static String SimpleMarkovChainPredictor = "org.ubhave.anticipatorymiddleware.predictor.MarkovChainPredictor";

	public static String SimpleTimeSeriesPredictor = "org.ubhave.anticipatorymiddleware.predictor.TimeSeriesPredictor";

	public static String FirstDegreeMarkovChainPredictor = "org.ubhave.anticipatorymiddleware.predictor.FirstDegreeMarkovChainPredictor";
	
	public static String[] Predictors = new String[] {SimpleMarkovChainPredictor, SimpleTimeSeriesPredictor, FirstDegreeMarkovChainPredictor};
	
	public static String[] PredictorNames 
			= new String[] {"SimpleMarkovChainPredictor", "SimpleTimeSeriesPredictor", "FirstDegreeMarkovChainPredictor", "UserDefined"};

	public String getName(String type)
	{
		if(type.equalsIgnoreCase(SimpleMarkovChainPredictor))
			return "SimpleMarkovChainPredictor";
		else if(type.equalsIgnoreCase(SimpleTimeSeriesPredictor))
			return "SimpleTimeSeriesPredictor";
		else if(type.equalsIgnoreCase(FirstDegreeMarkovChainPredictor))
			return "FirstDegreeMarkovChainPredictor";
		else 
			return "UserDefined";
	}
	
	public PredictorModule getPredictorModule(String predictor_name_or_path){
		PredictorModule predictor_module = null;
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName(predictor_name_or_path);
			predictor_module = (PredictorModule) c.newInstance();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
//			Log.e(TAG, "Error in creating the instance of the required predictor module. MarkovChainPredictor is used for now!!");
//			predictor_module =  new MarkovChainPredictor();
		}
		return predictor_module;
	}
}
