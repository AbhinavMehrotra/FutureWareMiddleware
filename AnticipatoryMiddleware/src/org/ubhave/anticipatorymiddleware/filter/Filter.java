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
package org.ubhave.anticipatorymiddleware.filter;

import java.io.Serializable;
import java.util.Set;

import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;

import android.util.Log;


public class Filter  implements Serializable{

	private static final long serialVersionUID = -1080247041978676299L;
	private static final String TAG = "AnticipatoryManager";	
	private int root_id;
	private String expression_string;
	private FilterParser parser;
	
	public Filter(String expression_string) {
		this.expression_string = expression_string;
		parser = new FilterParser(this.expression_string);
		Log.d(TAG, "Filter create: " + this.expression_string);
	}

	public int getExpressionTreeRootId(){		
		return root_id;
	}

	public Set<Integer> getRequiredPredictors(){
		return parser.getSetOfRequiredPredictors();
	}
	
	public Set<Condition> getAllConditions(){
		return parser.getSetOfConditions();
	}

	
	public String getExpressionString(){
		return expression_string;
	}
	
	public Boolean validateFilter(Set<PredictorData> predictor_data){
		return parser.validateFilter(predictor_data);
	}
	
	@Override
	public String toString(){
		return expression_string;
	}
}
