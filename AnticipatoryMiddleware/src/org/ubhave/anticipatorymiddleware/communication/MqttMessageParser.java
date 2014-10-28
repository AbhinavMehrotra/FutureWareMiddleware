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
package org.ubhave.anticipatorymiddleware.communication;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.event.EventManager;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.privacy.PrivacyManager;
import org.ubhave.anticipatorymiddleware.utils.ObjectSerializer;

import android.content.Context;

/**
 * Message parser for message received from the clients via TCP socket
 */
public class MqttMessageParser {

	private JSONObject obj;
	private final Context context;

	protected MqttMessageParser(Context context, MqttTopic topic, MqttMessage message){
		this.context = context;
		try {
			obj = new JSONObject(message.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parses the message received via TCP socket and takes actions to handle it
	 * @param message
	 */
	protected void run(){
		try {
			String data_type= obj.getString(JSONKeys.DATA_TYPE);
			int data_type_value = getDataTypeValue(data_type);
			System.out.println("data_type: "+data_type);
			System.out.println("data_type_value: "+data_type_value);
			switch (data_type_value) {
			case 1:
				//GROUP_PREDICTION_REQUEST
				//This will not be received instead this is to be transmitted to the server
				break;
			case 2:
				//GROUP_PREDICTION_RESPONSE
				//TODO
				try {
					int subscription_id = obj.getInt(JSONKeys.SUBSCRIPTION_ID);
					PredictorData predictor_data = (PredictorData) ObjectSerializer.fromString(obj.getString(JSONKeys.PREDICTOR_DATA));
					EventManager em = new EventManager(context, null);
					em.onGroupPredictionResponse(subscription_id, predictor_data);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 3:
				//REGISTRATION
				//This will not be received instead this is to be transmitted to the server
				break;
			case 4:
				//REMOTE_PREDICTION_REQUEST
				/*
				 * TODO
				 * Check the privacy policy.
				 * if pass: subscribe to event manager
				 * if fail: transmit dummy predictor data
				 */
				String requestor_id = obj.getString(JSONKeys.REQUESTOR_ID);
				int subscription_id = obj.getInt(JSONKeys.SUBSCRIPTION_ID);
				int predictor_type = obj.getInt(JSONKeys.PREDICTOR_TYPE);
				String preiction_type = obj.getString(JSONKeys.PREDICTION_TYPE);
				String predictor_name_or_path = obj.getString(JSONKeys.PREDICTOR_NAME_OR_PATH);
				PrivacyManager pm = new PrivacyManager(context);
				if(pm.hasReadAccess(requestor_id, predictor_type)){				
					if(preiction_type.equals(JSONKeys.PREDICTION_TYPE_STATE_SPECIFIC)){
						String prediction_type_value = obj.getString(JSONKeys.PREDICTION_TYPE_VALUE);
						EventManager em = new EventManager(context, null);
						em.newRemotePredictionRequest(requestor_id, subscription_id, predictor_type, preiction_type, prediction_type_value, predictor_name_or_path);
					}
					else if(preiction_type.equals(JSONKeys.PREDICTION_TYPE_TIME_SPECIFIC)){
						long prediction_type_value = obj.getLong(JSONKeys.PREDICTION_TYPE_VALUE);
						EventManager em = new EventManager(context, null);
						em.newRemotePredictionRequest(requestor_id, subscription_id, predictor_type, preiction_type, prediction_type_value, predictor_name_or_path);
					}
				}
				break;
			case 5:
				//REMOTE_PREDICTION_RESPONSE
				//This will not be received instead this is to be transmitted to the server
				break;
			case 6:
				//UPDATED_PREDICTION_MODEL
				//This will not be received instead this is to be transmitted to the server
				break;				

			case 7:
				//UPDATED_PRIVACY_POLICY
				//This will not be received instead this is to be transmitted to the server
				break;

			case 8:
				//USER_CONTEXT
				//This will not be received instead this is to be transmitted to the server
				break;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static int getDataTypeValue(String name){
		if(name.equalsIgnoreCase(MessageType.GROUP_PREDICTION_REQUEST)) return 1;
		if(name.equalsIgnoreCase(MessageType.GROUP_PREDICTION_RESPONSE)) return 2;
		if(name.equalsIgnoreCase(MessageType.REGISTRATION)) return 3;
		if(name.equalsIgnoreCase(MessageType.REMOTE_PREDICTION_REQUEST)) return 4;
		if(name.equalsIgnoreCase(MessageType.REMOTE_PREDICTION_RESPONSE)) return 5;	
		if(name.equalsIgnoreCase(MessageType.UPDATED_PREDICTION_MODEL)) return 6;	
		if(name.equalsIgnoreCase(MessageType.UPDATED_PRIVACY_POLICY)) return 7;			
		if(name.equalsIgnoreCase(MessageType.USER_CONTEXT)) return 8;		
		return 0;
	}


}
