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
package org.ubhave.anticipatorymiddleware.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.AMException;
import org.ubhave.anticipatorymiddleware.subscribe.Subscription;

import android.content.Context;
import android.util.Log;

public class FileManager {

	private final String TAG = "AnticipatoryManager";

	public JSONObject readJSONObject(String fileLocation) {
		Log.d(TAG, "Inside readJSONObject()");
		Log.d(TAG, "Reading from: " + fileLocation);
		String data = "";
		JSONObject jsonData = new JSONObject();
		File file = new File(fileLocation);

		Log.d(TAG, "Checking the file at: " + fileLocation);
		try {
			if (file.exists()) {
				Log.i(TAG, "File already exists.");
			} else {
				FileOutputStream f = new FileOutputStream(file);
				f.close();
				jsonData.put("DATA", new JSONArray());
				writeJSONObject(jsonData, fileLocation);
				Log.i(TAG, "File did not exists and a new file is created.");
				return jsonData;
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}

		try {
			FileInputStream inputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			String lineIn;
			while ((lineIn = bufferedReader.readLine()) != null) {
				data = data.concat(lineIn);
			}
			inputStreamReader.close();
			inputStream.close();

			if (data.length() > 0) {
				jsonData = new JSONObject(data);
			}
			else{
				Log.i(TAG, "File data length is zero. Empty array added.");
				jsonData.put("DATA", new JSONArray());
				writeJSONObject(jsonData, fileLocation);
			}
			return jsonData;
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
		
		try{
		jsonData.put("DATA", new JSONArray());
		writeJSONObject(jsonData, fileLocation);
		}
		catch(JSONException e){
			Log.e(TAG, e.toString());
		}
		return jsonData;
	}

	public Boolean writeJSONObject(JSONObject jsonObject, String fileLocation) {
		Log.d(TAG, "Inside writeJSONObject()");
		String data = jsonObject.toString();
		File file = new File(fileLocation);
		Log.d(TAG, "File location: " + fileLocation);

		try {
			if (file.createNewFile())
				Log.d(TAG, "WriteJSONObject(): File created at: "
						+ fileLocation);

			Log.d(TAG, "File present at: " + fileLocation);
			FileOutputStream f = new FileOutputStream(file);
			PrintWriter pw = new PrintWriter(f);
			pw.println(data);
			pw.flush();
			pw.close();
			f.close();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return false;
		}
		return true;
	}

	public Boolean appendJSONObjectInJSONArray(String array_name,
			JSONObject jsonObject, String fileLocation) throws AMException {
		Log.i(TAG, "Append JSONObject In JSONArray");
		Log.i(TAG, "JsonObject: " + jsonObject);
		Log.i(TAG, "FileLocation: " + fileLocation);
		JSONObject data;
		try {
			data = readJSONObject(fileLocation);
			JSONArray jsonArray = data.getJSONArray(array_name);
			jsonArray.put(jsonObject);
			data.remove(array_name);
			data.put(array_name, jsonArray);
			deleteFile(fileLocation);
			return writeJSONObject(data, fileLocation);
		} 
		catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
		return false;
	}

	public Boolean deleteFile(String fileLocation) {
		File file = new File(fileLocation);
		return file.delete();
	}

	public void addSubscriptionListToFile(List<Subscription> subscriptionList, Context context) {
		List<Subscription> oldSubscriptionList = getSubscriptionListFromFile(context);
		Log.i(TAG, "Old subscription list: "+oldSubscriptionList);
		Set<Subscription> subscription_set = new HashSet<Subscription>();
		subscription_set.addAll(subscriptionList);
		if(oldSubscriptionList != null)
		{
			subscription_set.addAll(oldSubscriptionList);
		}
		//put all subscription back
		subscriptionList.clear();
		subscriptionList.addAll(subscription_set);
		Log.i(TAG, "New subscription list: "+subscriptionList);
		FilePathGenerator path_generator = new FilePathGenerator();
		String file_path = path_generator.generateSubscriptionDataFilePath(context);
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(file_path, false);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(subscriptionList);
		} catch (Exception e) {
			Log.e(TAG,
					"Error while writing subscription to file: " + e.toString());
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					Log.e(TAG,
							"Error while writing subscription to file: "
									+ e.toString());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<Subscription> getSubscriptionListFromFile(Context context) {
		FilePathGenerator path_generator = new FilePathGenerator();
		String file_path = path_generator.generateSubscriptionDataFilePath(context);
		List<Subscription> subscriptionList = null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(file_path);
			ois = new ObjectInputStream(fis);
			subscriptionList = (List<Subscription>) ois.readObject();
			Log.d(TAG, "List of Subscription - ids: ");
			for (Subscription s : subscriptionList)
				Log.d(TAG, "" + s.getId());
		} catch (Exception e) {
			Log.e(TAG,
					"Error while reading subscription to file: " + e.toString());
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					Log.e(TAG,
							"Error while reading subscription to file: "
									+ e.toString());
				}
			}
		}
		return subscriptionList;
	}

	
	
	// public Boolean appendJSONArrayInJSONArray(String array_name, JSONArray
	// json_array, String fileLocation) throws AMException{
	// JSONObject data = readJSONObject(fileLocation);
	// try {
	// JSONArray jsonArray = data.getJSONArray(array_name);
	// jsonArray.put(jsonArray);
	// data.remove(array_name);
	// data.put(array_name, jsonArray);
	// deleteFile(fileLocation);
	// return writeJSONObject(data, fileLocation);
	// } catch (JSONException e) {
	// Log.d(TAG, "Inside TrainingDataJSONFile- appendTrainingData()");
	// }
	// return false;
	// }

	// public Boolean replaceData(String array_name, int index, JSONObject
	// jsonObject, String fileLocation) throws AMException{
	// JSONObject data = readJSONObject(fileLocation);
	// try {
	// JSONArray jsonArray = data.getJSONArray("DATA");
	// jsonArray.put(jsonObject);
	// data.remove("DATA");
	// data.put("DATA", jsonArray);
	// deleteFile(fileLocation);
	// return writeJSONObject(data, fileLocation);
	// } catch (JSONException e) {
	// Log.d(TAG, "Inside TrainingDataJSONFile- appendTrainingData()");
	// }
	// return false;
	// }
	
	
}
