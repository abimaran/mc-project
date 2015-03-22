package com.profilemaker.dao;

import java.util.ArrayList;
import java.util.List;
import com.profilemaker.db.DataBaseHelper;
import com.profilemaker.model.Profile;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProfileDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DataBaseHelper dbHelper;
	private String[] allColumns = { DataBaseHelper.COLUMN_ID,
			DataBaseHelper.COLUMN_NAME, DataBaseHelper.COLUMN_RING_MODE, DataBaseHelper.COLUMN_RINGTONE,
			DataBaseHelper.COLUMN_RINGTONE_VOLUME, DataBaseHelper.COLUMN_NOTIFICATION_MODE, DataBaseHelper.COLUMN_NOTIFICATION_TONE,
			DataBaseHelper.COLUMN_NOTIFICATION_VOLUME, DataBaseHelper.COLUMN_MEDIA_VOLUME, DataBaseHelper.COLUMN_ACTIVATED};
	
	public ProfileDataSource(Context context) {
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

	public Profile createProfile(String name, int ringMode, String ringtone, int ringtoneVolume, 
			int notificationMode, String notificationTone, int notificationToneVolume, int mediaVolume, int activated) {
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.COLUMN_NAME, name);
		values.put(DataBaseHelper.COLUMN_RING_MODE, ringMode);
		values.put(DataBaseHelper.COLUMN_RINGTONE, ringtone);
		values.put(DataBaseHelper.COLUMN_RINGTONE_VOLUME, ringtoneVolume);
		values.put(DataBaseHelper.COLUMN_NOTIFICATION_MODE, notificationMode);
		values.put(DataBaseHelper.COLUMN_NOTIFICATION_TONE, notificationTone);
		values.put(DataBaseHelper.COLUMN_NOTIFICATION_VOLUME, notificationToneVolume);
		values.put(DataBaseHelper.COLUMN_MEDIA_VOLUME, mediaVolume);
		values.put(DataBaseHelper.COLUMN_ACTIVATED, activated);
		long insertId = database.insert(DataBaseHelper.TABLE_PROFILE, null, values);
		Cursor cursor = database.query(DataBaseHelper.TABLE_PROFILE,
				allColumns, DataBaseHelper.COLUMN_ID + " = " + insertId, null,null, null, null);
		cursor.moveToFirst();
		Profile newProfile = cursorToProfile(cursor);
		cursor.close();
		return newProfile;
	}

	//update the profile activated column when profile is activated....
	public void updateActivated(int profileId){
		
		//Change the old activated profile as de-activated
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.COLUMN_ACTIVATED, 0);
		database.update(DataBaseHelper.TABLE_PROFILE, values, DataBaseHelper.COLUMN_ACTIVATED + "=" + 1, null);
		values.clear();
		
		//Change the new activated profile as activated
		values.put(DataBaseHelper.COLUMN_ACTIVATED, 1);
		int i = database.update(DataBaseHelper.TABLE_PROFILE, values, DataBaseHelper.COLUMN_ID + "=" + profileId, null);
		Log.i("Update Profile", ""+i);
	}
	
	public void deleteProfile(String profileName) {
		//int id = profile.getId();
		//System.out.println("Profile deleted with id: " + id);
		database.delete(DataBaseHelper.TABLE_PROFILE, DataBaseHelper.COLUMN_NAME
				+ " = "+"'"+profileName+"'", null);
	}

	public String[] getAllProfileNames(){
		String [] columns = {DataBaseHelper.COLUMN_ID, DataBaseHelper.COLUMN_NAME};
		Cursor cursor = database.query(DataBaseHelper.TABLE_PROFILE,
				columns, null, null, null, null, null);
		cursor.moveToFirst();
		String [] names = new String[cursor.getCount()];
		
		int i = 0;		
		while(!cursor.isAfterLast()){
			names[i] = cursor.getString(1);
			cursor.moveToNext();
			i++;
		}
		cursor.close();		
		return names;
	} 
	
	public Profile getProfile(String profileName){
		Cursor cursor = database.query(DataBaseHelper.TABLE_PROFILE, allColumns, 
				DataBaseHelper.COLUMN_NAME + " = "+"'"+profileName+"'", null, null, null, null);
		cursor.moveToFirst();
		Profile profile = cursorToProfile(cursor);
		cursor.close();
		
		return profile;
	}
	
	public Profile getProfile(int profileId){
		Cursor cursor = database.query(DataBaseHelper.TABLE_PROFILE, allColumns, 
				DataBaseHelper.COLUMN_ID + " = "+"'"+profileId+"'", null, null, null, null);
		cursor.moveToFirst();
		Profile profile = cursorToProfile(cursor);
		cursor.close();
		
		return profile;
	}
	
	public List<Profile> getAllProfiles() {
		List<Profile> profiles = new ArrayList<Profile>();

		Cursor cursor = database.query(DataBaseHelper.TABLE_PROFILE,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Profile profile = cursorToProfile(cursor);
			profiles.add(profile);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return profiles;
	}

	private Profile cursorToProfile(Cursor cursor) {
		Profile profile = new Profile();
		profile.setId(cursor.getInt(0));
		profile.setName(cursor.getString(1));
		profile.setRingMode(cursor.getInt(2));
		profile.setRingtone(cursor.getString(3));
		profile.setRingtoneVolume(cursor.getInt(4));
		profile.setNotificationMode(cursor.getInt(5));
		profile.setNotificationTone(cursor.getString(6));
		profile.setNotificationToneVolume(cursor.getInt(7));
		profile.setMediaVolume(cursor.getInt(8));
		profile.setActivated(cursor.getInt(9));
		return profile;
	}
}
