package com.profilemaker.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.media.AudioManager;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.profilemaker.dao.LocationDataSource;
import com.profilemaker.dao.PatternDataSource;
import com.profilemaker.dao.ProfileDataSource;
import com.profilemaker.dao.TimePeriodDataSource;
import com.profilemaker.model.Location;
import com.profilemaker.model.Pattern;
import com.profilemaker.model.TimePeriod;

public class LocationTracker{
	private BluetoothPattern bluetoothPattern;
	
	public boolean changeProfileOnDB(android.location.Location location, Context context){
		
		LocationDataSource locationDataSource = new LocationDataSource(context);
		locationDataSource.open();
		List<Location> locations = locationDataSource.getAllLocations();
		locationDataSource.close();
		
		Iterator<Location> itrLocations = locations.iterator();
		android.location.Location savedLocation = new android.location.Location(location);
		
		while(itrLocations.hasNext()){
			Location tempLocation = itrLocations.next();
			savedLocation.setLatitude(tempLocation.getLatitude());
			savedLocation.setLongitude(tempLocation.getLongitude());
			int distance = (int)location.distanceTo(savedLocation);
			
			Log.i("GPS", "distance is:"+distance);
			if(distance < 10){
				Log.i("GPS", "came into the first if...");
				
				int profileId = tempLocation.getProfileId();
				TimePeriodDataSource timePeriodDataSource = new TimePeriodDataSource(context);
				timePeriodDataSource.open();
				List<TimePeriod> timePeriods = timePeriodDataSource.getTimePeriods(profileId);
				timePeriodDataSource.close();
				
				Iterator<TimePeriod> itrTimePeriods = timePeriods.iterator();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				
				Time today = new Time(Time.getCurrentTimezone());
				today.setToNow();
											
				while(itrTimePeriods.hasNext()){
					TimePeriod tempTimePeriod = itrTimePeriods.next();
					Log.i("Time", "came into the second while...: "+getIntDay(tempTimePeriod.getDay()));
					try {
						Date startTime = sdf.parse(tempTimePeriod.getStartHour()+":"+tempTimePeriod.getStartMinute());
						Date currentTime = sdf.parse(today.hour+":"+today.minute);
						
						if((Math.abs(startTime.getTime()-currentTime.getTime())<600000)&&(getIntDay(tempTimePeriod.getDay())==today.weekDay)){
							Log.i("Time", "Came into the seconde if...");
							ProfileDataSource profileDataSource  = new ProfileDataSource(context);
							ProfileActivator profileActivator = new ProfileActivator();
							profileDataSource.openReadable();		
							profileActivator.activateProfile(context, profileDataSource.getProfile(profileId));
							profileDataSource.close();
							return true;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}

				}	
			}else if((distance > 60)&&(distance < 500)){
				Log.i("GPS", "came into the first else if...");
				
				int profileId = tempLocation.getProfileId();
				TimePeriodDataSource timePeriodDataSource = new TimePeriodDataSource(context);
				timePeriodDataSource.open();
				List<TimePeriod> timePeriods = timePeriodDataSource.getTimePeriods(profileId);
				timePeriodDataSource.close();
				
				Iterator<TimePeriod> itrTimePeriods = timePeriods.iterator();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				
				Time today = new Time(Time.getCurrentTimezone());
				today.setToNow();
				
				while(itrTimePeriods.hasNext()){
					TimePeriod tempTimePeriod = itrTimePeriods.next();
					Log.i("Time", "came into the second else if while...: "+getIntDay(tempTimePeriod.getDay()));
					
					try {
						Date endTime = sdf.parse(tempTimePeriod.getEndHour()+":"+tempTimePeriod.getEndMinute());
						Date currentTime = sdf.parse(today.hour+":"+today.minute);
						
						if((Math.abs(endTime.getTime()-currentTime.getTime())<600000)&&(getIntDay(tempTimePeriod.getDay())==today.weekDay)){
							Log.i("Time", "Came into the seconde else if if...");
							ProfileDataSource profileDataSource  = new ProfileDataSource(context);
							ProfileActivator profileActivator = new ProfileActivator();
							profileDataSource.openReadable();		
							profileActivator.activateDefaultNormalProfile(context);
							profileDataSource.close();
							
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				
				}
			}
		}
		return false;
			
	}
	
	//Using pattern, change the mode.......................................................
	public boolean changeModeOnPattern(android.location.Location location, Context context) {
		
		PatternDataSource patternDataSource = new PatternDataSource(context);
		patternDataSource.open();
		List<Pattern> patterns = patternDataSource.getAllPatterns();
		patternDataSource.close();
		Iterator<Pattern> itrPatterns = patterns.iterator();
		android.location.Location savedLocation = new android.location.Location(location);
		
		Time today = new Time(Time.getCurrentTimezone());
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		today.setToNow();
		
		while(itrPatterns.hasNext()){
			Pattern tempPattern = itrPatterns.next();
			savedLocation.setLatitude(tempPattern.getLatitude());
			savedLocation.setLongitude(tempPattern.getLongitude());
			
			try {
				Date startTime = sdf.parse(tempPattern.getStartHour()+":"+tempPattern.getStartMinute());
				Date currentTime = sdf.parse(today.hour+":"+today.minute);
				
				if((today.weekDay==tempPattern.getDay())&&
						(Math.abs(startTime.getTime()-currentTime.getTime())<600000)&&
						(savedLocation.distanceTo(location)<10)){
					
					int ringMode = determineMode(tempPattern);
					ProfileActivator  profileActivator = new ProfileActivator();
					profileActivator.activateRingMode(context, ringMode);
					Log.i("On Pattern", "mode changed..!!");
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		
		}
		return false;
	}
	
	//Change the location on place type from Google maps API...................................
	public boolean changeProfileOnLocation(android.location.Location location, Context context) {
		
		HttpResponse response = null;
		String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location.getLatitude()+","+location.getLongitude()+"&radius=50&sensor=true&key=AIzaSyCvQsX8OvQdcXUPqo9Qvz1FFUHbvCqVFjk";
		try {        
		        HttpClient client = new DefaultHttpClient();
		        HttpPost request = new HttpPost();
		        request.setURI(new URI(url));
		        response = client.execute(request);
		        Log.i("Repond", "response came!!"+response.getStatusLine().getStatusCode());
		        
		        String responseString = convertStreamToString(response.getEntity().getContent());
		        Log.i("Repond", responseString);
		        JSONObject json = new JSONObject(responseString);
		        JSONArray results = json.getJSONArray("results");	
		        
		        //loop all the json array objects..............
		        for(int j = 0; j<results.length(); j++){
			        JSONObject result = results.getJSONObject(j);
			        JSONArray types = result.getJSONArray("types");
			        
			        Log.i("Repond", ""+types.length());
			        
			        //Check types each by each to get libraries or gas stations...
			        for(int i = 0; i < types.length(); i++){
			        	String type = types.getString(i).toString();
			        	Log.i("Repond", ""+type);
			        	if(type.equals("courthouse")||type.equals("library")){
			        		ProfileActivator profileActivator = new ProfileActivator();
			        		profileActivator.activateDefaultSilentProfile(context);
			        		return true;
			        		
			        	}else if(type.equals("gas_station")){
			        		ProfileActivator profileActivator = new ProfileActivator();
			        		profileActivator.activateDefaultBlockedProfile(context);
			        		return true;
			        	}		        		
			        }
		        } 		        
			} catch (URISyntaxException e) {
		        e.printStackTrace();
		    } catch (ClientProtocolException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    } catch (JSONException e) {
				e.printStackTrace();
			}   
		return false;
	}
	
	//change profile on bluetooth generated text file details..........
	public void changeProfileOnBluetooth(Context context){
		Log.i("Bluetooth", "Loc tracker");
		bluetoothPattern = new BluetoothPattern(context);
		bluetoothPattern.sendRequest();
		
	}
		
	//return the int Day using String day.......
	public int getIntDay(String day){	
	  if(day.equalsIgnoreCase("Monday"))  
	   return 1;  
	  if(day.equalsIgnoreCase("Tuesday"))  
	   return 2;  
	  if(day.equalsIgnoreCase("Wednesday"))  
	   return 3;  
	  if(day.equalsIgnoreCase("Thrusday"))  
	   return 4;  
	  if(day.equalsIgnoreCase("Friday"))  
	   return 5;  
	  if(day.equalsIgnoreCase("Saturday"))  
	   return 6;  
	  if(day.equalsIgnoreCase("Sunday"))  
	   return 0;  
	return -1;
	}  

	//Convert the stream into string...................................................
	public String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),1024);
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                inputStream.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

	//Using pattern identify the mode and return it..........
	public int determineMode(Pattern pattern) {
		
		int noOfSilents = pattern.getSilent();
		int noOfVibrations = pattern.getVibration();
		int noOfNormals = pattern.getVibration();
		int noOfFlights = pattern.getVibration();
		
		int total = noOfSilents+noOfVibrations+noOfNormals+noOfFlights;
		float probOfSilents = noOfSilents/total;
		float probOfVibrations = noOfVibrations/total;
		float probOfNormals = noOfNormals/total;
		float probOfFlights = noOfFlights/total;
		
		if((probOfSilents>probOfVibrations)&&(probOfSilents>probOfNormals)&&
				(probOfSilents>probOfFlights))
			return 0;
		else if((probOfVibrations>probOfSilents)&&(probOfVibrations>probOfNormals)&&
				(probOfVibrations>probOfFlights))
			return 1;
		else if((probOfNormals>probOfSilents)&&(probOfNormals>probOfVibrations)&&
				(probOfNormals>probOfFlights))
			return 2;
		else if((probOfFlights>probOfSilents)&&(probOfFlights>probOfVibrations)&&
				(probOfFlights>probOfNormals))
			return 4;
		
		return -1;
	}
}
