package org.ubhave.anticipatorymiddleware.alarm;

import org.ubhave.anticipatorymiddleware.event.DataCollectionService;
import org.ubhave.anticipatorymiddleware.event.SubscriptionService;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class AlarmReceiver extends WakefulBroadcastReceiver {

	private static final String TAG = "AnticipatoryManager";

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.d(TAG, "Inside AlarmReceiver onReceive()");
		int subscription_id = intent.getExtras().getInt("SUBSCRIPTION_ID"); 
		String alarm_type = intent.getExtras().getString("ALARM_TYPE"); 
		long alarm_expiry_time = intent.getExtras().getLong("ALARM_EXPIRY_TIME");
		long trigger_start_time = intent.getExtras().getLong("TRIGGER_START_TIME");
		long trigger_interval_time = intent.getExtras().getLong("TRIGGER_INTERVAL_TIME");
		String predictor_name_or_path = intent.getExtras().getString("PREDICTOR_NAME_OR_PATH");


		Log.d(TAG, "alarm_type : "+ alarm_type);
		
		if(alarm_type.equals(AlarmMgr.ALARM_TYPE_SUBSCRIPTION)){
			Intent service = new Intent(context, SubscriptionService.class);
			service.putExtra("SUBSCRIPTION_ID", subscription_id);
			service.putExtra("ALARM_TYPE", alarm_type);
			service.putExtra("ALARM_EXPIRY_TIME", alarm_expiry_time);
			service.putExtra("TRIGGER_START_TIME", trigger_start_time);
			service.putExtra("TRIGGER_INTERVAL_TIME", trigger_interval_time);
			service.putExtra("PREDICTOR_NAME_OR_PATH", predictor_name_or_path);
			// Start the service, keeping the device awake while it is launching.
			startWakefulService(context, service);	
		}	
		else if(alarm_type.equals(AlarmMgr.ALARM_TYPE_DATA_COLLECTION)){
			Intent service = new Intent(context, DataCollectionService.class);
			service.putExtra("SUBSCRIPTION_ID", subscription_id);
			service.putExtra("ALARM_TYPE", alarm_type);
			service.putExtra("ALARM_EXPIRY_TIME", alarm_expiry_time);
			service.putExtra("TRIGGER_START_TIME", trigger_start_time);
			service.putExtra("TRIGGER_INTERVAL_TIME", trigger_interval_time);
			service.putExtra("PREDICTOR_NAME_OR_PATH", predictor_name_or_path);
			// Start the service, keeping the device awake while it is launching.
			startWakefulService(context, service);	
		}

//		else if(alarm_type.equals(AlarmMgr.ALARM_TYPE_APP_KEEP_ALIVE)){
//			Intent service = new Intent(context, KeepAliveService.class);
//			service.putExtra("SUBSCRIPTION_ID", subscription_id);
//			service.putExtra("ALARM_TYPE", alarm_type);
//			service.putExtra("ALARM_EXPIRY_TIME", alarm_expiry_time);
//			service.putExtra("TRIGGER_START_TIME", trigger_start_time);
//			service.putExtra("TRIGGER_INTERVAL_TIME", trigger_interval_time);
//			service.putExtra("PREDICTOR_NAME_OR_PATH", predictor_name_or_path);
//			// Start the service, keeping the device awake while it is launching.
//			startWakefulService(context, service);			
//		}
		else{
			Log.e(TAG, "Unknown alarm type!!");
		}

	}


}
