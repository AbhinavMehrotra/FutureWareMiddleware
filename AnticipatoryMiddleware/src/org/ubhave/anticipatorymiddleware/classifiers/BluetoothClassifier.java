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
package org.ubhave.anticipatorymiddleware.classifiers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.storage.DBHelper;
import org.ubhave.anticipatorymiddleware.storage.SQLiteUtils;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.util.Log;

import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pullsensor.BluetoothData;
import com.ubhave.sensormanager.data.pullsensor.ESBluetoothDevice;

public class BluetoothClassifier  implements Classifier{

	private static final String TAG = "AnticipatoryManager";

	private final Context context;

	private final int sensor_id;
	
	public BluetoothClassifier (Context context){
		this.context =  context;
		this.sensor_id = Constants.SENSOR_TYPE_BLUETOOTH;
	}

	
	@Override
	public void trainClassifier(JSONArray array) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public ArrayList<String> getAllLabels() {
		ArrayList<String> address_list = new ArrayList<String>();
		for(ESBluetoothDevice device : getAllStoredDevices())
		{
			address_list.add(device.getBluetoothDeviceAddress());
		}
		return address_list;
	}

	
	@Override
	public String classifySensorData(SensorData data) {
		BluetoothData bluetooth_data = ((BluetoothData) data);
		ArrayList<ESBluetoothDevice> devices = bluetooth_data.getBluetoothDevices();
		JSONArray device_json_array = new JSONArray(); 
		for(ESBluetoothDevice device : devices)
		{
			try
			{
			JSONObject json_object = new JSONObject();
			json_object.put("ADDRESS", device.getBluetoothDeviceAddress());
			json_object.put("NAME", device.getBluetoothDeviceName());	
			json_object.put("TIME", device.getTimestamp());	
			json_object.put("RSSI", String.valueOf(device.getRssi()));	
			device_json_array.put(json_object);
			addNewBluetoothDevice(device);
			}
			catch(JSONException e)
			{
				Log.e(TAG, e.toString());
			}
		}
		return device_json_array.toString();
	}

	
	private void addNewBluetoothDevice(ESBluetoothDevice device){
		SQLiteUtils sql_utils = new SQLiteUtils();
		String db_name = sql_utils.getDbName();
		DBHelper db = new DBHelper(context, db_name);
		String label_data_table = sql_utils.getLabelDataTableName(sensor_id);
		try
		{
			boolean is_label_present = false;
			for(String label_data_string : db.getRecords(label_data_table))
			{
				JSONObject label_data_json = new JSONObject(label_data_string);
				if(label_data_json.getString("ADDRESS").equalsIgnoreCase(device.getBluetoothDeviceAddress())){
					is_label_present = true;
					break;
				}
			}
			if(!is_label_present){
				JSONObject json_object = new JSONObject();
				json_object.put("ADDRESS", device.getBluetoothDeviceAddress());
				json_object.put("NAME", device.getBluetoothDeviceName());	
				json_object.put("TIME", device.getTimestamp());	
				json_object.put("RSSI", String.valueOf(device.getRssi()));	
				db.addRecord(label_data_table, json_object.toString());
				Log.i(TAG, "New label details added to the DB.");
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.toString());
		}			
	}
	
	
	public ArrayList<ESBluetoothDevice>  getAllStoredDevices(){
		ArrayList<ESBluetoothDevice> devices = new ArrayList<ESBluetoothDevice>();
		SQLiteUtils sql_utils = new SQLiteUtils();
		String db_name = sql_utils.getDbName();
		DBHelper db = new DBHelper(context, db_name);
		String label_data_table = sql_utils.getLabelDataTableName(sensor_id);
		try
		{
			for(String data_string : db.getRecords(label_data_table))
			{
				JSONObject data = new JSONObject(data_string);
				long ts = data.getLong("TIME");
				String btAddr = data.getString("ADDRESS");
				String btName = data.getString("NAME");
				float btRssi = Float.parseFloat(data.getString("RSSI"));
				ESBluetoothDevice device = new ESBluetoothDevice(ts, btAddr, btName, btRssi);
				devices.add(device);				
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.toString());
		}	
		return devices;
	}

	
	public ESBluetoothDevice getDevice(String btAddress){
		ESBluetoothDevice device = null;
		SQLiteUtils sql_utils = new SQLiteUtils();
		String db_name = sql_utils.getDbName();
		DBHelper db = new DBHelper(context, db_name);
		String label_data_table = sql_utils.getLabelDataTableName(sensor_id);
		try
		{
			for(String data_string : db.getRecords(label_data_table))
			{
				JSONObject data = new JSONObject(data_string);
				String btAddr = data.getString("ADDRESS");
				if(btAddr.equalsIgnoreCase(btAddress))
				{
					long ts = data.getLong("TIME");
					String btName = data.getString("NAME");
					float btRssi = Float.parseFloat(data.getString("RSSI"));
					device = new ESBluetoothDevice(ts, btAddr, btName, btRssi);	
					break;
				}				
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.toString());
		}
		return device;
	}

}
