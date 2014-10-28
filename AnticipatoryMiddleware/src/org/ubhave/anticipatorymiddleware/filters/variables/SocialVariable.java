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
