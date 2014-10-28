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

public class ServerSettings {

	private int server_port;

	private String mongo_db_ip;

	private String mongo_db_name;

	private String mongo_db_user_name;

	private String mongo_db_password;

	private String mqtt_broker_url;

	private String mqtt_broker_user_name;

	private String mqtt_broker_password;


	public static String Server_Port = "Server_Port";

	public static String Mongo_DB_IP = "Mongo_DB_IP";

	public static String Mongo_DB_Name = "Mongo_DB_Name";

	public static String Mongo_DB_USER_NAME = "Mongo_DB_USER_NAME";

	public static String Mongo_DB_PASSWORD = "Mongo_DB_PASSWORD";

	public static String Mqtt_Broker_URL = "Mqtt_Broker_URL";

	public static String Mqtt_Broker_USER_NAME = "Mqtt_Broker_USER_NAME";

	public static String Mqtt_Broker_PASSWORD = "Mqtt_Broker_PASSWORD";

	public void put(String key, int value) throws IllegalArgumentException{
		if(key.equals(ServerSettings.Server_Port)){
			this.server_port = value;
		}
		else{
			throw new IllegalArgumentException("Value for the key: "+ key +" should not be of type int.");
		}
	}

	public void put(String key, String value){
		if(key.equals(ServerSettings.Mongo_DB_IP)){
			this.mongo_db_ip = value;
		}
		else if(key.equals(ServerSettings.Mongo_DB_Name)){
			this.mongo_db_name = value;
		}
		else if(key.equals(ServerSettings.Mongo_DB_USER_NAME)){
			this.mongo_db_user_name = value;
		}
		else if(key.equals(ServerSettings.Mongo_DB_PASSWORD)){
			this.mongo_db_password = value;
		}
		else if(key.equals(ServerSettings.Mqtt_Broker_URL)){
			this.mqtt_broker_url = value;
		}
		else if(key.equals(ServerSettings.Mqtt_Broker_USER_NAME)){
			this.mqtt_broker_user_name = value;
		}
		else if(key.equals(ServerSettings.Mqtt_Broker_PASSWORD)){
			this.mqtt_broker_password = value;
		}
		else{
			throw new IllegalArgumentException("Value for the key: "+ key +" should not be of type String.");
		}
	}

	public int getInt(String key){
		if(key.equals(ServerSettings.Server_Port)){
			return this.server_port;
		}
		else{
			throw new IllegalArgumentException("Value for the key: "+ key +" is not of type int.");
		}
	}

	public String getString(String key){
		if(key.equals(ServerSettings.Mongo_DB_IP)){
			return this.mongo_db_ip;
		}
		else if(key.equals(ServerSettings.Mongo_DB_Name)){
			return this.mongo_db_name;
		}
		else if(key.equals(ServerSettings.Mongo_DB_USER_NAME)){
			return this.mongo_db_user_name;
		}
		else if(key.equals(ServerSettings.Mongo_DB_PASSWORD)){
			return this.mongo_db_password;
		}
		else if(key.equals(ServerSettings.Mqtt_Broker_URL)){
			return this.mqtt_broker_url;
		}
		else if(key.equals(ServerSettings.Mqtt_Broker_USER_NAME)){
			return this.mqtt_broker_user_name;
		}
		else if(key.equals(ServerSettings.Mqtt_Broker_PASSWORD)){
			return this.mqtt_broker_password;
		}
		else{
			throw new IllegalArgumentException("Value for the key: "+ key +" is not of type String.");
		}
	}
}
