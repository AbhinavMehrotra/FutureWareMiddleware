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

import java.util.HashSet;
import java.util.Set;

import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;

import android.content.Context;

public class ActivePredictorsList {
	
	private final SharedPreferences sp;
	
	public ActivePredictorsList(Context app_context){
		this.sp = new SharedPreferences(app_context);
	}

	public Set<Integer> getActivePredictorsList(){
		Set<Integer> predictor_set = new HashSet<Integer>();
		Set<String> predictors = sp.getStringSet("ACTIVE_PREDICTORS");
		for(String predictor : predictors)
			predictor_set.add(Integer.parseInt(predictor));
		return predictor_set;
	}
	

	public void addPredictorInActivePredictorsList(int predictor_id){
		Set<String> predictors = sp.getStringSet("ACTIVE_PREDICTORS");
		predictors.add(String.valueOf(predictor_id));
		sp.addStringSet("ACTIVE_PREDICTORS", predictors);
	}
	
}
