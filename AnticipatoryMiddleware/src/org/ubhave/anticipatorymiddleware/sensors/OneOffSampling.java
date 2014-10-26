package org.ubhave.anticipatorymiddleware.sensors;

import org.ubhave.anticipatorymiddleware.time.Time;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.ubhave.dataformatter.json.JSONFormatter;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pullsensor.LocationData;
import com.ubhave.sensormanager.sensors.SensorUtils;

/**
 * OneOffSensing class enables fetching sensor data for the current instance of time.
 * It extends AsyncTask so that the sensing process is done in the background.
 */
public class OneOffSampling extends AsyncTask<Void, Void, SensorData>
{

	private static final String TAG = "AnticipatoryManager";

	private final ESSensorManager sensorManager;

	private int sensor;

	private SensorData sensorData;

	JSONFormatter formatter;

	private LocationManager locationManager;
	private long kMinimumTime = 40*1000L;
	private int kMinimumDistance = 200;
	boolean isGPSEnabled, isNetworkEnabled;
	private final Context app_context;

	public OneOffSampling(Context app_context, int sensor) throws ESException
	{
		Log.d(TAG, "One-off sensing constructor, Registered sensor: " + sensor);
		this.app_context = app_context;
		this.sensor = sensor;
		sensorManager = ESSensorManager.getSensorManager(app_context);
		Log.d(TAG, "ESSensorManager instantiated ");
//		formatter = DataFormatter.getJSONFormatter(app_context, sensor);
	}

	@Override
	protected SensorData doInBackground(Void... params)
	{
		Log.d(TAG, "One-off sensing doInBackground, Registered sensor: " + sensor);
		if(sensor == Constants.SENSOR_TYPE_LOCATION)
		{
			Log.d(TAG, "Sampling data for "+sensor+" with LocationTracker");				
			Location location = LocationTracker.getInstance(app_context).getLatestLocation();
			LocationData location_data = new LocationData(Time.getCurrentTimeInMilliseconds(), SensorUtils.getDefaultSensorConfig(sensor));
			location_data.setLocation(location);
			sensorData =  location_data;
			Log.i(TAG, "New location by LocationHelper: "+location.getLatitude()+","+location.getLongitude());
		

		}
		else if(sensor == Constants.SENSOR_TYPE_ACCELEROMETER)
		{
			ActivityTracker activity_tracker = ActivityTracker.getInstance(app_context); // start sampling
			sensorData = activity_tracker.getSensorData();
		}
		else if(sensor ==  Constants.SENSOR_TYPE_CALL_LOGS)
		{
			sensorData =  new CallLogsReader().getCallData(app_context);
		}
		else if(sensor ==  Constants.SENSOR_TYPE_SMS_LOGS)
		{
			sensorData =  new SMSLogsReader().getSMSData(app_context);
		}
		else
		{
			try
			{	
				sensorData = sensorManager.getDataFromSensor(sensor);
				Log.d(TAG, "Data sampled for "+sensor+" with ESSensorManager");			
			}
			catch (ESException e)
			{
				Log.e(TAG, "Error inside One-off sensing doInBackground!! " + e.toString());
				return null;
			} 	
		}	
		return(sensorData);
	}

	//Location code.
	@SuppressWarnings("unused")
	private Location locationCode(){
		locationManager = (LocationManager)app_context.getSystemService(Context.LOCATION_SERVICE);

		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if(isGPSEnabled || isNetworkEnabled)
		{
			Location newLocation = getLatestLocation(app_context);
			return newLocation;


		}else{
			Log.e(TAG, "Failed to access location service: No permission.");
			return null;
		}
	}

	////Extras

	private Location getLatestLocation(Context context) {

		LocationManager manager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = manager.getBestProvider(criteria, true);
		Location bestLocation;
		if (provider != null)
			bestLocation = manager.getLastKnownLocation(provider);
		else
			bestLocation = null;
		Location latestLocation = getLatest(bestLocation,
				manager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
		latestLocation = getLatest(latestLocation,
				manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		latestLocation = getLatest(latestLocation,
				manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
		return latestLocation;
	}

	private Location getLatest(final Location location1,
			final Location location2) {
		if (location1 == null)
			return location2;

		if (location2 == null)
			return location1;

		if (location2.getTime() > location1.getTime())
			return location2;
		else
			return location1;
	}



	//Google function for better location verification.
	public boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > kMinimumTime;
		boolean isSignificantlyOlder = timeDelta < -kMinimumTime;
		;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > kMinimumDistance;
		boolean isMoreAccurate = accuracyDelta < kMinimumDistance;
		boolean isSignificantlyLessAccurate = accuracyDelta > 400;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	public static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}


}