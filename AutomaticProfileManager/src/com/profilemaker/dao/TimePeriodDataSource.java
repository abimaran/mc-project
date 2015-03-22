package com.profilemaker.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.profilemaker.db.DataBaseHelper;
import com.profilemaker.model.TimePeriod;

public class TimePeriodDataSource {

	private SQLiteDatabase database;
	private DataBaseHelper dbHelper;
	private String[] allColumns = { DataBaseHelper.COLUMN_PERIOD_ID,
			DataBaseHelper.COLUMN_PROFILE_ID, DataBaseHelper.COLUMN_DAY, DataBaseHelper.COLUMN_START_HOUR,
			DataBaseHelper.COLUMN_START_MINUTE, DataBaseHelper.COLUMN_END_HOUR, 
			DataBaseHelper.COLUMN_END_MINUTE};
	
	public TimePeriodDataSource(Context context) {
		dbHelper = new DataBaseHelper(context);
	}
		
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public TimePeriod createTimePeriod(int profileId, String day, int startHour, int startMinute, 
			int endHour, int endMinute) {
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.COLUMN_PROFILE_ID, profileId);
		values.put(DataBaseHelper.COLUMN_DAY, day);
		values.put(DataBaseHelper.COLUMN_START_HOUR, startHour);
		values.put(DataBaseHelper.COLUMN_START_MINUTE, startMinute);
		values.put(DataBaseHelper.COLUMN_END_HOUR, endHour);
		values.put(DataBaseHelper.COLUMN_END_MINUTE, endMinute);
		
		long insertId = database.insert(DataBaseHelper.TABLE_TIMEPERIOD, null, values);
		Cursor cursor = database.query(DataBaseHelper.TABLE_TIMEPERIOD,
				allColumns, DataBaseHelper.COLUMN_PERIOD_ID + " = " + insertId, null,null, null, null);
		cursor.moveToFirst();
		TimePeriod newTimePeriod = cursorToTimePeriod(cursor);
		cursor.close();
		return newTimePeriod;
	}
	
	public void deleteTimePeriod(String profileName) {
		//int periodId = timePeriod.getPeriodId();
		//Get the profile ID of the profile that should be deleted
		String[] columns = {DataBaseHelper.COLUMN_ID};
		Cursor cursor = database.query(DataBaseHelper.TABLE_PROFILE, columns, 
				DataBaseHelper.COLUMN_NAME + " = " + "'" +profileName+ "'", null, null, null, null);
		cursor.moveToFirst();
		int profileID = cursor.getInt(0);
		cursor.close();
		
		//delete the profile..............................................................
		database.delete(DataBaseHelper.TABLE_TIMEPERIOD, DataBaseHelper.COLUMN_PERIOD_ID
				+ " = " +profileID, null);
	}

	public List<TimePeriod> getAllTimePeriods() {
		List<TimePeriod> timePeriods = new ArrayList<TimePeriod>();

		Cursor cursor = database.query(DataBaseHelper.TABLE_TIMEPERIOD,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TimePeriod timePeriod = cursorToTimePeriod(cursor);
			timePeriods.add(timePeriod);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return timePeriods;
	}
	
	public List<TimePeriod> getTimePeriods(int profileId) {
		List<TimePeriod> timePeriods = new ArrayList<TimePeriod>();

		Cursor cursor = database.query(DataBaseHelper.TABLE_TIMEPERIOD,
				allColumns, DataBaseHelper.COLUMN_PROFILE_ID
				+ " = " + profileId, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TimePeriod timePeriod = cursorToTimePeriod(cursor);
			timePeriods.add(timePeriod);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return timePeriods;
	}
	
	private TimePeriod cursorToTimePeriod(Cursor cursor) {
		TimePeriod timePeriod = new TimePeriod();
		timePeriod.setPeriodId(cursor.getInt(0));
		timePeriod.setProfileId(cursor.getInt(1));
		timePeriod.setDay(cursor.getString(2));
		timePeriod.setStartHour(cursor.getInt(3));
		timePeriod.setStartMinute(cursor.getInt(4));
		timePeriod.setEndHour(cursor.getInt(5));
		timePeriod.setEndMinute(cursor.getInt(6));
		return timePeriod;
	}

}
