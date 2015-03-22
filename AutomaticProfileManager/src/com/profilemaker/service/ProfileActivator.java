package com.profilemaker.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.profilemaker.dao.ProfileDataSource;
import com.profilemaker.model.Profile;

public class ProfileActivator {
	
	static boolean vibrate;
	private AudioManager mobilemode;
	private ProfileDataSource profileDataSource;
	
	public void activateProfile(Context context, Profile profile){
		int profileId = profile.getId();
		int ringMode = profile.getRingMode();
		String ringtone = profile.getRingtone();
		int ringVolume = profile.getRingtoneVolume();
		//int notiMode = profile.getNotificationMode();
		String notiTone = profile.getNotificationTone();
		int notiVolume = profile.getNotificationToneVolume();
		int mediaVolume = profile.getMediaVolume();
		
		mobilemode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		activateRingMode(context, ringMode);
		
		if(ringtone!=null){
			activateRingTone(context, ringtone);
		}
		setRingVolume(ringVolume, context);
		
		if(notiTone!=null){
			activateNotiTone(notiTone,context);
		}
		setNotiVolume(notiVolume, context);
		
		setMediaVolume(mediaVolume, context);
		
		//update the column activated in profile table....
		profileDataSource = new ProfileDataSource(context);
		profileDataSource.open();
		profileDataSource.updateActivated(profileId);
		profileDataSource.close();
	}
	
	//This is the default normal profile to activate normal mode for undefined places...
	public void activateDefaultNormalProfile(Context context){
		
		mobilemode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		activateRingMode(context, 2);
		setRingVolume(5, context);
		setNotiVolume(5, context);		
		setMediaVolume(5, context);
	}
	
	//This is the default silent profile to activate silent mode for undefined places... 
	public void activateDefaultSilentProfile(Context context){
		mobilemode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		activateRingMode(context, 0);
		
	}
	
	//This is the default blocked profile to activate blocked mode for undefined places......
	public void activateDefaultBlockedProfile(Context context){
		mobilemode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		activateRingMode(context, 4);
	}
	
	//Activate the ring mode.....
	public void activateRingMode(Context context, int ringMode){
		vibrate = false;
		
		switch (ringMode) {
		
		case 0: 
			if(Settings.System.getInt(context.getContentResolver(),
	                Settings.System.AIRPLANE_MODE_ON, 0)==1){
				
				deactivateFlightMode(context);
			}
			if(!(mobilemode.getRingerMode()==0))
				mobilemode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			
			break;
		
		case 1:
			if(Settings.System.getInt(context.getContentResolver(),
	                Settings.System.AIRPLANE_MODE_ON, 0)==1){
				deactivateFlightMode(context);
			}
			if(!(mobilemode.getRingerMode()==1))
				mobilemode.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
					
			break;
		
		case 2:
			if(Settings.System.getInt(context.getContentResolver(),
	                Settings.System.AIRPLANE_MODE_ON, 0)==1){
				
				deactivateFlightMode(context);
			}
			if(!(mobilemode.getRingerMode()==2))
				mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					
			break;
		case 3:
			
			mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			vibrate = true;
			
			break;
		
		case 4:
			if((mobilemode.getRingerMode()==AudioManager.RINGER_MODE_SILENT)||
					(mobilemode.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE)){
				
				mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			}
				activateFlightMode(context);
			
			break;
		}
	}
	
	//Activate the flight mode.....
	private void activateFlightMode(Context context){
		
		Settings.System.putInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON,1);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", 1);
        context.sendBroadcast(intent);
	}
	
	private void deactivateFlightMode(Context context){
		
		Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON,0);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", 0);
        context.sendBroadcast(intent);
	}
	
	private void activateRingTone(Context context, String ringtone){
		
		Log.i("uri:",""+Uri.parse(ringtone));
		
		RingtoneManager.setActualDefaultRingtoneUri(context,
		RingtoneManager.TYPE_RINGTONE,Uri.parse(ringtone));
		
	}
	
	private void setRingVolume(int ringVolume, Context context){	
		mobilemode.setStreamVolume(AudioManager.STREAM_RING, ringVolume, AudioManager.FLAG_SHOW_UI);
	}
	
	private void activateNotiTone(String notiTone, Context context){
		RingtoneManager.setActualDefaultRingtoneUri(context,
				RingtoneManager.TYPE_NOTIFICATION,Uri.parse(notiTone));
	}
	
	private void setNotiVolume(int notiVolume, Context context){	
		mobilemode.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notiVolume, AudioManager.FLAG_SHOW_UI);
	}
	
	private void setMediaVolume(int mediaVolume, Context context){		
		mobilemode.setStreamVolume(AudioManager.STREAM_MUSIC, mediaVolume, AudioManager.FLAG_SHOW_UI);
	}
	
	
}
