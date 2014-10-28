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
