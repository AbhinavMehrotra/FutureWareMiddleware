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

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

/**
 * Background service to keep the connection with MQQT broker
 */
public class MQTTService extends Service {

	private final String TAG = "AnticipatoryManager";
	private MqttClient mqttClient;
	private final int START_STICKY = 1;
	private SharedPreferences sp;
	private Context context;
	private String broker_url;
	private String user_id;
	private	String mqtt_user_name;
	private String mqtt_password;

	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "MQTT service onStart");
		Log.i(TAG, broker_url);
		this.context = getApplicationContext();
		this.sp = new SharedPreferences(context);
		this.broker_url = sp.getString(sp.MQTT_BROKER_URL);
		this.user_id = sp.getString(sp.USER_ID);
		this.mqtt_user_name = sp.getString(sp.MQTT_USER_NAME);
		this.mqtt_password = sp.getString(sp.MQTT_USER_PASSWORD);
		Log.i(TAG, "User id: "+user_id);
		StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
		.permitNetwork()
		.build());
		try {
			
			MQTTManager m = new MQTTManager(context, broker_url, user_id, mqtt_user_name, mqtt_password);
			m.connect();
			m.publishToDevice("MQTT service started: "+user_id);
			m.subscribeDevice();
		} catch (MqttException e) {
			e.printStackTrace();
		}
        StrictMode.setThreadPolicy(old);
		return START_STICKY;
	}

	

	@Override
	public void onDestroy() {
		try {
			mqttClient.disconnect(0);
		} catch (MqttException e) {
			Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e(TAG,e.toString());
		}
	}

}
