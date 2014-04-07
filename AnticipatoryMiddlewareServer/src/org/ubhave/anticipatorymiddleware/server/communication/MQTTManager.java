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
