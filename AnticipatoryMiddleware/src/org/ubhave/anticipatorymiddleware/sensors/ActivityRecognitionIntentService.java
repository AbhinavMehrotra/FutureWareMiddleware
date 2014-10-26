package org.ubhave.anticipatorymiddleware.sensors;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;


/**
 * Service that receives ActivityRecognition updates. It receives updates
 * in the background, even if the main Activity is not visible.
 */
public class ActivityRecognitionIntentService extends IntentService {
	
	private static final String TAG = "AnticipatoryManager";

    public ActivityRecognitionIntentService() {
        super("ActivityRecognitionIntentService");
    	Log.i(TAG, "ActivityRecognitionIntentService started");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	Log.e(TAG, "Intent received");
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();
            int confidence = mostProbableActivity.getConfidence();
            int activityType = mostProbableActivity.getType();
            String activityName = getNameFromType(activityType);
            Log.e(TAG, activityName +","+ confidence);
            //TODO: Use these-  activityName and confidence   
            GoogleActivityRecognition.setActivityResult(activityName);

        } else {
            // Toast.makeText(getApplicationContext(), "Error in handle client", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error in handle client");
        }
    }

    private String getNameFromType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }
    
   
 
}