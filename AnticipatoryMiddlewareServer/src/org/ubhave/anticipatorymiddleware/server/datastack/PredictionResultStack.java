package org.ubhave.anticipatorymiddleware.server.datastack;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.server.AnticipatoryManager;
import org.ubhave.anticipatorymiddleware.server.ObjectSerializer;
import org.ubhave.anticipatorymiddleware.server.communication.JSONKeys;
import org.ubhave.anticipatorymiddleware.server.communication.MQTTManager;
import org.ubhave.anticipatorymiddleware.server.communication.MessageType;
import org.ubhave.anticipatorymiddleware.server.predictordata.PredictorData;

public class PredictionResultStack {
	
	private static Set<PredictionResult> prediction_result_stack = new HashSet<PredictionResult>();

	public static void addNewPredictionResult(int subscription_id, String requestor_id, Set<String> required_users, PredictorData predictor_data){
		PredictionResult.addNewResult(subscription_id, requestor_id, required_users, predictor_data);
		checkSuccessOfStackedDataForGroupPrediction();
	}

	public static PredictionResult getPredictionResultById(int subscription_id){
		for(PredictionResult pr: prediction_result_stack){
			if(pr.getSubscriptionId() == subscription_id){
				return pr;
			}
		}
		return null;
	}
	
	private static void checkSuccessOfStackedDataForGroupPrediction(){
		for(PredictionResult pr: prediction_result_stack){
			if(pr.getRequiredUsers().size() == pr.getPredictorDataSet().size()){
				PredictorData predictor_data = makeGroupPrediction(pr);
				if(predictor_data != null){
					try {
						JSONObject obj = new JSONObject();
						obj.put(JSONKeys.DATA_TYPE, MessageType.GROUP_PREDICTION_RESPONSE);
						obj.put(JSONKeys.SUBSCRIPTION_ID, pr.getSubscriptionId());
						obj.put(JSONKeys.PREDICTOR_DATA, ObjectSerializer.toString(predictor_data));
						String user_id = pr.getRequestorId();
						MQTTManager mqtt_manager = AnticipatoryManager.getInstance().getMQTTManager(user_id);
						mqtt_manager.publishToDevice(obj);
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (MqttException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
	}

	private static PredictorData makeGroupPrediction(PredictionResult prediction_result){
		//TODO
		return null;
	}
	
	
	
	public static class PredictionResult{
		private int subscription_id;
		String requestor_id;
		Set<String> required_users;
		Set<PredictorData> predictor_data_set;
		
		private PredictionResult(int subscription_id, String requestor_id, Set<String> required_users, PredictorData predictor_data) {
			this.subscription_id = subscription_id;
			this.requestor_id = requestor_id;
			this.required_users = required_users;
			this.predictor_data_set = new HashSet<PredictorData>();
			this.predictor_data_set.add(predictor_data);
		}
		
		public static void addNewResult(int subscription_id, String requestor_id, Set<String> required_users, PredictorData predictor_data){
			PredictionResult prediction_result = null;
			for (PredictionResult pr : prediction_result_stack){
				if(pr.getSubscriptionId() == subscription_id){
					prediction_result = pr;
					break;
				}
			}
			if(prediction_result == null){
				prediction_result = new PredictionResult(subscription_id, requestor_id, required_users, predictor_data);
				prediction_result_stack.add(prediction_result);
			}
			else{
				prediction_result_stack.remove(prediction_result);
				prediction_result.addToPredictorDataSet(predictor_data);
				prediction_result_stack.add(prediction_result);
			}
		}

		
		public int getSubscriptionId(){
			return subscription_id;
		}
		
		public String getRequestorId(){
			return requestor_id;
		}
		
		public Set<String> getRequiredUsers(){
			return required_users;
		}
		
		public Set<PredictorData> getPredictorDataSet(){
			return predictor_data_set;
		}
		
		public void addToPredictorDataSet(PredictorData predictor_data){
			predictor_data_set.add(predictor_data);
		}
	}
}
