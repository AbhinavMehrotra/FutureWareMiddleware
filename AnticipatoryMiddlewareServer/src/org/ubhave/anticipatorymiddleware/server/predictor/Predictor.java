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
package org.ubhave.anticipatorymiddleware.server.predictor;


import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.server.predictordata.PredictorData;

public class Predictor{

	private final int predictor_id;
	private final int context_sampling_rate;
	private final int context_life_cycle;

	public Predictor(int predictor_id, int context_sampling_rate, int context_life_cycle){
		this.predictor_id = predictor_id;
		this.context_sampling_rate = context_sampling_rate;
		this.context_life_cycle = context_life_cycle;
	}

	public PredictorData predictionRequest(JSONObject predictor_model, String current_state, long notification_period){
		MarkovChainPredictor makrok_chain_predictor = new MarkovChainPredictor(predictor_id, context_sampling_rate, context_life_cycle);
		PredictorData predictor_data = makrok_chain_predictor.predictionRequest(predictor_model, current_state, notification_period);
		if(predictor_data == null){
			System.out.println("MarkovChainPredictor returned null value!");
		}		
		return predictor_data;
	}


	public PredictorData predictionRequest(JSONObject predictor_model, String state_to_be_predicted){
		MarkovChainPredictor makrok_chain_predictor = new MarkovChainPredictor(predictor_id, context_sampling_rate, context_life_cycle);
		PredictorData predictor_data = makrok_chain_predictor.predictionRequest(predictor_model, state_to_be_predicted);
		if(predictor_data == null){
			System.out.println("MarkovChainPredictor returned null value!");
		}		
		return predictor_data;
	}


	public int getId() {
		return predictor_id;
	}


}
