package org.ubhave.anticipatorymiddleware.classifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.storage.DBHelper;
import org.ubhave.anticipatorymiddleware.storage.SQLiteUtils;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.util.Log;

import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pullsensor.ApplicationData;

public class ApplicationClassifier  implements Classifier{

	private static final String TAG = "AnticipatoryManager";

	private final Context context;

	private final int sensor_id;

	public ApplicationClassifier (Context context){
		this.context =  context;
		this.sensor_id = Constants.SENSOR_TYPE_APPLICATION;
	}


	@Override
	public void trainClassifier(JSONArray array) {
		// DO NOTHING
	}


	@Override
	public ArrayList<String> getAllLabels() {
		ArrayList<String> labels_list = new ArrayList<String>();
		for(Map.Entry<String, String> e : getAllAppDetails().entrySet())
		{
			labels_list.add(e.getKey());
		}
		return labels_list;
	}


	@Override
	public String classifySensorData(SensorData data) {
		ArrayList<String> current_app_list = ((ApplicationData) data).getApplications();
		SharedPreferences sp = new SharedPreferences(context);
		JSONArray classified_apps = new JSONArray();
		String old_app_labels_string = sp.getString(sp.PREVIOUS_APP_STACK);
		JSONArray old_app_labels;
		if(old_app_labels_string.equals(""))
		{
			old_app_labels = new JSONArray();
		}
		else
		{
			try
			{
				old_app_labels = new JSONArray(old_app_labels_string);
			} 
			catch (JSONException e) 
			{
				old_app_labels = new JSONArray();
			}
		}

		JSONArray new_app_labels = new JSONArray();
		for(String app : current_app_list)
		{
			String label = app.substring(1, app.indexOf(")"));
			new_app_labels.put(label);			
		}

		ArrayList<String> old_stack = new ArrayList<String>();
		ArrayList<String> new_stack = new ArrayList<String>();

		try
		{
			for(int i=0; i<old_app_labels.length(); i++)
			{
				old_stack.add(old_app_labels.getString(i));
			}
			for(int i=0; i<new_app_labels.length(); i++)
			{
				new_stack.add(new_app_labels.getString(i));
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.toString());
		}
		Set<String> recent_used_app_labels = compareStacks(old_stack, new_stack);	
		Set<String> ignored_apps = getAppsToBeIgmored();
		for(String app : current_app_list)
		{
			String package_name = app.substring(app.indexOf(")")+2);
			String label = app.substring(1, app.indexOf(")"));
			if(recent_used_app_labels.contains(label) && !ignored_apps.contains(label))
			{
				addNewAppDeatils(label, package_name);				
				classified_apps.put(label);				
			}	
		}	
		sp.add(sp.PREVIOUS_APP_STACK, new_app_labels.toString());
		Log.i(TAG, "Old app stack: " + old_app_labels.toString());
		Log.i(TAG, "New app stack: " + new_app_labels.toString());
		Log.i(TAG, "Recently used apps: " + classified_apps.toString());
		try {
			if(classified_apps.length()>0)
			{
				Log.i(TAG, "Recently used app: " + classified_apps.getString(0));
				return classified_apps.getString(0);
			}
			else
			{
				return "";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "";
	}


	private void addNewAppDeatils(String label, String package_name){
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
				if(label_data_json.getString("LABEL").equalsIgnoreCase(label)){
					is_label_present = true;
					break;
				}
			}
			if(!is_label_present){
				JSONObject json_object = new JSONObject();
				json_object.put("LABEL", label);
				json_object.put("PACKAGE", package_name);	
				db.addRecord(label_data_table, json_object.toString());
				Log.i(TAG, "New label details added to the DB.");
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.toString());
		}			
	}


	public HashMap<String,String> getAllAppDetails(){
		HashMap<String,String> apps = new HashMap<String, String>();
		SQLiteUtils sql_utils = new SQLiteUtils();
		String db_name = sql_utils.getDbName();
		DBHelper db = new DBHelper(context, db_name);
		String label_data_table = sql_utils.getLabelDataTableName(sensor_id);
		try
		{
			for(String label_data_string : db.getRecords(label_data_table))
			{
				JSONObject label_data_json = new JSONObject(label_data_string);
				apps.put(label_data_json.getString("LABEL"), label_data_json.getString("PACKAGE"));				
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.toString());
		}	
		Log.i(TAG, "App_details: "+ apps);
		return apps;
	}


	public String getAppPackageName(String label){
		String package_name = "UNKNOWN";
		SQLiteUtils sql_utils = new SQLiteUtils();
		String db_name = sql_utils.getDbName();
		DBHelper db = new DBHelper(context, db_name);
		String label_data_table = sql_utils.getLabelDataTableName(sensor_id);
		try
		{
			for(String label_data_string : db.getRecords(label_data_table))
			{
				JSONObject label_data_json = new JSONObject(label_data_string);
				if(label_data_json.getString("LABEL").equalsIgnoreCase(label)){
					package_name = label_data_json.getString("PACKAGE");
					break;
				}			
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.toString());
		}
		return package_name;
	}


	private Set<String> compareStacks(ArrayList<String> old_stack, ArrayList<String> new_stack)
	{
		Set<String> compared_stack = new HashSet<String>();
		if(old_stack.size() == 0)
		{
			return compared_stack;
		}
		//if new stack has extra elements
		if(old_stack.size() < new_stack.size())
		{

			for(int i= 0 ; i< new_stack.size(); i++)
			{
				if(!old_stack.contains(new_stack.get(i)))
				{
					compared_stack.add(new_stack.get(i));
				}
			}
			return compared_stack;
		}

		int index_counter_for_old = old_stack.size() -1;
		for(int i=new_stack.size()-1; i>=0; i--)
		{
			String new_stack_element = new_stack.get(i);
			String old_stack_element = old_stack.get(index_counter_for_old--);
			if(new_stack_element.equalsIgnoreCase(old_stack_element))
			{
				continue;
			}
			else
			{
				if(old_stack.contains(new_stack_element))
				{
					int j;
					for(j = old_stack.indexOf(new_stack_element)-1; j>=0; j--)
					{
						i = i-1;
						if(i<0)
						{
							break;
						}
						String s3 = new_stack.get(i);
						String s4 = old_stack.get(j);
						if(!s3.equalsIgnoreCase(s4))
						{
							break;
						}						
					}
					if(j<0)
					{
						i = i - 1;
					}
					for(j=i; j>=0; j--)
					{
						compared_stack.add(new_stack.get(j));
					}
					return compared_stack;
				}
				else
				{
					for(int j=i; j>=0; j--)
					{
						compared_stack.add(new_stack.get(j));
					}
					return compared_stack;
				}
			}	
		}


		return compared_stack;
	}

	public void setAppsToBeIgmored(Set<String> apps)
	{
		SharedPreferences sp = new SharedPreferences(context);
		sp.addStringSet(sp.APPS_IGNORED, apps);
	}

	public Set<String> getAppsToBeIgmored()
	{
		SharedPreferences sp = new SharedPreferences(context);
		return sp.getStringSet(sp.APPS_IGNORED);
	}
}
