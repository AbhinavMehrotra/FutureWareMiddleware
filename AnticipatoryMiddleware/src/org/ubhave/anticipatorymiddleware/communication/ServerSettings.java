package org.ubhave.anticipatorymiddleware.communication;

import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;

import android.content.Context;

public class ServerSettings {


	private final SharedPreferences sp; 
	public final String SERVER_PORT = "SERVER_PORT"; 
	public final String SERVER_IP = "SERVER_IP"; 
	public final String MQTT_BROKER_URL = "MQTT_BROKER_URL"; 
	public final String MQTT_USER_NAME = "MQTT_USER_NAME"; 
	public final String MQTT_USER_PASSWORD = "MQTT_USER_PASSWORD"; 


	public ServerSettings(Context app_context){
		sp = new SharedPreferences(app_context);
	}
	
	public void put(String key, int value) throws IllegalArgumentException{
		if(key.equals(this.SERVER_PORT)){
			sp.add(sp.SERVER_PORT, value);
		}
		else{
			throw new IllegalArgumentException("Value for the key: "+ key +" should not be of type int.");
		}
	}

	public void put(String key, String value){
		if(key.equals(this.SERVER_IP)){
			sp.add(sp.SERVER_IP, value);
		}
		else if(key.equals(this.MQTT_BROKER_URL)){
			sp.add(sp.MQTT_BROKER_URL, value);
		}
		else if(key.equals(this.MQTT_USER_NAME)){
			sp.add(sp.MQTT_USER_NAME, value);
		}
		else if(key.equals(this.MQTT_USER_PASSWORD)){
			sp.add(sp.MQTT_USER_PASSWORD, value);
		}
		else{
			throw new IllegalArgumentException("Value for the key: "+ key +" should not be of type String.");
		}
	}

	public int getInt(String key){
		if(key.equals(this.SERVER_PORT)){
			return sp.getInt(sp.SERVER_PORT);
		}
		else{
			throw new IllegalArgumentException("Value for the key: "+ key +" is not of type int.");
		}
	}

	public String getString(String key){
		if(key.equals(this.SERVER_IP)){
			return sp.getString(sp.SERVER_IP);
		}
		else if(key.equals(this.MQTT_BROKER_URL)){
			return sp.getString(sp.MQTT_BROKER_URL);
		}
		else if(key.equals(this.MQTT_USER_NAME)){
			return sp.getString(sp.MQTT_USER_NAME);
		}
		else if(key.equals(this.MQTT_USER_PASSWORD)){
			return sp.getString(sp.MQTT_USER_PASSWORD);
		}
		else{
			throw new IllegalArgumentException("Value for the key: "+ key +" is not of type String.");
		}
	}
}
