package com.mobileproject.smartprofileselector.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sumudu.w on 4/10/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    // Name of the database
    private static final String DATABASE_NAME = "profile_manager.db";
    // Current Version of database
    private static final int DATABASE_VERSION = 1;

    //User Profile table columns
    public static final String TABLE_PROFILE = "user_profile";
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

    // Database creation sql statements

    // User Profile Table Creation SQL
    private static final String TABLE_CREATE_PROFILE = "create table if not exists "
            + TABLE_PROFILE + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NAME
            + " text not null," + COLUMN_RING_MODE + " integer,"+ COLUMN_RINGTONE + " text,"
            + COLUMN_RINGTONE_VOLUME + " integer, " + COLUMN_NOTIFICATION_MODE+ " integer,"+ COLUMN_NOTIFICATION_TONE + " text, "
            + COLUMN_NOTIFICATION_VOLUME + " integer," + COLUMN_MEDIA_VOLUME + " integer,"
            + COLUMN_ACTIVATED +" integer);";



    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(TABLE_CREATE_PROFILE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);

    }
}
