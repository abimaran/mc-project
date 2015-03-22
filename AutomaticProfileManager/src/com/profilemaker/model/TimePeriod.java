package com.profilemaker.model;

public class TimePeriod {

	private int periodId;
	private int profileId;
	private String day;
	private int startHour;
	private int startMinute;
	private int endHour;
	private int endMinute;
	
	public void setPeriodId(int periodId) {
		this.periodId = periodId;
	}
	public int getPeriodId() {
		return periodId;
	}
	
	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}
	public int getProfileId() {
		return profileId;
	}
	
	public void setDay(String day) {
		this.day = day;
	}
	public String getDay() {
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
	
	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}
	public int getEndHour() {
		return endHour;
	}
	
	public void setEndMinute(int endMinute) {
		this.endMinute = endMinute;
	}
	public int getEndMinute() {
		return endMinute;
	}
}