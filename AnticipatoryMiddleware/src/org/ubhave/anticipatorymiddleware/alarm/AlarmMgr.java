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
package org.ubhave.anticipatorymiddleware.alarm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class AlarmMgr {

	private static final String TAG = "AnticipatoryManager";

	public static final String ALARM_TYPE_SUBSCRIPTION = "ALARM_TYPE_SUBSCRIPTION";

	public static final String ALARM_TYPE_SERVER_UPDATE = "ALARM_TYPE_SERVER_UPDATE";

	public static final String ALARM_TYPE_DATA_COLLECTION = "ALARM_TYPE_DATA_COLLECTION";

	public static final String ALARM_TYPE_APP_KEEP_ALIVE = "ALARM_TYPE_APP_KEEP_ALIVE";

	public static final int ALARM_EXPIRY_CONTINUOUS = -1;

	public final int DATA_COLLECTION_ALARM_SUBSCRIPTION_ID = 54321;

	private AlarmManager alarmMgr;

	private Context context;

	public AlarmMgr(Context context){
		this.context = context;
	}


	/**
	 * Creates a new alarm for the given subscription
	 * @param alarm_type : AlarmMgr constants
	 * @param subscription_id : unique id for the predictor subscription
	 * @param trigger_start_time : time in seconds that the alarm should first go off (PP-NP)
	 * @param trigger_interval_time : interval in seconds between subsequent repeats of the alarm. (Frequency_Period)
	 * @param alarm_expiry_time : The clock time at which the alarm should be removed. 
	 * For one-time alarm, alarm_expiry_time should be equal to Current-time + Prediction_Period.
	 * For continuous alarm with no Subscription_Lease_Time, alarm_expiry_time should be -1.
	 */
	public void setNewAlarm(String alarm_type, int subscription_id, long trigger_start_time, 
			long trigger_interval_time, long alarm_expiry_time, String predictor_name_or_path){
		Log.d(TAG, "Inside AlarmMgr setAlarm()");
		alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra("ALARM_TYPE", alarm_type);
		intent.putExtra("SUBSCRIPTION_ID", subscription_id);
		intent.putExtra("TRIGGER_START_TIME", trigger_start_time);
		intent.putExtra("TRIGGER_INTERVAL_TIME", trigger_interval_time);
		intent.putExtra("ALARM_EXPIRY_TIME", alarm_expiry_time);
		intent.putExtra("PREDICTOR_NAME_OR_PATH", predictor_name_or_path);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, subscription_id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

//		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
//				Calendar.getInstance().getTimeInMillis()+trigger_start_time, 
//				(long)trigger_interval_time, alarmIntent);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				Calendar.getInstance().getTimeInMillis()+trigger_start_time*1000, 
				trigger_interval_time*1000, alarmIntent);



		Log.v(TAG, "New alarm set!!");
		Log.v(TAG, "trigger_start_time: "+trigger_start_time);
		Log.v(TAG, "trigger_interval_time: "+trigger_interval_time*1000);
		Log.v(TAG, "predictor_name_or_path: "+predictor_name_or_path);
		Log.v(TAG, "subscription_id: "+subscription_id);
		
		// Enable AmBootReceiver to automatically restart the alarm on device reboot
		ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);           
	}
	

	/**
	 * Removes the alarm for the given subscription
	 * @param subscription_id
	 */
	public void removeAlarm(int subscription_id){
		Log.d(TAG, "Inside AlarmMgr cancelAlarm()");
		alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, subscription_id, intent, 0);
		alarmMgr.cancel(alarmIntent);

		// Disable AMBootReceiver so that now, it doesn't automatically restart the alarm on reboot
		ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	}

	/**
	 * Resets the alarm
	 * @param subscription_id
	 * @param start_time
	 * @param frequency_time
	 * @param lease_time
	 */
	public void resetAlarm(String alarm_type, int subscription_id, long start_time, long frequency_time, long lease_time, String predictor_name_or_path){
		Log.d(TAG, "Inside AlarmMgr resetAlarm()");		
		//remove
		removeAlarm(subscription_id);		
		//start
		setNewAlarm(alarm_type, subscription_id, start_time, frequency_time, lease_time, predictor_name_or_path);
	}

}
