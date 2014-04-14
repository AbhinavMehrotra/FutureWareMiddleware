package org.ubhave.anticipatorymiddleware.server;

import java.net.UnknownHostException;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.ubhave.anticipatorymiddleware.server.communication.MQTTManager;
import org.ubhave.anticipatorymiddleware.server.communication.TCPServer;
import org.ubhave.anticipatorymiddleware.server.database.MongoDBManager;



public class AnticipatoryManager {

	private static AnticipatoryManager a_manager;
	
	private TCPServer tcp_server;
	
	private ServerSettings server_settings;
	
	public static AnticipatoryManager getInstance(){
		if(a_manager == null){
			a_manager = new AnticipatoryManager();
		}
		return a_manager;
	}
	
	private AnticipatoryManager(){}
	
	public void startServer(ServerSettings server_settings) throws UnknownHostException, AMException, MqttException{
		this.server_settings = server_settings;
		//before starting server check for MongoDB connection
		getMongoDBManager();
		//before starting server check for MQTT connection
		getMQTTManager("test").connectionTest();
		
		//start server now
		int server_port = this.server_settings.getInt(ServerSettings.Server_Port);
		tcp_server = new TCPServer(server_port);
		tcp_server.start();
		
	}
	
	public void stopServer(){
		tcp_server.stopServer();
	}
	
	
	public MongoDBManager getMongoDBManager() throws UnknownHostException, AMException{
		String mongo_db_ip = this.server_settings.getString(ServerSettings.Mongo_DB_IP);
		String mongo_db_name = this.server_settings.getString(ServerSettings.Mongo_DB_Name);
		String mongo_db_user_name = this.server_settings.getString(ServerSettings.Mongo_DB_USER_NAME);
		String mongo_db_password = this.server_settings.getString(ServerSettings.Mongo_DB_PASSWORD);
		return new MongoDBManager(mongo_db_ip, mongo_db_name, mongo_db_user_name, mongo_db_password);
	}
	
	public MQTTManager getMQTTManager(String user_id) throws MqttException{
		String mqtt_broker_url = this.server_settings.getString(ServerSettings.Mqtt_Broker_URL);
		String user_name = this.server_settings.getString(ServerSettings.Mqtt_Broker_USER_NAME);
		String password = this.server_settings.getString(ServerSettings.Mqtt_Broker_PASSWORD);
		return new MQTTManager(user_id, mqtt_broker_url, user_name, password);
	}
}
