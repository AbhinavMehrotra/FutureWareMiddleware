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
package org.ubhave.anticipatorymiddleware.server.communication;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;
import org.json.JSONObject;

public class MQTTManager {

	private final String mqtt_broker_url; //="tcp://mqtt.cs.bham.ac.uk:1883";
	private final MqttClient mqttClient;
	private final String clientId;
	private final String topic;
	private final int keepAliveInterval=60*5;
	private final MqttConnectOptions opt;



	public MQTTManager(String user_id, String mqtt_broker_url, String mqtt_broker_user_name, String mqtt_broker_password) throws MqttException{
		this.mqtt_broker_url = mqtt_broker_url;
		this.clientId="ANTICIPATORY_MIDDLEWARE";
		this.topic="USER_"+user_id;
		this.opt = new MqttConnectOptions();
		opt.setKeepAliveInterval(this.keepAliveInterval);
		opt.setConnectionTimeout(10);
		opt.setUserName(mqtt_broker_user_name); //"axm_mos"
		opt.setPassword(mqtt_broker_password.toCharArray()); //"Mos2013"
		mqttClient = new MqttClient(this.mqtt_broker_url, this.clientId, new MemoryPersistence());
		mqttClient.setCallback(new MQTTCallback(this.mqtt_broker_url, this.clientId, this.topic));
	}

	public void connectionTest() throws MqttException{
		this.mqttClient.connect(opt);
	}


	private void connect(){
		try {
			mqttClient.connect(opt);
			System.out.print("Connected!");
		} catch (MqttException e) {
			System.out.print("Connection error!! "+e.toString());
			connect();
		}
	}


	public void subscribeToDevice(){
		try {
			this.connect();
			this.mqttClient.subscribe(this.topic);
			System.out.print("Subscribed");
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void publishToDevice(JSONObject json_object) throws MqttPersistenceException, MqttException{
		this.connect();	
		MqttTopic mqtt_topic = mqttClient.getTopic(this.topic);
		System.out.println("Publish to topic="+topic);
		MqttMessage mqtt_msg= new MqttMessage(json_object.toString().getBytes());
		mqtt_topic.publish(mqtt_msg);
		System.out.println("Published");
		mqttClient.disconnect();
	}


	/**
	 * Callback class to receive message
	 */
	public class MQTTCallback implements MqttCallback{

		public String BROKER_URL;
		public String deviceId;                  
		public String TOPIC;
		public MqttClient mqttClient;

		public MQTTCallback(String BROKER_URL, String deviceId, String TOPIC) {
			this.BROKER_URL= BROKER_URL;
			this.deviceId=deviceId;
			this.TOPIC=TOPIC;
		}

		public void connectionLost(Throwable arg0) {
			System.out.println("Connection lost!");			
		}

		public void deliveryComplete(MqttDeliveryToken arg0) {
			if(arg0==null)
				System.out.println("Message delivered");			
		}

		public void messageArrived(MqttTopic arg0, MqttMessage arg1)
				throws Exception {
			// argo-> device id
			//arg1 --> message
			//for testing
			System.out.print(arg0.toString()+arg1.toString());

		}
	}


}
