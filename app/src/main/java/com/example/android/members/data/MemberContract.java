package com.example.android.members.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ashutosh on 24-05-2017.
 */
public final class MemberContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private MemberContract() {}
    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.members";
    /**
            * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
            * the content provider.
    */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.members/members/ is a valid path for
     * looking at pet data. content://com.example.android.members/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_MEMBERS = "members";


    /**
     * Inner class that defines constant values for the members database table.
     * Each entry in the table represents a single member.
     */
    public static final class MemberEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_MEMBERS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of members.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEMBERS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEMBERS;



        /** Constants for database schema */
        public static final String TABLE_NAME = "members";
        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;
        /**
         * Name of member
         *
         * Type TEXT
         */
        public static final String COLUMN_MEMBER_NAME = "name";
        /**
         * Father name of member
         *
         * Type TEXT
         */
        public static final String COLUMN_MEMBER_FATHER_NAME = "father";
        /**
         * Code number of member
         *
         * Type INTEGER
         */
        public static final String COLUMN_MEMBER_CODE = "code";
        /**
         * Phone number of member
         *
         * Type TEXT
         */
        public static final String COLUMN_MEMBER_PHONE = "phone";
        /**
         * Addmision date of member
         *
         * Type TEXT
         */
        public static final String COLUMN_MEMBER_DATE = "date";
        /**
         * Weight of member
         *
         * Type INTEGER
         */
        public static final String COLUMN_MEMBER_WEIGHT = "weight";
    }

}
