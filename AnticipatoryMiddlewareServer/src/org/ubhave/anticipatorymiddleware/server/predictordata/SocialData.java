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
package org.ubhave.anticipatorymiddleware.server.predictordata;

import java.util.ArrayList;

import org.ubhave.anticipatorymiddleware.server.utils.Constants;

public class SocialData  extends PredictorData{


	private static final long serialVersionUID = -3556903417287655390L;

	private final int predictor_type = Constants.PREDICTOR_TYPE_SOCIAL;

	private ArrayList<PredictionResult> result;

	protected SocialData(){};

	public SocialData(ArrayList<PredictionResult> result){
		this.result = result;
	}

	@Override
	public int getPredictorType() {
		return this.predictor_type;
	}

	@Override
	public PredictionResult getMostProbableResult() {
		PredictionResult most_probable_result = new PredictionResult();
		int max_probability = 0;
		for(PredictionResult pr : result){
			if(pr.getPredictionProbability() > max_probability){
				max_probability = pr.getPredictionProbability();
				most_probable_result = pr;
			}
		}
		return most_probable_result;
	}

	@Override
	public ArrayList<PredictionResult> getResult() {
		return result;
	}

	@Override
	public void setResult(ArrayList<PredictionResult> result) {
		this.result = result;
	}
		
}
