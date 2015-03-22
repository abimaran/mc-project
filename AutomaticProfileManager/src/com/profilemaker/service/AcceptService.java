package com.profilemaker.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class AcceptService extends Service{
	LocationManager locationManager;
	private boolean isBlutthRecgActivated;
	LocationTracker locTracker;
	private Context context;
	
	@Override
	public void onCreate(){
		context = getApplicationContext();
		locTracker = new LocationTracker();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flag, int startId) {
		//while(true){
		
		SharedPreferences shrdPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		isBlutthRecgActivated = shrdPrefs.getBoolean("pref_key_activate_bluetooth_recognition", false);
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, );
        GpsStatus gpsStates = locationManager.getGpsStatus(null);
        
        Iterator<GpsSatellite> itr = gpsStates.getSatellites().iterator();
       // Log.i("Location Acuracy", ""+itr.toString());
        int i =0;
        while(itr.hasNext()){
        	i++;
        	itr.next();
        }
        if((i<6)&&isBlutthRecgActivated){
    			locTracker.changeProfileOnBluetooth(context);
    			Log.i("Bluetooth", "acuracy low...");
        }
        Log.i("Location Acuracy", ""+itr.toString());
        return START_STICKY;
	//}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
