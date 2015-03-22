package com.profilemaker.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import com.profilemaker.dao.PatternDataSource;
import com.profilemaker.db.DataBaseHelper;
import com.profilemaker.model.Pattern;

public class ModeTracker {
	
	private AudioManager audioManager;
	public void insertPattern(Context context){
		Time today = new Time(Time.getCurrentTimezone());
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		today.setToNow();
		
		int day = today.weekDay;
		int startHour = today.hour;
		int startMinute = today.minute;
		
		//get the instant access to the gps receiver and get current location details 
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		double latitude = 0.0;
		double longitude = 0.0;
			
		//Access the saved pattern table through DAO object.........
		PatternDataSource patternDataSource = new PatternDataSource(context);
		patternDataSource.open();
		List<Pattern> patterns = patternDataSource.getAllPatterns();
		audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		boolean isFound = false;
		
		Iterator<Pattern> itrPaterns = patterns.iterator();
		
		if(location!=null){
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		
			Log.i("Got Location...", ""+latitude +" "+longitude);
			Location savedLocation = new Location(location);
		
		
			while(itrPaterns.hasNext()){
				Pattern tempPattern = itrPaterns.next();
				savedLocation.setLatitude(tempPattern.getLatitude());
				savedLocation.setLongitude(tempPattern.getLongitude());
				
				try {
					Date startTime = sdf.parse(tempPattern.getStartHour()+":"+tempPattern.getStartMinute());
					Date currentTime = sdf.parse(today.hour+":"+today.minute);
					
					//check whether there is matching pattern in the table and if so add new update to it......
					if((tempPattern.getDay()==day)&&(Math.abs(startTime.getTime()-currentTime.getTime())<600000)&&
							(savedLocation.distanceTo(location)<10)){
						isFound = true;
						switch(audioManager.getRingerMode()){
						case AudioManager.RINGER_MODE_SILENT:
							patternDataSource.updatePattern(tempPattern.getPatternId(), 
									DataBaseHelper.COLUMN_SILENT, tempPattern.getSilent()+1);
							Log.i("Pattern Results", "badu awa.....");
							break;						
						case AudioManager.RINGER_MODE_VIBRATE:
							patternDataSource.updatePattern(tempPattern.getPatternId(), 
									DataBaseHelper.COLUMN_VIBRATION, tempPattern.getVibration()+1);
							Log.i("Pattern Results", "badu awa.....");
							break;
						case AudioManager.RINGER_MODE_NORMAL:
							patternDataSource.updatePattern(tempPattern.getPatternId(), 
									DataBaseHelper.COLUMN_NORMAL, tempPattern.getNormal()+1);
							Log.i("Pattern Results", "badu awa.....");
							break;
						}				
					}
					
				} catch (ParseException e) {
					e.printStackTrace();
				}						
			}
		
			// if no add new record to the table with current details.................
			if(!isFound){
				Pattern pattern;
				switch(audioManager.getRingerMode()){
				case AudioManager.RINGER_MODE_SILENT:
					pattern = patternDataSource.createPattern(day, startHour, startMinute, latitude, 
							longitude, 1, 0, 0, 0);
					Log.i("Pattern Results", ""+pattern.getSilent());
					break;						
				case AudioManager.RINGER_MODE_VIBRATE:
					pattern = patternDataSource.createPattern(day, startHour, startMinute, latitude, 
							longitude, 0, 1, 0, 0);
					Log.i("Pattern Results", ""+pattern.getVibration());
					break;
				case AudioManager.RINGER_MODE_NORMAL:
					pattern = patternDataSource.createPattern(day, startHour, startMinute, latitude, 
							longitude, 0, 0, 1, 0);
					Log.i("Pattern Results", ""+pattern.getNormal());
					break;
				}
			}
		}
		patternDataSource.close();
	}
	
	public void writeMode(Context context){
		
		try{
			File phoneMode = new File(Environment.getExternalStorageDirectory(),"Mode"); 
			if(!phoneMode.exists()){
				phoneMode.mkdir();
			}
			File myMode = new File(phoneMode,"myMode.txt");
			FileWriter writer = new FileWriter(myMode);
			writer.write(""+audioManager.getRingerMode());
			writer.flush();
			writer.close();
			
		}catch(IOException ie){
			ie.printStackTrace();
	         
		}
		
	}
}
