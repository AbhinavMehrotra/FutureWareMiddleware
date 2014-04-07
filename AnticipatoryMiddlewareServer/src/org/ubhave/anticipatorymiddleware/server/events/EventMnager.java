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
import org.ubhave.anticipatorymiddleware.server.ObjectSerializer;
import org.ubhave.anticipatorymiddleware.server.communication.JSONKeys;
import org.ubhave.anticipatorymiddleware.server.communication.MQTTManager;
import org.ubhave.anticipatorymiddleware.server.communication.MessageType;
import org.ubhave.anticipatorymiddleware.server.database.MongoDBManager;
import org.ubhave.anticipatorymiddleware.server.datastack.PredictionResultStack;
import org.ubhave.anticipatorymiddleware.server.datastack.PredictionResultStack.PredictionResult;
import org.ubhave.anticipatorymiddleware.server.predictordata.PredictorData;

public class EventMnager {

	public void onNewGroupPredictionRequest(int subscription_id, String requestor_id, Set<String> friend_ids, int predictor_type){
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
				JSONObject prediction_model = mongodb_manager.geteUserPredictionModel(id);
				if(prediction_model != null){
					//TODO: make prediction for this user
					PredictorData predictor_data = null;
					
					//add prediction data to the stack
					PredictionResultStack.addNewPredictionResult(subscription_id, requestor_id, group_ids, predictor_data);
				}
				else{
					this.remoteQuery(subscription_id, id, requestor_id, predictor_type);				
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
			PredictionResult pr = PredictionResultStack.getPredictionResultById(subscription_id);
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
	
	private void  remoteQuery(int subscription_id, String user_id, String requestor_id, int predictor_type){
		try {
			JSONObject query = new JSONObject();
			query.put(JSONKeys.DATA_TYPE, MessageType.REMOTE_PREDICTION_REQUEST);
			query.put(JSONKeys.SUBSCRIPTION_ID, subscription_id);
			query.put(JSONKeys.REQUESTOR_ID, requestor_id);
			query.put(JSONKeys.PREDICTOR_TYPE, predictor_type);
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
