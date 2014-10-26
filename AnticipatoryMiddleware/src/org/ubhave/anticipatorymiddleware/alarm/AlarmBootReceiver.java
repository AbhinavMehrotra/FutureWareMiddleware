package org.ubhave.anticipatorymiddleware.alarm;

import java.util.List;

import org.ubhave.anticipatorymiddleware.AMException;
import org.ubhave.anticipatorymiddleware.AnticipatoryManager;
import org.ubhave.anticipatorymiddleware.communication.MQTTService;
import org.ubhave.anticipatorymiddleware.communication.ServerSettings;
import org.ubhave.anticipatorymiddleware.predictor.Configuration;
import org.ubhave.anticipatorymiddleware.privacy.PrivacyManager;
import org.ubhave.anticipatorymiddleware.sensors.SensorManager;
import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;
import org.ubhave.anticipatorymiddleware.subscribe.Subscription;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmBootReceiver extends BroadcastReceiver {

	private final String TAG = "AnticipatoryManager";

	@Override
	public void onReceive(Context context, Intent intent) {
		/*TODO: 
		 * 1. Set alarm for all the subscriptions
		 * 3. Set alarm for data sampling
		 * 4. start mqtt service
		 */
		
		
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
		{
			AlarmMgr alarm = new AlarmMgr(context.getApplicationContext());
			//set alarm for subscription
			try {
				AnticipatoryManager a_manager = AnticipatoryManager.getAnticipatoryManager(context);
				List<Subscription> subscriptions = a_manager.getAllSubscriptions();
				for(Subscription s: subscriptions){
					int subscription_id = s.getId();
					Configuration config = s.getConfiguration();
					long trigger_start_time = (config.getLong(Configuration.Prediction_Preiod) - config.getLong(Configuration.Notification_Preiod))*60;
					long trigger_interval_time = (config.getLong(Configuration.Prediction_Frequency))*60;
					long alarm_expiry_time = config.getLong(Configuration.Subscription_Lease_Preiod);
					String predictor_name_or_path = config.getString(Configuration.Predictor_Name_Or_Path);
					alarm.setNewAlarm(AlarmMgr.ALARM_TYPE_SUBSCRIPTION, subscription_id, trigger_start_time, trigger_interval_time, alarm_expiry_time, predictor_name_or_path);
				}				
			} catch (AMException e) {
				Log.e(TAG, "Error in AlarmBootReceiver: " + e.toString());
			}			
			
			//check for training data and set alarm for data sampling
			SharedPreferences sp = new SharedPreferences(context.getApplicationContext());
			if(sp.getBoolean(sp.SAMPLING_STARTED_FOR_SENSORS)){
				SensorManager sm = new SensorManager(context.getApplicationContext());
				long csr = sm.getContextSamplingRate() * 60 ;
				alarm.setNewAlarm(AlarmMgr.ALARM_TYPE_DATA_COLLECTION, 0, csr, csr, AlarmMgr.ALARM_EXPIRY_CONTINUOUS, null);
			}
			
			startMqttService(context);
		}
	}
	

	private boolean startMqttService(Context app_context){
		//TODO: check for all  req before starting
		ServerSettings ss = new ServerSettings(app_context);
		int server_port = ss.getInt(ss.SERVER_PORT);
		String server_ip = ss.getString(ss.SERVER_IP);
		String mqtt_broker_url = ss.getString(ss.MQTT_BROKER_URL);
		String mqtt_user_name = ss.getString(ss.MQTT_USER_NAME);
		String mqtt_user_password = ss.getString(ss.MQTT_USER_PASSWORD);
		if(server_port == 0 || server_ip.equals("") || mqtt_broker_url.equalsIgnoreCase("")
				 || mqtt_user_name.equals("") || mqtt_user_password.equals("")){
			return false;
		}
		
		SharedPreferences sp = new SharedPreferences(app_context);
		PrivacyManager pm = new PrivacyManager(app_context);
		
		if((!sp.getBoolean(sp.IS_MQTT_SERVICE_STARTED)) && pm.isDataTransmissionToServerEnabled()){
			app_context.startService(new Intent(app_context, MQTTService.class));
			sp.add(sp.IS_USER_REGISTERED, true);
			return true;
		}
		return false;
	}
}
