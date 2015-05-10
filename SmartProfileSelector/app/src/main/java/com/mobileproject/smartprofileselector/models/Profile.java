package com.mobileproject.smartprofileselector.models;

public class Profile
{

	private int id;
	private String name;
	private int ringMode;
	private String ringtone;
	private int ringtoneVolume;
	private int notificationMode;
	private String notificationTone;
	private int notificationToneVolume;
	private int mediaVolume;
	private int activated;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getRingMode() {
		return ringMode;
	}

	public void setRingMode(int ringMode) {
		this.ringMode = ringMode;
	}
	
	public String getRingtone() {
		return ringtone;
	}

	public void setRingtone(String ringtone) {
		this.ringtone = ringtone;
	}
	
	public int getRingtoneVolume() {
		return ringtoneVolume;
	}

	public void setRingtoneVolume(int ringtoneVolume) {
		this.ringtoneVolume = ringtoneVolume;
	}
	
	public int getNotificationMode() {
		return notificationMode;
	}

	public void setNotificationMode(int notificationMode) {
		this.notificationMode = notificationMode;
	}
	
	public String getNotificationTone() {
		return notificationTone;
	}

	public void setNotificationTone(String notificationTone) {
		this.notificationTone = notificationTone;
	}
	
	public int getNotificationToneVolume() {
		return notificationToneVolume;
	}

	public void setNotificationToneVolume(int  notificationToneVolume) {
		this.notificationToneVolume = notificationToneVolume;
	}

	public int getMediaVolume() {
		return mediaVolume;
	}

	public void setMediaVolume(int mediaVolume) {
		this.mediaVolume = mediaVolume;
	}
	
	public int getActivated(){
		return activated;
	}
	
	public void setActivated(int activated){
		this.activated = activated;
	}
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return name;
	}
}
