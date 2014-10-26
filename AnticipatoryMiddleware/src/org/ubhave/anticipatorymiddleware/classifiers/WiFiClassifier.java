package org.ubhave.anticipatorymiddleware.classifiers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.storage.DBHelper;
import org.ubhave.anticipatorymiddleware.storage.SQLiteUtils;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pullsensor.WifiData;
import com.ubhave.sensormanager.data.pullsensor.WifiScanResult;

public class WiFiClassifier  implements Classifier{

	private static final String TAG = "AnticipatoryManager";

	private final Context context;

	private final int sensor_id;
	
	public WiFiClassifier (Context context){
		this.context =  context;
		this.sensor_id = Constants.SENSOR_TYPE_WIFI;
	}

	
	@Override
	public void trainClassifier(JSONArray array) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public ArrayList<String> getAllLabels() {
		ArrayList<String> address_list = new ArrayList<String>();
		for(WifiScanResult device : getAllStoredDevices())
		{
			address_list.add(device.getSsid());
		}
		return address_list;
	}

	
	@Override
	public String classifySensorData(SensorData data) {
		WifiData wifi_data = ((WifiData) data);
		ArrayList<WifiScanResult> wifi_results = wifi_data.getWifiScanData();
		JSONArray device_json_array = new JSONArray(); 
		String connected_ssid = getConnectionSsid(context);
		for(WifiScanResult wifi_scan_result : wifi_results)
		{
			try
			{
			JSONObject json_object = new JSONObject();
			json_object.put("FREQUENCY", wifi_scan_result.getFrequency());
			json_object.put("LEVEL", wifi_scan_result.getLevel());	
			json_object.put("BSSID", wifi_scan_result.getBssid());	
			json_object.put("SSID", wifi_scan_result.getSsid());	
			json_object.put("CAPABILITIES", wifi_scan_result.getCapabilities());
			if(connected_ssid != null && connected_ssid.equalsIgnoreCase(wifi_scan_result.getSsid()))
			{
				json_object.put("CONNECTED", true);	
				addNewWiFiScanResult(wifi_scan_result, true);
			}
			else
			{
				json_object.put("CONNECTED", false);
				addNewWiFiScanResult(wifi_scan_result, false);	
			}
			
			device_json_array.put(json_object);
			}
			catch(JSONException e)
			{
				Log.e(TAG, e.toString());
			}
		}
		return device_json_array.toString();
	}

	
	private void addNewWiFiScanResult(WifiScanResult wifi_scan_result, boolean isConnected){
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
				if(label_data_json.getString("SSID").equalsIgnoreCase(wifi_scan_result.getSsid())){
					is_label_present = true;
					break;
				}
			}
			if(!is_label_present){
				JSONObject json_object = new JSONObject();
				json_object.put("FREQUENCY", wifi_scan_result.getFrequency());
				json_object.put("LEVEL", wifi_scan_result.getLevel());	
				json_object.put("BSSID", wifi_scan_result.getBssid());	
				json_object.put("SSID", wifi_scan_result.getSsid());	
				json_object.put("CAPABILITIES", wifi_scan_result.getCapabilities());
				json_object.put("CONNECTED", isConnected);
				db.addRecord(label_data_table, json_object.toString());
				Log.i(TAG, "New label details added to the DB.");
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.toString());
		}			
	}
	
	
	public ArrayList<WifiScanResult>  getAllStoredDevices(){
		ArrayList<WifiScanResult> wifi_scan_results = new ArrayList<WifiScanResult>();
		SQLiteUtils sql_utils = new SQLiteUtils();
		String db_name = sql_utils.getDbName();
		DBHelper db = new DBHelper(context, db_name);
		String label_data_table = sql_utils.getLabelDataTableName(sensor_id);
		try
		{
			for(String data_string : db.getRecords(label_data_table))
			{
				JSONObject data = new JSONObject(data_string);
				String ssid = data.getString("SSID");
				String bssid = data.getString("BSSID");
				String capabilities = data.getString("CAPABILITIES");
				int level = data.getInt("LEVEL");
				int frequency = data.getInt("FREQUENCY");
				WifiScanResult result = new WifiScanResult(ssid, bssid, capabilities, level, frequency);
				wifi_scan_results.add(result);				
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.toString());
		}	
		return wifi_scan_results;
	}

	
	public WifiScanResult getDevice(String wifi_ssid){
		WifiScanResult wifi_result = null;
		SQLiteUtils sql_utils = new SQLiteUtils();
		String db_name = sql_utils.getDbName();
		DBHelper db = new DBHelper(context, db_name);
		String label_data_table = sql_utils.getLabelDataTableName(sensor_id);
		try
		{
			for(String data_string : db.getRecords(label_data_table))
			{
				JSONObject data = new JSONObject(data_string);
				String ssid = data.getString("SSID");
				if(ssid.equalsIgnoreCase(wifi_ssid))
				{
					String bssid = data.getString("BSSID");
					String capabilities = data.getString("CAPABILITIES");
					int level = data.getInt("LEVEL");
					int frequency = data.getInt("FREQUENCY");
					wifi_result = new WifiScanResult(ssid, bssid, capabilities, level, frequency);
					break;
				}				
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.toString());
		}
		return wifi_result;
	}
	
	
	public static String getConnectionSsid(Context context) {
		  String ssid = null;
		  ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		  if (networkInfo.isConnected()) {
		    final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		    final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
		    if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
		      ssid = connectionInfo.getSSID();
		    }
		  }
		  return ssid;
		}

}
