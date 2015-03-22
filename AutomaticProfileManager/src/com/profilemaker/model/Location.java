package com.profilemaker.model;

public class Location {

	private int locationId;
	private int profileId;
	private double latitude;
	private double longitude;

	public Location(int profileId, double latitude, double longitude){
		this.profileId = profileId;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public int getLocationId() {
		return locationId;
	}
	
	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}
	public int getProfileId() {
		return profileId;
	}
		
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLatitude() {
		return latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLongitude() {
		return longitude;
	}
}
