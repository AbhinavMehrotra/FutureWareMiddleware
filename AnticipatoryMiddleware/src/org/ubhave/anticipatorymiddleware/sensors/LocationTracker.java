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
package org.ubhave.anticipatorymiddleware.sensors;


import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

public class LocationTracker implements LocationListener{
	private final String Location_Tracker_Latitude = "Location_Tracker_Latitude";

	private final String Location_Tracker_Longitude = "Location_Tracker_Longitude";

	private final String Location_Tracker_Time = "Location_Tracker_Time";
	
	private LocationManager location_manager;
	
	private final SharedPreferences sp;

	private final int sampling_rate;
	
	private static LocationTracker instance;
	
	public static LocationTracker getInstance(Context context)
	{
		if(instance == null)
		{
			return new LocationTracker(context);
		}
		else
		{
			return instance;
		}
	}
	
	private LocationTracker(Context app_context) {
		instance = this;
		location_manager = (LocationManager) app_context.getSystemService(Context.LOCATION_SERVICE);
		requestLocationUpdates();
		sp = new SharedPreferences(app_context);
		SensorManager sm = new SensorManager(app_context);
		sampling_rate = sm.getContextSamplingRate();
	}
	
	public Location getLatestLocation(){
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
		Location last_known_location = location_manager.getLastKnownLocation(location_manager.getBestProvider(criteria, true));
		if(last_known_location == null){
			Log.d(getClass().getName(), "Last known location is null");
			Location location = new Location(LocationManager.GPS_PROVIDER);
			location.setLatitude(0);
			location.setLongitude(0);
			return location;
		}
		if(sp.getString(this.Location_Tracker_Time).equals("") || last_known_location.getTime() > Long.parseLong(sp.getString(this.Location_Tracker_Time))){
			Log.d(getClass().getName(), "Last known location (recent): "+last_known_location.getLatitude()+","+last_known_location.getLongitude());
			double latitude = last_known_location.getLatitude();
			double longitude = last_known_location.getLongitude();
			long time = last_known_location.getTime();
			sp.add(this.Location_Tracker_Latitude, String.valueOf(latitude));
			sp.add(this.Location_Tracker_Longitude, String.valueOf(longitude));
			sp.add(this.Location_Tracker_Time, String.valueOf(time));
			return last_known_location;
		}
		else{
			last_known_location.setLatitude(Double.parseDouble(sp.getString(this.Location_Tracker_Latitude)));
			last_known_location.setLongitude(Double.parseDouble(sp.getString(this.Location_Tracker_Longitude)));
			Log.d(getClass().getName(), "Last known location (old): "+last_known_location.getLatitude()+","+last_known_location.getLongitude());
			return last_known_location;
		}
	}
	
	private void requestLocationUpdates(){
		location_manager.removeUpdates(this);
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = location_manager.getBestProvider(criteria, true);
		location_manager.requestLocationUpdates(provider, sampling_rate, 0, this, Looper.getMainLooper());
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(getClass().getName(), "New location: "+location.getLatitude()+","+location.getLongitude());
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		long time = location.getTime();
		sp.add(this.Location_Tracker_Latitude, String.valueOf(latitude));
		sp.add(this.Location_Tracker_Longitude, String.valueOf(longitude));
		sp.add(this.Location_Tracker_Time, String.valueOf(time));
	}

	@Override
	public void onProviderDisabled(String provider) {
		requestLocationUpdates();		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
