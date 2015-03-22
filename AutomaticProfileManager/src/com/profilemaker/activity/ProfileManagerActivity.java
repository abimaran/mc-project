package com.profilemaker.activity;

import com.profilemaker.activity.R;
import com.profilemaker.dao.LocationDataSource;
import com.profilemaker.dao.ProfileDataSource;
import com.profilemaker.dao.TimePeriodDataSource;
import com.profilemaker.model.Profile;
import com.profilemaker.preference.SettingsActivity;
import com.profilemaker.service.AcceptService;
import com.profilemaker.service.BluetoothPattern;
import com.profilemaker.service.LocationTracker;
import com.profilemaker.service.ProfileActivator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ProfileManagerActivity extends Activity implements LocationListener {
    /** Called when the activity is first created. */
	private static final int DIALOG_ALERT = 10;
	
	private Context context;
	private Button addProfile;
	private Button delProfile;
	private ListView list;
	ArrayAdapter<String> profileList;
	private String profileName;
	private SparseBooleanArray checked;
	
	private boolean isPatternRecgActivated;
	private boolean isPlaceRecgActivated;
	private boolean isBlutthRecgActivated;
	private boolean isOnDBActivated;
	private String profActivationModePatt;
	private String profActivationModeBlu;
	private boolean isPlaceRecgRun = false;
	private boolean isPatternRecgRun = false;
	private boolean isOnDBRun = false;
	private LocationManager locationManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = getApplication();
        
        addProfile = (Button)findViewById(R.id.btnAddProfile);
        delProfile = (Button)findViewById(R.id.btnDeleteProfile);
        list = (ListView)findViewById(R.id.profileList);
     
        //LocationTracker locTracker = new LocationTracker();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        
        //Sets the shared preferences into default values..................
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        
        //call loadprofilelist method to load profile list from database....
        loadProfileList();
       
        //Check bluetooth availability.......
        
        
        addProfile.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent profileIntent = new Intent(ProfileManagerActivity.this,ProfileMakerBasicActivity.class);
				startActivity(profileIntent);				
			}
		});
        
        delProfile.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//get all selected profile positions...........
				checked = list.getCheckedItemPositions();
				if(checked.size()>0){
					showDialog(10);
				}				
			}
		});
        
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> aV, View v, int index, long id) {
				ProfileDataSource profileDatasource = new ProfileDataSource(context);
				profileDatasource.openReadable();
				Profile profile = profileDatasource.getProfile(aV.getItemAtPosition(index).toString());
				profileDatasource.close();
				
				//Call to activate new Profile.................
				ProfileActivator profileActivator = new ProfileActivator();
				profileActivator.activateProfile(context, profile);
				
				return false;
			}
        });       
    }
    
    public void onClick(View view){
        showDialog(DIALOG_ALERT);
    }

    //Create dialog method.............................
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ALERT:
          //Create out AlertDialog
          Builder builder = new AlertDialog.Builder(this);
          builder.setMessage("Are you sure you want to delete Profiles");
          builder.setCancelable(true);
          builder.setPositiveButton("Yes", new OkOnClickListener());
          builder.setNegativeButton("Cancel", new CancelOnClickListener());
          AlertDialog dialog = builder.create();
          dialog.show();
        }
        return super.onCreateDialog(id);
      }

      private final class CancelOnClickListener implements
          DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
          //Toast.makeText(getApplicationContext(), "Activity will continue",
          //    Toast.LENGTH_LONG).show();
        }
      }

      private final class OkOnClickListener implements
          DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
        	
        	for(int i = 0; i<list.getCount(); i++){
				Log.i("List View", ""+checked.get(i));
				if(checked.get(i)){
					//get the selected profile name each by each......
					profileName = list.getItemAtPosition(i).toString();
					//show confirmation dialog to be confirmed deletion..
					//Delete the profile details from time details table....................
					TimePeriodDataSource timePeriodDataSource = new TimePeriodDataSource(context);
					timePeriodDataSource.open();
					timePeriodDataSource.deleteTimePeriod(profileName);
					timePeriodDataSource.close();
					
					//Delete the profile details from location table.........................
					LocationDataSource locationDataSource = new LocationDataSource(context);
					locationDataSource.open();
					locationDataSource.deleteLocation(profileName);
					locationDataSource.close();
					
					//Delete the profile details from profile table.........................
					ProfileDataSource profileDataSource = new ProfileDataSource(context);
					profileDataSource.open();
					profileDataSource.deleteProfile(profileName);
					profileDataSource.close();							
				}
			}
			loadProfileList();				
        }
      }
      
    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.preference:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void loadProfileList(){
    	ProfileDataSource profileDatasource = new ProfileDataSource(context);
    	
    	profileDatasource.open();
    	String[] names = profileDatasource.getAllProfileNames();
    	profileDatasource.close();
   
    	//add items into Profile listview...................
    	profileList = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice,
    			android.R.id.text1, names) ;
    	list.setAdapter(profileList);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            
    }

	@Override
	public void onLocationChanged(Location location) {
		Log.i("GPS", "receiver working!!");
		
		//make the object from location manager class to access methods of location tracker...
		LocationTracker locTracker = new LocationTracker();
		
		//get the shared preference object and initialize all the variables from it........
		SharedPreferences shrdPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		isPatternRecgActivated = shrdPrefs.getBoolean("pref_key_activate_pattern_recognition", false);
		isPlaceRecgActivated = shrdPrefs.getBoolean("pref_key_activate_place_recognition", false);
		isBlutthRecgActivated = shrdPrefs.getBoolean("pref_key_activate_bluetooth_recognition", false);
		isOnDBActivated = shrdPrefs.getBoolean("pref_key_activate_on_db", false);
		profActivationModePatt = shrdPrefs.getString("pref_key_profile_change_mode_pattern", "Alert User");
		profActivationModeBlu = shrdPrefs.getString("pref_key_bluetooth_recognition_mode", "Alert User");
		
		//select what is the profile changing mode.................
		//GpsStatus gpsStates = locationManager.getGpsStatus(null);
		//if(gpsStates.getMaxSatellites()>6){
		//if((2.7<=location.getAccuracy())&&(location.getAccuracy()<=20)){
			Log.i("Location Acuracy", ""+location.getAccuracy());
			if(isPlaceRecgActivated){
				if(locTracker.changeProfileOnLocation(location, context))
					isPlaceRecgRun = true;
			}
			if(isOnDBActivated&&!isPlaceRecgRun){
				Log.i("On DB", "run location DB..");
				if(locTracker.changeProfileOnDB(location, context))
					isOnDBRun = true;
			}
			if(isPatternRecgActivated&&!isPlaceRecgRun&&!isOnDBRun){		
				if(locTracker.changeModeOnPattern(location, context))
					isPatternRecgRun = true;
			}
		// }else{
		//	 if(!isPlaceRecgRun&&!isOnDBRun&&!isPatternRecgRun&&isBlutthRecgActivated)
		//		 locTracker.changeProfileOnBluetooth(context);
		// }
	}

	@Override
	public void onProviderDisabled(String provider) {
	
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	
	}
	
}