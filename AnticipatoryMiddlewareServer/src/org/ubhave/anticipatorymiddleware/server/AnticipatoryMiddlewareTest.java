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
