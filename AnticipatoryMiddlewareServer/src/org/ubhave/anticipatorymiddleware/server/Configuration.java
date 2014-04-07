package org.ubhave.anticipatorymiddleware.server;


public class Configuration {

	private static String mqttBrokerUrl;
	private static String tcpIP;
	private static int tcpPort;
	private static String mongoDBIP;
	private static String mongoDBName;
	

	public void setServerPort(String ip, int port){
		Configuration.tcpIP = ip;
		Configuration.tcpPort = port;
	}

	public static String getTcpIP() {
		return tcpIP;
	}

	public static int getTcpPort() {
		return tcpPort;
	}

	public void setMongoDBConfig(String ip, String db_name){
		Configuration.mongoDBIP = ip;
		Configuration.mongoDBName = db_name;
	}
	
	public static String getMongoDBIP() {
		return mongoDBIP;
	}
	
	public static String getMongoDBName() {
		return mongoDBName;
	}

	public static void setMQTTBrokerURL(String url){
		Configuration.mqttBrokerUrl = url;
	}
	
	public static String getMqttBrokerUrl() {
		return mqttBrokerUrl;
	}
}
