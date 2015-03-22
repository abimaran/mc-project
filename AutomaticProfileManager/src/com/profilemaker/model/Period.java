package com.profilemaker.model;

import java.util.ArrayList;

public class Period {

	private int id;
	private ArrayList<String> days;
	private int startHour;
	private int endHour;
	private int startMinute;
	private int endMinute;
	
	public Period(int id, ArrayList<String> days,int startHour, int endHour, int startMinute,int endMinute){
		this.id = id;
		this.days = days;
		this.startHour = startHour;
		this.endHour = endHour;
		this.startMinute = startMinute;
		this.endMinute = endMinute;
	}
	/*public void setStartTime(float startTime){
		this.startTime = startTime;
	}*/
	public float getId(){
		return id;
	}
	
	public void setDays(ArrayList<String> days){
		this.days = days;
	}	
	public ArrayList<String> getDays(){
		return days;
	}
	
	public void setStartHour(int startHour){
		this.startHour = startHour;
	}	
	public int getStartHour(){
		return startHour;
	}
	
	public void setStartMinute(int startMinute){
		this.startMinute = startMinute;
	}	
	public int getStartMinute(){
		return startMinute;
	}
	
	public void setEndHour(int endHour){
		this.endHour = endHour;
	}	
	public int getEndHour(){
		return endHour;
	}
	
	public void setEndMinute(int endMinute){
		this.endMinute = endMinute;
	}	
	public int getEndMinute(){
		return endMinute;
	}
}