package com.example.android.members.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.members.data.MemberContract.MemberEntry;

/**
 * Created by Ashutosh on 25-05-2017.
 */
public class MemberProvider extends ContentProvider {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = MemberProvider.class.getSimpleName();

    private MemberDbHelper mDbHelper;

    /**
     * URI matcher code for the content URI for the members table
     */
    private static final int MEMBERS = 100;

    /**
     * URI matcher code for the content URI for a single pet in the members table
     */
    private static final int MEMBER_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher
        sUriMatcher.addURI(MemberContract.CONTENT_AUTHORITY, MemberContract.PATH_MEMBERS, MEMBERS);
        sUriMatcher.addURI(MemberContract.CONTENT_AUTHORITY, MemberContract.PATH_MEMBERS + "/#", MEMBER_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a MemberDbHelper object to gain access to the members database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new MemberDbHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                // For the PETS code, query the members table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the members table.
                // TODO: Perform database query on members table
                cursor = database.query(MemberEntry.TABLE_NAME, projection, null, null,
                        null, null, sortOrder);
                break;
            case MEMBER_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.members/members/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = MemberEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the members table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(MemberEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Setting up the notificationUri method
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                return insertMember(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertMember(Uri uri, ContentValues values) {

        // TODO: Insert a new pet into the members database table with the given ContentValues
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Check that the name is not null
        String name = values.getAsString(MemberEntry.COLUMN_MEMBER_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Member requires a name");
        }
        // Check that the father name is not null
        String fatherName = values.getAsString(MemberEntry.COLUMN_MEMBER_FATHER_NAME);
        if (fatherName == null) {
            throw new IllegalArgumentException("Member requires a father name");
        }
        // Check that the code is not null
        Integer code = values.getAsInteger(MemberEntry.COLUMN_MEMBER_CODE);
        if (code == null || code <= 0) {
            throw new IllegalArgumentException("Member requires a code and code must be non-negative");
        }
        // Check that the phone is not null
        String phone = values.getAsString(MemberEntry.COLUMN_MEMBER_PHONE);
        if (phone == null) {
            throw new IllegalArgumentException("Member requires a phone");
        }
        // Check that the date is not null
        String date = values.getAsString(MemberEntry.COLUMN_MEMBER_DATE);
        if (date == null) {
            throw new IllegalArgumentException("Member requires a date");
        }
        // Check that the father name is not null
        Integer weight = values.getAsInteger(MemberEntry.COLUMN_MEMBER_WEIGHT);
        if (weight == null || weight <= 0) {
            throw new IllegalArgumentException("Member requires a weight and weight must be non-negative");
        }
        // inserting values to database
        long id = database.insert(MemberEntry.TABLE_NAME, null, values);
        // if id = -1, log insert failed to log
        if (id == -1) {
            Log.e(null, "insertMember: insert fail");
        }
        // Notify all the users that content URI has changed
        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case MEMBER_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = MemberEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update members in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more members).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // TODO: Update the selected members in the members database table with the given ContentValues

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Check that the name is not null
        if (values.containsKey(MemberEntry.COLUMN_MEMBER_NAME)) {
            String name = values.getAsString(MemberEntry.COLUMN_MEMBER_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Member requires a name");
            }
        }
        if (values.containsKey(MemberEntry.COLUMN_MEMBER_FATHER_NAME)) {
            // Check that the father name is not null
            String fatherName = values.getAsString(MemberEntry.COLUMN_MEMBER_FATHER_NAME);
            if (fatherName == null) {
                throw new IllegalArgumentException("Member requires a father name");
            }
        }

        if (values.containsKey(MemberEntry.COLUMN_MEMBER_CODE)) {
            // Check that the code is not null
            Integer code = values.getAsInteger(MemberEntry.COLUMN_MEMBER_CODE);
            if (code == null || code <= 0) {
                throw new IllegalArgumentException("Member requires a code and code must be non-negative");
            }
        }
        if (values.containsKey(MemberEntry.COLUMN_MEMBER_PHONE)) {
            // Check that the phone is not null
            String phone = values.getAsString(MemberEntry.COLUMN_MEMBER_PHONE);
            if (phone == null) {
                throw new IllegalArgumentException("Member requires a phone");
            }
        }
        if (values.containsKey(MemberEntry.COLUMN_MEMBER_DATE)) {
            // Check that the date is not null
            String date = values.getAsString(MemberEntry.COLUMN_MEMBER_DATE);
            if (date == null) {
                throw new IllegalArgumentException("Member requires a date");
            }
        }
        if (values.containsKey(MemberEntry.COLUMN_MEMBER_WEIGHT)) {
            // Check that the father name is not null
            Integer weight = values.getAsInteger(MemberEntry.COLUMN_MEMBER_FATHER_NAME);
            if (weight == null || weight <= 0) {
                throw new IllegalArgumentException("Member requires a weight and weight must be non-negative");
            }
        }


        // Update row
        int id = database.update(MemberEntry.TABLE_NAME, values, selection, selectionArgs);
        // Notify all the users that content URI has changed
        getContext().getContentResolver().notifyChange(uri, null);
        // TODO: Return the number of rows that were affected
        return id;
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                // Delete all rows that match the selection and selection args
                return database.delete(MemberEntry.TABLE_NAME, selection, selectionArgs);
            case MEMBER_ID:
                // Delete a single row given by the ID in the URI
                selection = MemberEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // Notify all the users that content URI has changed
                getContext().getContentResolver().notifyChange(uri, null);

                return database.delete(MemberEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }


    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                return MemberEntry.CONTENT_LIST_TYPE;
            case MEMBER_ID:
                return MemberEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}