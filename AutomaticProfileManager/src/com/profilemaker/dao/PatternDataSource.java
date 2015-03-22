package com.profilemaker.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.profilemaker.db.DataBaseHelper;
import com.profilemaker.model.Pattern;

public class PatternDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DataBaseHelper dbHelper;
	private String[] allColumns = {DataBaseHelper.COLUMN_PATTERN_ID,
			DataBaseHelper.COLUMN_DAY, DataBaseHelper.COLUMN_START_HOUR, DataBaseHelper.COLUMN_START_MINUTE,
			DataBaseHelper.COLUMN_LATITUDE, DataBaseHelper.COLUMN_LONGITUDE, DataBaseHelper.COLUMN_SILENT,
			DataBaseHelper.COLUMN_VIBRATION, DataBaseHelper.COLUMN_NORMAL, DataBaseHelper.COLUMN_FLIGHT};

	public PatternDataSource(Context context) {
		dbHelper = new DataBaseHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void openReadable() throws SQLException {
		database = dbHelper.getReadableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public Pattern createPattern(int day, int startHour, int startMinute, 
			double latitude, double longitude, int silent, int vibration, int normal, int flight) {
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.COLUMN_DAY, day);
		values.put(DataBaseHelper.COLUMN_START_HOUR, startHour);
		values.put(DataBaseHelper.COLUMN_START_MINUTE, startMinute);
		values.put(DataBaseHelper.COLUMN_LATITUDE, latitude);
		values.put(DataBaseHelper.COLUMN_LONGITUDE, longitude);
		values.put(DataBaseHelper.COLUMN_SILENT, silent);
		values.put(DataBaseHelper.COLUMN_VIBRATION, vibration);
		values.put(DataBaseHelper.COLUMN_NORMAL, normal);
		values.put(DataBaseHelper.COLUMN_FLIGHT, flight);
		long insertId = database.insert(DataBaseHelper.TABLE_PATTERN, null, values);
		Log.i("insert id",""+insertId);
		Cursor cursor = database.query(DataBaseHelper.TABLE_PATTERN,
				allColumns, DataBaseHelper.COLUMN_PATTERN_ID + " = " + insertId, null,null, null, null);
		cursor.moveToFirst();
		Pattern newPattern = cursorToPattern(cursor);
		cursor.close();
		return newPattern;
	}
	
	public void deletePattern(Pattern pattern) {
		int id = pattern.getPatternId();
		System.out.println("Pattern deleted with id: " + id);
		database.delete(DataBaseHelper.TABLE_PATTERN, DataBaseHelper.COLUMN_PATTERN_ID
				+ " = " + id, null);
	}
	
	public List<Pattern> getAllPatterns() {
		List<Pattern> patterns = new ArrayList<Pattern>();

		Cursor cursor = database.query(DataBaseHelper.TABLE_PATTERN,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Pattern pattern = cursorToPattern(cursor);
			patterns.add(pattern);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return patterns;
	}
	
	public void updatePattern(int patternId, String column, int value){
		ContentValues values = new ContentValues();
		values.put(column, value);
		database.update(DataBaseHelper.TABLE_PATTERN, values, DataBaseHelper.COLUMN_PATTERN_ID
				+ " = " + patternId, null);
	}
	
	private Pattern cursorToPattern(Cursor cursor) {
		Pattern pattern = new Pattern();
		pattern.setPatternId(cursor.getInt(0));
		pattern.setDay(cursor.getInt(1));
		pattern.setStartHour(cursor.getInt(2));
		pattern.setStartMinute(cursor.getInt(3));
		pattern.setLatitude(cursor.getDouble(4));
		pattern.setLongitude(cursor.getDouble(5));
		pattern.setSilent(cursor.getInt(6));
		pattern.setVibration(cursor.getInt(7));
		pattern.setNormal(cursor.getInt(8));
		pattern.setFlight(cursor.getInt(8));
		return pattern;
	}
}
