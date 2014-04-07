package org.ubhave.anticipatorymiddleware.server.datastack;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.server.privacy.PrivacyManager;

public class GroupPredictionRequestStack {

	private static JSONArray group_prediction_requests = new JSONArray();	


	public static void addNewGroupPredictionRequest(JSONObject obj){
		group_prediction_requests.put(obj);
	}

	public static void onNewUserOrPrivacyModification(){
		try {
			ArrayList<Integer> indeces = new ArrayList<Integer>();
			for(int i=0; i<group_prediction_requests.length(); i++){
				JSONObject group_prediction_request = group_prediction_requests.getJSONObject(i);
				boolean privacy_result = PrivacyManager.onNewGroupPredictionRequest(group_prediction_request, false);
				if(privacy_result){
					indeces.add(i);
				}
			} 
			for(int i: indeces){
				group_prediction_requests.remove(i);
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
	}
}


