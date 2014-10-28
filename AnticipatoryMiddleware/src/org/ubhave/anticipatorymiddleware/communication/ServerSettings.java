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
