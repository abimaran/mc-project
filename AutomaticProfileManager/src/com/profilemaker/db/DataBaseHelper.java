package com.profilemaker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{

	private static final String DATABASE_NAME = "profile_manager.db";
	private static final int DATABASE_VERSION = 1;
	
	//Profile table columns..........
	public static final String TABLE_PROFILE = "profile";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_RING_MODE = "ring_mode";
	public static final String COLUMN_RINGTONE = "ringtone";
	public static final String COLUMN_RINGTONE_VOLUME = "ringtone_volume";
	public static final String COLUMN_NOTIFICATION_MODE = "notification_mode";
	public static final String COLUMN_NOTIFICATION_TONE = "notification_tone";
	public static final String COLUMN_NOTIFICATION_VOLUME = "notification_volume";
	public static final String COLUMN_MEDIA_VOLUME = "media_volume";
	public static final String COLUMN_ACTIVATED = "activated";
	
	//Time period table columns.........
	public static final String TABLE_TIMEPERIOD = "time_period";
	public static final String COLUMN_PERIOD_ID = "period_id";
	public static final String COLUMN_PROFILE_ID = "profile_id";
	public static final String COLUMN_DAY = "day";
	public static final String COLUMN_START_HOUR = "start_hour";
	public static final String COLUMN_START_MINUTE = "start_minute";
	public static final String COLUMN_END_HOUR = "end_hour";
	public static final String COLUMN_END_MINUTE = "end_minute";
	
	//locations table columns.............
	public static final String TABLE_LOCATION = "location";
	public static final String COLUMN_LOCATION_ID = "location_id";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	
	//pattern table columns................
	public static final String TABLE_PATTERN = "pattern";
	public static final String COLUMN_PATTERN_ID = "pattern_id";
	public static final String COLUMN_SILENT = "silent";
	public static final String COLUMN_VIBRATION = "vibration";
	public static final String COLUMN_NORMAL = "normal";
	public static final String COLUMN_FLIGHT = "flight";
	
	// Database creation sql statement
	private static final String TABLE_CREATE_PROFILE = "create table if not exists "
			+ TABLE_PROFILE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NAME
			+ " text not null," + COLUMN_RING_MODE + " integer,"+ COLUMN_RINGTONE + " text," 
			+ COLUMN_RINGTONE_VOLUME + " integer, " + COLUMN_NOTIFICATION_MODE+ " integer,"+ COLUMN_NOTIFICATION_TONE + " text, "
			+ COLUMN_NOTIFICATION_VOLUME + " integer," + COLUMN_MEDIA_VOLUME + " integer,"
			+ COLUMN_ACTIVATED +" integer);";
	
	private static final String TABLE_CREATE_TIMEPERIOD = "create table if not exists "
		+ TABLE_TIMEPERIOD + "(" + COLUMN_PERIOD_ID
		+ " integer primary key autoincrement, " + COLUMN_PROFILE_ID
		+ " integer," + COLUMN_DAY + " text,"+ COLUMN_START_HOUR + " integer," 
		+ COLUMN_START_MINUTE + " integer, " + COLUMN_END_HOUR+ " integer,"+ COLUMN_END_MINUTE + " integer);";
	
	private static final String TABLE_CREATE_LOCATION = "create table if not exists "
		+ TABLE_LOCATION + "(" + COLUMN_LOCATION_ID
		+ " integer primary key autoincrement, " + COLUMN_PROFILE_ID
		+ " integer," + COLUMN_LATITUDE + " double,"+ COLUMN_LONGITUDE + " double);";
	
	private static final String TABLE_CREATE_PATTERN = "create table if not exists "
		+ TABLE_PATTERN + "(" + COLUMN_PATTERN_ID + " integer primary key autoincrement, " 
		+ COLUMN_DAY + " integer," + COLUMN_START_HOUR + " integer," + COLUMN_START_MINUTE + " integer,"
		+ COLUMN_LATITUDE + " double," + COLUMN_LONGITUDE + " double," + COLUMN_SILENT + " integer,"
		+ COLUMN_VIBRATION + " integer," + COLUMN_NORMAL + " integer," + COLUMN_FLIGHT + " integer);";
	
	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null,DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE_PROFILE);
		database.execSQL(TABLE_CREATE_TIMEPERIOD);	
		database.execSQL(TABLE_CREATE_LOCATION);
		database.execSQL(TABLE_CREATE_PATTERN);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DataBaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMEPERIOD);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATTERN);
		onCreate(db);
		
	}

}
