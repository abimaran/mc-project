package com.profilemaker.model;

public class Pattern {
	
	private int patternId;
	private int day;
	private int startHour;
	private int startMinute;
	private double latitude;
	private double longitude;
	private int silent;
	private int vibration;
	private int normal;
	private int flight;
	
	public void setPatternId(int patternId){
		this.patternId = patternId;
	}
	public int getPatternId() {
		return patternId;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	public int getDay() {
		return day;
	}
	
	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}
	public int getStartHour() {
		return startHour;
	}
	
	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}
	public int getStartMinute() {
		return startMinute;
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
	
	public void setSilent(int silent) {
		this.silent = silent;
	}
	public int getSilent() {
		return silent;
	}
	
	public void setVibration(int vibration) {
		this.vibration = vibration;
	}
	public int getVibration() {
		return vibration;
	}
	
	public void setNormal(int normal) {
		this.normal = normal;
	}
	public int getNormal() {
		return normal;
	}
	
	public void setFlight(int flight) {
		this.flight = flight;
	}
	public int getFlight() {
		return flight;
	}
}
