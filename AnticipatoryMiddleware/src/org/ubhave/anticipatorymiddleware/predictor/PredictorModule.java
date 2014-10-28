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

import java.util.ArrayList;

import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;

import android.content.Context;

public interface PredictorModule{

	
	/*
	 * TODO: create a method that returns the ids for the data required 
	 * according to the prediction to be made.
	 */	
//Example code for Simple Markov Chain Predictor
//	public ArrayList<Integer> idsOfRequiredData(int predictor_type){		
//		ArrayList<Integer> data_types = new ArrayList<Integer>();
//		data_types.add(Constants.getSensor(predictor_type));
//		return data_types;
//	}

	public String getNameOrPath();
	
	/**
	 * return ids of the required predictor data
	 * @param predictor_type
	 * @return predictor ids
	 */
	public ArrayList<Integer> idsOfRequiredData(int predictor_type);
	
	public JSONObject generateDataModel(Context app_context, JSONObject data, ArrayList<String> states);
	
	public PredictorData predictionRequest(int predictor_id, int context_sampling_rate, int context_life_cycle, ArrayList<JSONObject> predictor_models, String current_state, String state_to_be_predicted);

	public PredictorData predictionRequest(int predictor_id, int context_sampling_rate, int context_life_cycle, ArrayList<JSONObject> predictor_models, String current_state, long notification_period);

}
