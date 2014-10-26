package org.ubhave.anticipatorymiddleware.event;

import org.ubhave.anticipatorymiddleware.alarm.AlarmMgr;
import org.ubhave.anticipatorymiddleware.alarm.AlarmReceiver;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class KeepAliveService extends IntentService{

	private static final String TAG = "AnticipatoryManager";
	
	public KeepAliveService() {
		super("KeepAliveService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Inside KeepAliveService!!");
		String alarm_type = intent.getExtras().getString("ALARM_TYPE");

		if(alarm_type == null){
			Log.e(TAG, "Why is the alarm type is null??");
		}
		else if(alarm_type.equals(AlarmMgr.ALARM_TYPE_APP_KEEP_ALIVE)){
			Log.d(TAG, "The alarm is alive....!!");
		}
		else{
			Log.e(TAG, "Unknown alarm type!!");
		}
		AlarmReceiver.completeWakefulIntent(intent);
		Log.d(TAG, "Wakeful Intent released for intent of alarm: "+ alarm_type);
		return; 
		
	}

}
