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
package org.ubhave.anticipatorymiddleware.subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ubhave.anticipatorymiddleware.AnticipatoryListener;
import org.ubhave.anticipatorymiddleware.filter.Filter;
import org.ubhave.anticipatorymiddleware.predictor.Configuration;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.utils.Constants;



public class Subscription implements Serializable
{

	private static final long serialVersionUID = 2719548867625835020L;
	private final List<PredictorData> required_data; 
//	private final AnticipatoryListener listener;
	private final String listener_canonical_name;
	private final ClassReferenceHelper class_reference;
	private final Filter filter;
	private final Configuration configuration;
	private int id;
	
	public Subscription(List<PredictorData> required_data, AnticipatoryListener listener, 
			Filter filter, Configuration configuration)
	{
		this.required_data = required_data;
		class_reference = new ClassReferenceHelper();
		this.listener_canonical_name = class_reference.getClassCanonicalName(listener);
		this.filter = filter;
		this.configuration = configuration;
				
	}

	public List<PredictorData> getRequiredData() {
		return required_data;
	}

	public ArrayList<Integer> getPredictorListForRequiredData() {
		ArrayList<Integer> predictors = new ArrayList<Integer>();
		for(PredictorData data: required_data){
			if(data.getPredictorType() == Constants.PREDICTOR_TYPE_SOCIAL){
				continue; 
			}
			predictors.add(data.getPredictorType());
		}
		return predictors;
	}
	
	public AnticipatoryListener getListener() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		AnticipatoryListener listener = (AnticipatoryListener) class_reference.getClassReference(listener_canonical_name);
		return listener;
	}

	public Filter getFilter() {
		return filter;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
}
