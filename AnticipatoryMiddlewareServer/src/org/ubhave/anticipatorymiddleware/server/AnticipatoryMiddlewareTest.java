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
import org.ubhave.anticipatorymiddleware.server.AnticipatoryManager;
import org.ubhave.anticipatorymiddleware.server.ServerSettings;


public class AnticipatoryMiddlewareTest {

	public static void main(String[] args) {
		try{
			int port = Integer.parseInt(args[0]);
			String mqtt_url = args[1];
			String mqtt_username = args[2];
			String mqtt_pwd = args[3];
			String mongodb_ip = args[4];
			String mongodb_name = args[5];
			String mongodb_username = args[6];
			String mongodb_pwd = args[7];
			
			ServerSettings ss = new ServerSettings();
			ss.put(ServerSettings.Server_Port, port);
			ss.put(ServerSettings.Mqtt_Broker_URL, mqtt_url);
			ss.put(ServerSettings.Mqtt_Broker_USER_NAME, mqtt_username);
			ss.put(ServerSettings.Mqtt_Broker_PASSWORD, mqtt_pwd);
			ss.put(ServerSettings.Mongo_DB_IP, mongodb_ip);
			ss.put(ServerSettings.Mongo_DB_Name, mongodb_name);
			ss.put(ServerSettings.Mongo_DB_USER_NAME, mongodb_username);
			ss.put(ServerSettings.Mongo_DB_PASSWORD, mongodb_pwd);
			
			AnticipatoryManager am = AnticipatoryManager.getInstance();
			am.startServer(ss);
		}
		catch(UnknownHostException e){
			System.out.println(e.toString());
		} catch (AMException e) {
			System.out.println(e.toString());
		} catch (MqttException e) {
			System.out.println(e.toString());
		}

	}

}
