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
import com.profilemaker.model.Location;

public class LocationDataSource {

	private SQLiteDatabase database;
	private DataBaseHelper dbHelper;
	private String[] allColumns = { DataBaseHelper.COLUMN_LOCATION_ID, DataBaseHelper.COLUMN_PROFILE_ID,
			DataBaseHelper.COLUMN_LATITUDE, DataBaseHelper.COLUMN_LONGITUDE};
	
	public LocationDataSource(Context context) {
		dbHelper = new DataBaseHelper(context);
	}
		
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void openReadable() throws SQLException{
		database = dbHelper.getReadableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	//insert location record into the table...................................
	public Location createLocation(int profileId, double latitude, double longitude) {
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.COLUMN_PROFILE_ID, profileId);
		values.put(DataBaseHelper.COLUMN_LATITUDE, latitude);
		values.put(DataBaseHelper.COLUMN_LONGITUDE, longitude);
		
		long insertId = database.insert(DataBaseHelper.TABLE_LOCATION, null, values);
		Log.i("insert id",""+insertId);
		Cursor cursor = database.query(DataBaseHelper.TABLE_LOCATION,
				allColumns, DataBaseHelper.COLUMN_LOCATION_ID + " = " + insertId, null,null, null, null);
		cursor.moveToFirst();
		Location newLocation = cursorToLocation(cursor);
		cursor.close();
		return newLocation;
	}
	
	public void deleteLocation(String profileName) {
		//int locationId = location.getLocationId();
		//System.out.println("Time Location deleted with id: " + locationId);
		String[] columns = {DataBaseHelper.COLUMN_ID};
		Cursor cursor = database.query(DataBaseHelper.TABLE_PROFILE, columns, 
				DataBaseHelper.COLUMN_NAME + " = " + "'" +profileName+ "'", null, null, null, null);
		cursor.moveToFirst();
		int profileID = cursor.getInt(0);
		cursor.close();
		database.delete(DataBaseHelper.TABLE_LOCATION, DataBaseHelper.COLUMN_PROFILE_ID
				+ " = " + profileID, null);
	}
	
	//get the all location records from table.....................
	public List<Location> getAllLocations() {
		List<Location> locations = new ArrayList<Location>();

		Cursor cursor = database.query(DataBaseHelper.TABLE_LOCATION,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Location location = cursorToLocation(cursor);
			locations.add(location);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return locations;
	}
	
	private Location cursorToLocation(Cursor cursor) {
		Location location = new Location(0,0.0,0.0);
		location.setLocationId(cursor.getInt(0));
		location.setProfileId(cursor.getInt(1));
		location.setLatitude(cursor.getDouble(2));
		location.setLongitude(cursor.getDouble(3));
		return location;
	}
}
