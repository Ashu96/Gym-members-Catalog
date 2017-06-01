package com.example.android.members.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.members.data.MemberContract.MemberEntry;

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class MemberDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MemberDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "gym.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link MemberDbHelper}.
     *
     * @param context of the app
     */
    public MemberDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the members table
        String SQL_CREATE_MEMBERS_TABLE =  "CREATE TABLE " + MemberEntry.TABLE_NAME + " ("
                + MemberEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MemberEntry.COLUMN_MEMBER_NAME + " TEXT NOT NULL, "
                + MemberEntry.COLUMN_MEMBER_FATHER_NAME + " TEXT NOT NULL, "
                + MemberEntry.COLUMN_MEMBER_CODE + " INTEGER NOT NULL, "
                + MemberEntry.COLUMN_MEMBER_PHONE + " TEXT NOT NULL, "
                + MemberEntry.COLUMN_MEMBER_DATE + " TEXT NOT NULL, "
                + MemberEntry.COLUMN_MEMBER_WEIGHT + " INTEGER NOT NULL );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_MEMBERS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}