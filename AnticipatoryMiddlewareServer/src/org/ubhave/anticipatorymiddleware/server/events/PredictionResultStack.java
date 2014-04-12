package org.ubhave.anticipatorymiddleware.server.events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.server.AnticipatoryManager;
import org.ubhave.anticipatorymiddleware.server.communication.JSONKeys;
import org.ubhave.anticipatorymiddleware.server.communication.MQTTManager;
import org.ubhave.anticipatorymiddleware.server.communication.MessageType;
import org.ubhave.anticipatorymiddleware.server.predictordata.PredictionResult;
import org.ubhave.anticipatorymiddleware.server.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.server.time.Time;
import org.ubhave.anticipatorymiddleware.server.utils.Constants;
import org.ubhave.anticipatorymiddleware.server.utils.ObjectSerializer;

public class PredictionResultStack {
	
	private static Set<StackedGroupPredictorData> prediction_result_stack = new HashSet<StackedGroupPredictorData>();

	protected static void addNewPredictionResult(int subscription_id, String requestor_id, Set<String> required_users, PredictorData predictor_data){
		StackedGroupPredictorData.addNewResult(subscription_id, requestor_id, required_users, predictor_data);
		checkSuccessOfStackedDataForGroupPrediction();
	}

	protected static StackedGroupPredictorData getPredictionResultById(int subscription_id){
		for(StackedGroupPredictorData pr: prediction_result_stack){
			if(pr.getSubscriptionId() == subscription_id){
				return pr;
			}
		}
		return null;
	}
	
	private static void checkSuccessOfStackedDataForGroupPrediction(){
		for(StackedGroupPredictorData pr: prediction_result_stack){
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

	private static PredictorData makeGroupPrediction(StackedGroupPredictorData prediction_result){
		Set<PredictorData> predictor_data_set = prediction_result.getPredictorDataSet();
//		int predictor_type = 0;
		String predicted_state = "UNKNOWN";
		int prediction_probability = 0;
		int prediction_confidence_level = 0;
		for(PredictorData pd : predictor_data_set){
//			predictor_type = pd.getPredictorType();
			ArrayList<PredictionResult> pr_list = pd.getResult();
			PredictionResult pr = pr_list.get(0);
			predicted_state = pr.getPredictedState();
			prediction_confidence_level = pr.getPredictionConfidenceLevel();
			prediction_probability = pr.getPredictionProbability();
			break;
		}
		
		Set<Time> time_result = new HashSet<Time>();
		ArrayList<Set<Time>> time_list = new ArrayList<Set<Time>>();
		for(PredictorData pd : predictor_data_set){
			ArrayList<PredictionResult> pr_list = pd.getResult();
			Set<Time> time_set = new HashSet<Time>();
			for(PredictionResult pr : pr_list){
				time_set.add(pr.getPredictedTime());
			}
			time_list.add(time_set);
		}

		for(int i=0; i<time_list.size(); i++){
			Set<Time> time_set = time_list.get(i);
			for(Time time : time_set){
				if(isPresentInAllSets(time_list, time)){
					time_result.add(time);
				}
			}
		}
//		PredictorData result = PredictorData.getInstance(predictor_type);
		PredictorData result = PredictorData.getInstance(Constants.PREDICTOR_TYPE_SOCIAL);
		ArrayList<PredictionResult> predictor_result_set = new ArrayList<PredictionResult>();
		for(Time time : time_result){
			PredictionResult pr = new PredictionResult(predicted_state, time, prediction_probability, prediction_confidence_level);
			predictor_result_set.add(pr);
		}
		result.setResult(predictor_result_set);		
		return result;
	}
	
	private static boolean isPresentInAllSets(ArrayList<Set<Time>> time_list, Time time){
		boolean[] results = new boolean[time_list.size()];
		for(int i=0; i<time_list.size(); i++){
			if(time_list.get(i).contains(time)){
				results[i] = true;
			}
			else{
				results[i] = false;
			}
		}
		for(boolean result: results){
			if(result == false)
				return false;
		}
		return true;
	}
	
	
	
	public static class StackedGroupPredictorData{
		private int subscription_id;
		String requestor_id;
		Set<String> required_users;
		Set<PredictorData> predictor_data_set;
		
		private StackedGroupPredictorData(int subscription_id, String requestor_id, Set<String> required_users, PredictorData predictor_data) {
			this.subscription_id = subscription_id;
			this.requestor_id = requestor_id;
			this.required_users = required_users;
			this.predictor_data_set = new HashSet<PredictorData>();
			this.predictor_data_set.add(predictor_data);
		}
		
		public static void addNewResult(int subscription_id, String requestor_id, Set<String> required_users, PredictorData predictor_data){
			StackedGroupPredictorData prediction_result = null;
			for (StackedGroupPredictorData pr : prediction_result_stack){
				if(pr.getSubscriptionId() == subscription_id){
					prediction_result = pr;
					break;
				}
			}
			if(prediction_result == null){
				prediction_result = new StackedGroupPredictorData(subscription_id, requestor_id, required_users, predictor_data);
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
