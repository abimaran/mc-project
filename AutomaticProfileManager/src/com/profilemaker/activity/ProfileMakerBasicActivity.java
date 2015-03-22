package com.profilemaker.activity;

import com.profilemaker.activity.R;
import com.profilemaker.dao.ProfileDataSource;
import com.profilemaker.model.Profile;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

public class ProfileMakerBasicActivity extends Activity {

	private Context context;
	private EditText setProfileName;
	private Spinner selectRingMode;
	private Spinner selectNotiMode;
	private Button selectRingTone;
	private Button selectNotiTone;
	private SeekBar setRingVolume;
	private SeekBar setNotiVolume;
	private SeekBar setMediaVolume;
	private Button saveProfile;
	private Button cancelProfile;
	
	private String ringtone;
	private String notificationTone;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_maker_basic);
        context = getApplicationContext();
        
        setProfileName = (EditText)findViewById(R.id.txtProfName);
        selectRingMode = (Spinner)findViewById(R.id.spinRingMode);
       // selectNotiMode = (Spinner)findViewById(R.id.spinSelectNotiMode);
        selectRingTone = (Button)findViewById(R.id.btnSelectRingTone);      
        selectNotiTone = (Button)findViewById(R.id.btnSelectNotiTone);
        setRingVolume = (SeekBar)findViewById(R.id.seekBarRingtone);
        setRingVolume.setMax(10);
        setNotiVolume = (SeekBar)findViewById(R.id.seekBarNotification);
        setNotiVolume.setMax(10);
        setMediaVolume = (SeekBar)findViewById(R.id.seekBarMedia);
        setMediaVolume.setMax(10);
        saveProfile = (Button)findViewById(R.id.btnSaveProfile);
        cancelProfile = (Button)findViewById(R.id.btnCancelProfile);
        
	//Get Ring tone list
	selectRingTone.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {
               
           	Intent intent = new Intent( RingtoneManager.ACTION_RINGTONE_PICKER);
           	intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TYPE,
           	RingtoneManager.TYPE_RINGTONE);
           	intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Ringtone");
           	intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
           	intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
           	Uri.parse( "/sdcard/media/audio/ringtone/"));
                       	
           	startActivityForResult(intent, 0);            	
       }
                    
	});    
	
	//Get Notification tone list
	selectNotiTone.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {
               
        	Intent intent = new Intent( RingtoneManager.ACTION_RINGTONE_PICKER);
        	intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TYPE,
        	RingtoneManager.TYPE_NOTIFICATION);
        	intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Tone");
        	intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        	intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
        	Uri.parse( "/sdcard/media/audio/ringtone/"));
                   	
        	startActivityForResult(intent, 1);            	
        }
                
	});   
	
	saveProfile.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			String profileName = setProfileName.getText().toString();
			int ringMode = selectRingMode.getSelectedItemPosition();
			int ringtoneVolume = setRingVolume.getProgress();
			int notificationMode = 0;//selectNotiMode.getSelectedItemPosition();
			int notificationToneVolume = setNotiVolume.getProgress();
			int mediaVolume = setMediaVolume.getProgress();
			int activated = 0;
			
			ProfileDataSource profileDataSourse = new ProfileDataSource(getApplicationContext());
			profileDataSourse.open();
			Profile tempProfile = profileDataSourse.createProfile(profileName, ringMode, ringtone, 
					ringtoneVolume, notificationMode, notificationTone, notificationToneVolume, mediaVolume, activated);
			profileDataSourse.close();
			
			Toast toast = Toast.makeText(context, "Profile is saved successfully!", Toast.LENGTH_SHORT);
			toast.show();
			Log.i("Profile id",""+tempProfile.getId());;
			
			Intent timeIntent = new Intent(ProfileMakerBasicActivity.this, ProfileMakerTimeActivity.class);
			timeIntent.putExtra("profileId", tempProfile.getId());
			ProfileMakerBasicActivity.this.startActivity(timeIntent);
		}
	});
	
	cancelProfile.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			finish();			
		}
	});
	
	}         
     
	//Save the Ring tone and notification tone to the variables to send the database.......
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
      	switch (resultCode) {
      	/*
      	*
      	*/
      	case RESULT_OK:
      	//Setting ringtone to the variable.................................................
      	if(requestCode==0){
      	
	      	//sents the ringtone that is picked in the Ringtone Picker Dialog
	      	Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	
	      	//send the output of the selected to a string
	      	ringtone = uri.toString();
	      	//this should be send to the database.......................................
	      	//the program creates a "line break" when using the "\n" inside a string value
	      	
	      	//prints out the result in the console window
	      	Log.i("Ring tone", "uri " + uri);
	      	
      	}else{
      		//Setting the notification tone the variables.................................
      		//sents the ringtone that is picked in the Ringtone Picker Dialog
          	Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

          	//send the output of the selected to a string
          	notificationTone = uri.toString();
          	//this should be send to the database.........................................
          	
          	//prints out the result in the console window
          	Log.i("Notification tone", "uri " + uri);
      	}
      	break;

      }

  }
	
	//get ringtone volume........
	protected int getRingVolume() {
		return setRingVolume.getProgress();	
	}
	
	//get notification tone volume.....
	protected int getNotiVolume() {
		return setNotiVolume.getProgress();	
	}
	
	//get media volume..........
	protected int getMediaVolume() {
		return setMediaVolume.getProgress();	
	}
	
	
}
	

