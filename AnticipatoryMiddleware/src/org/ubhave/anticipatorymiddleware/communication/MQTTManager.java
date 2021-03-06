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

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;

import android.content.Context;
import android.util.Log;

/**
 * MQTTManager class provide methods to connect, subscribe, publish, and listen to MQTT broker
 */
public class MQTTManager {

	private final String TAG = "AnticipatoryManager";
	private final String broker_url;
	private final MqttClient mqttClient;
	private final String topic;
	private final int keep_alive_interval = 60*5;
	private final int connection_time_out = 10;
	private final MqttConnectOptions opt;
	private final Context context;
	
	/**
	 * Constructor
	 * @param context Application context
	 * @param Sting Device id
	 * @throws MqttException
	 */
	protected MQTTManager(Context context, String broker_url, String user_id, 
			String mqtt_user_name, String mqtt_password) throws MqttException {
		this.broker_url= broker_url;
		this.context=context;
		this.topic="topic"+user_id;
		opt=new MqttConnectOptions();
		opt.setUserName(mqtt_user_name);
		opt.setPassword(mqtt_password.toCharArray());
		opt.setKeepAliveInterval(keep_alive_interval);
		opt.setConnectionTimeout(connection_time_out);
		mqttClient = new MqttClient(this.broker_url, user_id, new MemoryPersistence());
//		mqttClient.setCallback(new MQTTCallback(this.broker_url, user_id, this.topic));
		mqttClient.setCallback(new MQTTCallback());
	}
	
	/**
	 * Connects to the MQTT broker service on server side.
	 */
	public void connect(){
		try {
			mqttClient.connect(opt);
		} catch (MqttException e) {
			Log.e(TAG, "Error while connecting to mqtt broker: "+e.toString());
			try {
				mqttClient.wait(connection_time_out);
			} catch (InterruptedException e1) {
				Log.e(TAG, "Error while waiting: "+e.toString());
			}
			connect();
		}
	}
	
	/**
	 * Subscribes the device to the topic provided via constructor
	 */
	public void subscribeDevice(){
		try {
			mqttClient.subscribe(this.topic);
		} catch (MqttException e) {
			Log.e(TAG, "Error while subscribing to mqtt broker: "+e.toString());
		}
	}

	/**
	 * Publishes the message to the MQTT broker service.
	 * @param String Message that needs to be published 
	 */
	public void publishToDevice(String message){
		try {
			MqttTopic mtopic=mqttClient.getTopic(this.topic);
			Log.i(TAG, "Published to : "+mtopic);
			MqttMessage msg= new MqttMessage(message.getBytes());
			mtopic.publish(msg);
		} catch (MqttException e) {
			Log.e(TAG, "Error while publishing to mqtt broker: "+e.toString());
		}
	}
	
	
	/**
	 * Inner class for mqtt callback
	 */
	public class MQTTCallback implements MqttCallback{

//		private final String TAG = "AnticipatoryManager";
//		private String BROKER_URL;
//		private String deviceId;                  
//		private String TOPIC;
//		private MqttClient mqttClient;
//
//		public MQTTCallback(String BROKER_URL, String deviceId, String TOPIC) {
//			this.BROKER_URL= BROKER_URL;
//			this.deviceId=deviceId;
//			this.TOPIC=TOPIC;
//		}
		
		public void connectionLost(Throwable arg0) {
			connect();			
		}

		public void deliveryComplete(MqttDeliveryToken arg0) {
			if(arg0==null)
				System.out.print("Message delivered");			
		}

		public void messageArrived(MqttTopic topic, MqttMessage message)
				throws Exception {
			// topic   --> device id		
			// message --> published message
			Log.i(TAG, "MQTT message arrived: "+ topic.toString()+":"+message.toString());
			new MqttMessageParser(context, topic, message).run();
		}


	}


}
