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
