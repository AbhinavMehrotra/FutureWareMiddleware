package org.ubhave.anticipatorymiddleware.sensors;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;


public class GoogleActivityRecognition implements ConnectionCallbacks, OnConnectionFailedListener{

	private static final String TAG = "AnticipatoryManager";

	public final long SAMPLING_INTERVAL_MILLISECONDS;

	private PendingIntent activityRecognitionPendingIntent;

	private ActivityRecognitionClient activityRecognitionClient;

	private boolean inProgress;

	public enum REQUEST_TYPE {START, STOP};

	private REQUEST_TYPE requestType;
	
	private static GoogleActivityRecognition instance;
	
	public static GoogleActivityRecognition getInstance(Context context, long time_interval)
	{
		if(instance == null)
			instance = new GoogleActivityRecognition(context, time_interval);
		return instance;
	}

	private GoogleActivityRecognition(Context context, long time_interval)
	{
		Log.i(TAG, "Creating GAR instance");
		this.SAMPLING_INTERVAL_MILLISECONDS = time_interval;
		inProgress=false;
		activityRecognitionClient = new ActivityRecognitionClient(context, this, this);		
		Intent intent = new Intent(context, ActivityRecognitionIntentService.class);
		activityRecognitionPendingIntent = 
				PendingIntent.getService(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) 
	{
		inProgress = false;		
	}

	@Override
	public void onConnected(Bundle arg0) 
	{
		switch (requestType) {
		case START : {
			activityRecognitionClient.requestActivityUpdates(SAMPLING_INTERVAL_MILLISECONDS,activityRecognitionPendingIntent);
			Log.d(TAG, "Google Activity Recognition Strated!");
			break;
		}
		case STOP:{
			activityRecognitionClient.removeActivityUpdates(activityRecognitionPendingIntent);
			Log.d(TAG, "Google Activity Recognition Stopped!");
			break;
		}
		}	
		inProgress = false;	
		activityRecognitionClient.disconnect();		
	}

	@Override
	public void onDisconnected() 
	{
		inProgress = false;
		// Delete the client
		activityRecognitionClient = null;
	}

	public void startSensing() 
	{
		requestType = REQUEST_TYPE.START;
		if (!inProgress) {
			inProgress = true;
			activityRecognitionClient.connect();
		} else {
			Log.i(TAG, "Already connected");
		}
	}

	public void stopSensing() 
	{
		requestType = REQUEST_TYPE.STOP;
		if (!inProgress) {
			inProgress = true;
			activityRecognitionClient.connect();
		} else {
			Log.i(TAG,  "Already disconnected");
		}
	}
	
	private static String activity = "UNKNOWN";
	
	public static String getActivityResult()
	{		
		return activity;
	}
	
	public static void setActivityResult(String activity)
	{		
		Log.i(TAG, "New activity set: "+activity);
		GoogleActivityRecognition.activity = activity;
	}
}

