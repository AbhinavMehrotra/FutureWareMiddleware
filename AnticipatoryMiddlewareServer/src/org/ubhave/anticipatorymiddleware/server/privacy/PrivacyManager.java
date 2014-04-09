package org.ubhave.anticipatorymiddleware.server.privacy;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.server.AMException;
import org.ubhave.anticipatorymiddleware.server.AnticipatoryManager;
import org.ubhave.anticipatorymiddleware.server.communication.JSONKeys;
import org.ubhave.anticipatorymiddleware.server.database.MongoDBManager;
import org.ubhave.anticipatorymiddleware.server.datastack.GroupPredictionRequestStack;
import org.ubhave.anticipatorymiddleware.server.events.EventMnager;

public class PrivacyManager {


	public static boolean onNewGroupPredictionRequest(JSONObject obj, boolean stack_required){
		try {
			String user_id = obj.getString(JSONKeys.USER_ID);
			int subscription_id = obj.getInt(JSONKeys.SUBSCRIPTION_ID);
			int predictor_id = obj.getInt(JSONKeys.PREDICTOR_TYPE);
			String state_to_be_predicted = obj.getString(JSONKeys.STATE_TO_BE_PREDICTED);
			JSONArray friend_ids_array = obj.getJSONArray(JSONKeys.FRIEND_IDS);
			Set<String> friend_ids = new HashSet<String>();
			for(int i = 0; i<friend_ids_array.length(); i++){
				friend_ids.add(friend_ids_array.getString(i));
			}
			boolean privacy_check = hasAccess(user_id, friend_ids, predictor_id);
			if(privacy_check){
				new EventMnager().onNewGroupPredictionRequest(subscription_id, user_id, friend_ids, predictor_id, state_to_be_predicted);
				return true;
			}
			else{
				System.out.println("Privacy check failed!! \n User: "+user_id+ " is not allowed to" +
						"access prediction data (predictor type: "+ predictor_id+ ") of the required" +
						"friends: "+ friend_ids);
				if(stack_required){
					GroupPredictionRequestStack.addNewGroupPredictionRequest(obj);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean hasAccess(String user_id, Set<String> friend_ids, int predictor_id){
		//TODO: implement this method
		boolean privacy_result = false;
		try {
			MongoDBManager mongodb = AnticipatoryManager.getInstance().getMongoDBManager();
			JSONObject privacy_object = mongodb.getUserPrivacyPolicy(user_id);
			JSONArray access_granted_friends_list = privacy_object.getJSONArray(JSONKeys.Access_Granted_Friends_List);
			JSONArray access_granted_predictor_list = privacy_object.getJSONArray(JSONKeys.Access_Granted_Predictors_List);
			
			//check for friend read access
			for(String friend_id: friend_ids){
				privacy_result = false;
				for(int i=0; i< access_granted_friends_list.length(); i++){
					if(access_granted_friends_list.getString(i).equalsIgnoreCase(friend_id)){
						privacy_result = true;
						break;
					}
				}
				if(privacy_result == false){
					return false;
				}
			}
			
			//check for allowed predictor			
			if(access_granted_predictor_list.getInt(0) == -1){
				return true;
			}
			else{
				for(int i=0; i<access_granted_predictor_list.length(); i++){
					if(access_granted_predictor_list.getInt(i) == predictor_id){
						return true;
					}
				}
				return false;
			}			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (AMException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}


}
