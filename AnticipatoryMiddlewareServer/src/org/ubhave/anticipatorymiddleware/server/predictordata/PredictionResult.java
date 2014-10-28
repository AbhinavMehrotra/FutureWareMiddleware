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

import java.io.Serializable;

import org.ubhave.anticipatorymiddleware.server.time.Time;


public class PredictionResult implements Serializable{

	private static final long serialVersionUID = 4995448060127505104L;

	private String predicted_state;

	private Time predicted_time;

//	private Set<Time> predicted_time;

	private int prediction_probability;

	private int prediction_confidence_level;

	public PredictionResult(){
		this.predicted_state = "UNKNOWN";
		this.predicted_time = Time.getCurrentTime();
		this.prediction_confidence_level = 0;
		this.prediction_probability = 0;
	}

	public PredictionResult(String predicted_state, Time predicted_time, int prediction_probability, int prediction_confidence_level){
		this.predicted_state = predicted_state;
		this.predicted_time = predicted_time;
		this.prediction_confidence_level = prediction_confidence_level;
		this.prediction_probability = prediction_probability;
	}

//	public PredictionResult(){
//		this.predicted_state = "UNKNOWN";
//		this.predicted_time = new HashSet<Time>();
//		this.prediction_confidence_level = 0;
//		this.prediction_probability = 0;
//	}
//
//	public PredictionResult(String predicted_state, Set<Time> predicted_time, int prediction_probability, int prediction_confidence_level){
//		this.predicted_state = predicted_state;
//		this.predicted_time = predicted_time;
//		this.prediction_confidence_level = prediction_confidence_level;
//		this.prediction_probability = prediction_probability;
//	}

	public String getPredictedState() {
		return predicted_state;
	}

	public void setPredictedState(String predicted_state) {
		this.predicted_state = predicted_state;
	}

	public Time getPredictedTime() {
		return predicted_time;
	}

	public void setPredictedTime(Time predicted_time) {
		this.predicted_time = predicted_time;
	}

//	public Set<Time> getPredictedTime() {
//		return predicted_time;
//	}
//
//	public void setPredictedTime(Set<Time> predicted_time) {
//		this.predicted_time = predicted_time;
//	}

	public int getPredictionProbability() {
		return prediction_probability;
	}

	public void setPredictionProbability(int prediction_probability) {
		this.prediction_probability = prediction_probability;
	}

	public int getPredictionConfidenceLevel() {
		return prediction_confidence_level;
	}

	public void setPredictionConfidenceLevel(int prediction_confidence_level) {
		this.prediction_confidence_level = prediction_confidence_level;
	}
	
	
	
}
