package com.example.android.members;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.android.members.data.MemberContract.MemberEntry;


/**
 * Displays list of members that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int MEMBER_LOADER = 0;

    private MemberCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the member data
        ListView memberListView = (ListView) findViewById(R.id.list_view_member);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        memberListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of member data in the Cursor.
        // There is no member data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new MemberCursorAdapter(this, null);
        memberListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific member that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.members/members/2"
                // if the member with ID 2 was clicked on.
                Uri currentPetUri = ContentUris.withAppendedId(MemberEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                // Launch the {@link EditorActivity} to display the data for the current member.
                startActivity(intent);
            }
        });

        // Kick off the loader
       getLoaderManager().initLoader(MEMBER_LOADER, null, this);
    }



    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the members database.
     */
    private void displayDatabaseInfo() {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                MemberEntry._ID,
                MemberEntry.COLUMN_MEMBER_NAME,
                MemberEntry.COLUMN_MEMBER_FATHER_NAME,
                MemberEntry.COLUMN_MEMBER_CODE,
                MemberEntry.COLUMN_MEMBER_PHONE,
                MemberEntry.COLUMN_MEMBER_DATE,
                MemberEntry.COLUMN_MEMBER_WEIGHT };

        Cursor cursor = getContentResolver().query(MemberEntry.CONTENT_URI,projection, null, null, null);

        // Find ListView to populate
        ListView listViewItems = (ListView) findViewById(R.id.list_view_member);
        // Setup cursor adapter using cursor from last step
        MemberCursorAdapter memberAdapter = new MemberCursorAdapter(this, cursor);
        // Attach cursor adapter to the ListView
        listViewItems.setAdapter(memberAdapter);


    }

    /**
     * Helper method to insert hardcoded member data into the database. For debugging purposes only.
     */
    private void insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's member attributes are the values.
        ContentValues values = new ContentValues();
        values.put(MemberEntry.COLUMN_MEMBER_NAME, "Jhon");
        values.put(MemberEntry.COLUMN_MEMBER_FATHER_NAME, "Ross");
        values.put(MemberEntry.COLUMN_MEMBER_CODE, 7);
        values.put(MemberEntry.COLUMN_MEMBER_PHONE, "9876543210");
        values.put(MemberEntry.COLUMN_MEMBER_DATE, "19/12/2016");
        values.put(MemberEntry.COLUMN_MEMBER_WEIGHT, 70);

        Uri newRowUri = getContentResolver().insert(MemberEntry.CONTENT_URI,values);
    }

    private void deleteAll() {
        int mRowUpdated = getContentResolver().delete(MemberEntry.CONTENT_URI, null, null);
        if (mRowUpdated == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, getString(R.string.editor_delete_member_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Show Toast message for updated member
            Toast.makeText(this, R.string.editor_delete_member_successful,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MemberEntry._ID,
                MemberEntry.COLUMN_MEMBER_NAME,
                MemberEntry.COLUMN_MEMBER_FATHER_NAME
        };

        return new CursorLoader(this, // context
                MemberEntry.CONTENT_URI, // content provider Uri
                projection, // projection
                null, // selection
                null, // selection args
                null // sort order
        );
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mCursorAdapter.swapCursor(null);

    }


}