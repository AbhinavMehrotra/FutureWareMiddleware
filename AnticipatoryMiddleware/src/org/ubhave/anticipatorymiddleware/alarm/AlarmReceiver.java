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
