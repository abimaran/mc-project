package com.mobileproject.smartprofileselector.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobileproject.smartprofileselector.db.DatabaseHelper;
import com.mobileproject.smartprofileselector.models.Profile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumudu on 4/10/2015.
 */
public class UserProfileDataSource
{
    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    // User Profile Table fields.
    private String[] allColumns = { DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_RING_MODE, DatabaseHelper.COLUMN_RINGTONE,
            DatabaseHelper.COLUMN_RINGTONE_VOLUME, DatabaseHelper.COLUMN_NOTIFICATION_MODE, DatabaseHelper.COLUMN_NOTIFICATION_TONE,
            DatabaseHelper.COLUMN_NOTIFICATION_VOLUME, DatabaseHelper.COLUMN_MEDIA_VOLUME, DatabaseHelper.COLUMN_ACTIVATED};

    public UserProfileDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
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
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_RING_MODE, ringMode);
        values.put(DatabaseHelper.COLUMN_RINGTONE, ringtone);
        values.put(DatabaseHelper.COLUMN_RINGTONE_VOLUME, ringtoneVolume);
        values.put(DatabaseHelper.COLUMN_NOTIFICATION_MODE, notificationMode);
        values.put(DatabaseHelper.COLUMN_NOTIFICATION_TONE, notificationTone);
        values.put(DatabaseHelper.COLUMN_NOTIFICATION_VOLUME, notificationToneVolume);
        values.put(DatabaseHelper.COLUMN_MEDIA_VOLUME, mediaVolume);
        values.put(DatabaseHelper.COLUMN_ACTIVATED, activated);
        long insertId = database.insert(DatabaseHelper.TABLE_PROFILE, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_PROFILE,
                allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId, null,null, null, null);
        cursor.moveToFirst();
        Profile newProfile = cursorToProfile(cursor);
        cursor.close();
        return newProfile;
    }

    // Update the profile activated column when profile is activated....
    public void updateActivated(int profileId){

        //Change the old activated profile as de-activated
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ACTIVATED, 0);
        database.update(DatabaseHelper.TABLE_PROFILE, values, DatabaseHelper.COLUMN_ACTIVATED + "=" + 1, null);
        values.clear();

        //Change the new activated profile as activated
        values.put(DatabaseHelper.COLUMN_ACTIVATED, 1);
        int i = database.update(DatabaseHelper.TABLE_PROFILE, values, DatabaseHelper.COLUMN_ID + "=" + profileId, null);
        Log.i("Update Profile", "" + i);
    }

    public void deleteProfile(String profileName) {
        //int id = profile.getId();
        //System.out.println("Profile deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_PROFILE, DatabaseHelper.COLUMN_NAME
                + " = "+"'"+profileName+"'", null);
    }

    public String[] getAllProfileNames(){
        String[] columns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME};
        Cursor cursor = database.query(DatabaseHelper.TABLE_PROFILE,
                columns, null, null, null, null, null);
        cursor.moveToFirst();
        String[] names = new String[cursor.getCount()];

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
        Cursor cursor = database.query(DatabaseHelper.TABLE_PROFILE, allColumns,
                DatabaseHelper.COLUMN_NAME + " = "+"'"+profileName+"'", null, null, null, null);
        cursor.moveToFirst();
        Profile profile = cursorToProfile(cursor);
        cursor.close();

        return profile;
    }

    public Profile getProfile(int profileId){
        Cursor cursor = database.query(DatabaseHelper.TABLE_PROFILE, allColumns,
                DatabaseHelper.COLUMN_ID + " = "+"'"+profileId+"'", null, null, null, null);
        cursor.moveToFirst();
        Profile profile = cursorToProfile(cursor);
        cursor.close();

        return profile;
    }

    public List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<Profile>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_PROFILE,
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

    private Profile cursorToProfile(Cursor cursor)
    {
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
