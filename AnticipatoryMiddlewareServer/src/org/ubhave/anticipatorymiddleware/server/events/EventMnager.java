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
package org.ubhave.anticipatorymiddleware.server.events;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.server.AMException;
import org.ubhave.anticipatorymiddleware.server.AnticipatoryManager;
import org.ubhave.anticipatorymiddleware.server.communication.JSONKeys;
import org.ubhave.anticipatorymiddleware.server.communication.MQTTManager;
import org.ubhave.anticipatorymiddleware.server.communication.MessageType;
import org.ubhave.anticipatorymiddleware.server.database.MongoDBManager;
import org.ubhave.anticipatorymiddleware.server.events.PredictionResultStack.StackedGroupPredictorData;
import org.ubhave.anticipatorymiddleware.server.predictor.Predictor;
import org.ubhave.anticipatorymiddleware.server.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.server.utils.Constants;
import org.ubhave.anticipatorymiddleware.server.utils.ObjectSerializer;

public class EventMnager {

	public void onNewGroupPredictionRequest(int subscription_id, String requestor_id, Set<String> friend_ids, int predictor_type, String state_to_be_predicted){
		/*TODO
		 * check if the required data is present locally
		 * if so, then make prediction and transmit the group prediction response.
		 * if not make remote query (via remoteQuery) and 
		 * put the prediction data (of the users of whom data is present locally) in stack
		 */
		Set<String> group_ids = new HashSet<String>();
		group_ids.addAll(friend_ids);
		group_ids.add(requestor_id);
		try {
			MongoDBManager mongodb_manager = AnticipatoryManager.getInstance().getMongoDBManager();
			for(String id: group_ids){
				JSONObject predictor_model = mongodb_manager.geteUserPredictionModel(id);
				if(predictor_model != null){
					//TODO: make prediction for this user
					int context_sampling_rate = mongodb_manager.getUserContextSamplingRate(id);
					int context_life_cycle = mongodb_manager.getUserContextLifeCyclePeriod(id);
//					String current_state = mongodb_manager.geteUserContext(id, Constants.getSensor(predictor_type));
					Predictor predictor = new Predictor(predictor_type, context_sampling_rate, context_life_cycle);
					PredictorData predictor_data = predictor.predictionRequest(predictor_model, state_to_be_predicted);
					
					//add prediction data to the stack
					PredictionResultStack.addNewPredictionResult(subscription_id, requestor_id, group_ids, predictor_data);
				}
				else{
					this.remoteQuery(subscription_id, id, requestor_id, predictor_type, state_to_be_predicted);				
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (AMException e) {
			e.printStackTrace();
		}		
	}

	public void onNewGroupPredictionRequest(int subscription_id, String requestor_id, Set<String> friend_ids, 
			int predictor_type, long required_prediction_time){
		/*TODO
		 * check if the required data is present locally
		 * if so, then make prediction and transmit the group prediction response.
		 * if not make remote query (via remoteQuery) and 
		 * put the prediction data (of the users of whom data is present locally) in stack
		 */
		Set<String> group_ids = new HashSet<String>();
		group_ids.addAll(friend_ids);
		group_ids.add(requestor_id);
		try {
			MongoDBManager mongodb_manager = AnticipatoryManager.getInstance().getMongoDBManager();
			for(String id: group_ids){
				JSONObject predictor_model = mongodb_manager.geteUserPredictionModel(id);
				if(predictor_model != null){
					//TODO: make prediction for this user
					int context_sampling_rate = mongodb_manager.getUserContextSamplingRate(id);
					int context_life_cycle = mongodb_manager.getUserContextLifeCyclePeriod(id);
					String current_state = mongodb_manager.geteUserContext(id, Constants.getSensor(predictor_type));
					Predictor predictor = new Predictor(predictor_type, context_sampling_rate, context_life_cycle);
					PredictorData predictor_data = predictor.predictionRequest(predictor_model, current_state, required_prediction_time);
					
					//add prediction data to the stack
					PredictionResultStack.addNewPredictionResult(subscription_id, requestor_id, group_ids, predictor_data);
				}
				else{
					this.remoteQuery(subscription_id, id, requestor_id, predictor_type, required_prediction_time);				
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (AMException e) {
			e.printStackTrace();
		}		
	}

	public void onNewRemotePredictionResponse(JSONObject obj){
		/*TODO
		 * put this data in stack
		 * check if stack has all the required data then make prediction and 
		 * transmit the group prediction response via transmitGroupPredictionResult.
		 */
		try {
			int subscription_id = obj.getInt(JSONKeys.SUBSCRIPTION_ID);
			String predictor_data_string = obj.getString(JSONKeys.PREDICTOR_DATA);
			StackedGroupPredictorData pr = PredictionResultStack.getPredictionResultById(subscription_id);
			PredictorData predictor_data = (PredictorData) ObjectSerializer.fromString(predictor_data_string);
			if(pr != null){
				PredictionResultStack.addNewPredictionResult(subscription_id, pr.getRequestorId(), pr.getRequiredUsers(), predictor_data);
			}
			else{
				System.out.println("Error inside onNewRemotePredictionResponse()");
				System.out.println("Null value received by the getPredictionResultById method.");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void  remoteQuery(int subscription_id, String user_id, String requestor_id, int predictor_type, String state_to_be_predicted){
		try {
			JSONObject query = new JSONObject();
			query.put(JSONKeys.DATA_TYPE, MessageType.REMOTE_PREDICTION_REQUEST);
			query.put(JSONKeys.SUBSCRIPTION_ID, subscription_id);
			query.put(JSONKeys.REQUESTOR_ID, requestor_id);
			query.put(JSONKeys.PREDICTOR_TYPE, predictor_type);
			query.put(JSONKeys.PREDICTION_TYPE, JSONKeys.PREDICTION_TYPE_STATE_SPECIFIC);
			query.put(JSONKeys.PREDICTION_TYPE_VALUE, state_to_be_predicted);
			MQTTManager mqtt_manager = AnticipatoryManager.getInstance().getMQTTManager(user_id);
			mqtt_manager.publishToDevice(query);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}		
	}
	
	private void  remoteQuery(int subscription_id, String user_id, String requestor_id, int predictor_type, long required_prediction_time){
		try {
			JSONObject query = new JSONObject();
			query.put(JSONKeys.DATA_TYPE, MessageType.REMOTE_PREDICTION_REQUEST);
			query.put(JSONKeys.SUBSCRIPTION_ID, subscription_id);
			query.put(JSONKeys.REQUESTOR_ID, requestor_id);
			query.put(JSONKeys.PREDICTOR_TYPE, predictor_type);
			query.put(JSONKeys.PREDICTION_TYPE, JSONKeys.PREDICTION_TYPE_TIME_SPECIFIC);
			query.put(JSONKeys.PREDICTION_TYPE_VALUE, required_prediction_time);
			MQTTManager mqtt_manager = AnticipatoryManager.getInstance().getMQTTManager(user_id);
			mqtt_manager.publishToDevice(query);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}		
	}

	public static void transmitGroupPredictionResult(String user_id, int subscription_id, PredictorData predictor_data){
		try {
			JSONObject result = new JSONObject();
			result.put(JSONKeys.DATA_TYPE, MessageType.GROUP_PREDICTION_RESPONSE);
			result.put(JSONKeys.SUBSCRIPTION_ID, subscription_id);
			result.put(JSONKeys.PREDICTOR_DATA, ObjectSerializer.toString(predictor_data));
			MQTTManager mqtt_manager = AnticipatoryManager.getInstance().getMQTTManager(user_id);
			mqtt_manager.publishToDevice(result);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}





}
