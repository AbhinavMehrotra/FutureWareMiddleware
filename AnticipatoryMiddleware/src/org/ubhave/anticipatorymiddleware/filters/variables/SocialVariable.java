package org.ubhave.anticipatorymiddleware.filters.variables;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

public class SocialVariable{

	public static String friends(Set<String> friends_osn_ids){
		JSONArray json_array = new JSONArray(friends_osn_ids);
		String json_string = json_array.toString();
		return "friend_ids_"+json_string.substring(1, json_string.length()-1);
	}

	public static Set<String> getFriendIdsByString(String friend_ids_string){
		Set<String> friends_osn_ids = new HashSet<String>();
		try {
			friend_ids_string = friend_ids_string.substring("friend_ids_".length());
			friend_ids_string = "["+friend_ids_string+"]";
			JSONArray json_array = new JSONArray(friend_ids_string);
			for(int i = 0; i<json_array.length(); i++){
				friends_osn_ids.add(json_array.getString(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return friends_osn_ids;
	}

	public static String PredictionProbability = "SocialPredictionProbability";

	public static String PredictionConfidenceLevel = "SocialPredictionConfidenceLevel";

	public static String PredictedTime = "SocialPredictedTime";
}
