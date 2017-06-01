/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.members;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.members.data.MemberContract.MemberEntry;


/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** EditText field to enter the member's name */
    private EditText mNameEditText;

    /** EditText field to enter the member's father name */
    private EditText mFatherEditText;

    /** EditText field to enter the member's code */
    private EditText mCodeEditText;

    /** EditText field to enter the member's phone */
    private EditText mPhoneEditText;

    /** EditText field to enter the member's date */
    private EditText mDateEditText;

    /** EditText field to enter the member's weight */
    private EditText mWeightEditText;

    /** Identifier for the pet data loader */
    private static final int EXISTING_MEMBER_LOADER = 0;

    /** Content URI for the existing member (null if it's a new member) */
    private  Uri mCurrentMemberUri;

    /** Boolean flag to keep track of whether member is new (true) or not (false) */
    private boolean mIsNewMember = true;

    /** Boolean flag to keep track of whether member has beed edited (true) or not (false) */
    private boolean mMemberHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mMemberHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new member or editing an existing one.
        Intent intent = getIntent();
        mCurrentMemberUri = intent.getData();

        // If the intent DOES NOT contain a member content URI, then we know that we are
        // creating a new member
        if (mCurrentMemberUri == null) {
            // This is a new member, so change the app bar to say "Add a Member"
            setTitle("Add a Member");

            // Invalidate the options menu 
            invalidateOptionsMenu();
        } else {
            setTitle("Edit a Member");
            mIsNewMember = false;
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_member_name);
        mFatherEditText = (EditText) findViewById(R.id.edit_member_father_name);
        mCodeEditText= (EditText) findViewById(R.id.edit_member_code);
        mPhoneEditText = (EditText) findViewById(R.id.edit_member_phone);
        mDateEditText = (EditText) findViewById(R.id.edit_member_date);
        mWeightEditText = (EditText) findViewById(R.id.edit_member_weight);

        // Attaching OnTouchListener to all relevant views
        mNameEditText.setOnTouchListener(mTouchListener);
        mFatherEditText.setOnTouchListener(mTouchListener);
        mCodeEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mDateEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);

        // kickoff the LoaderManager
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * Create a Discard change dialog
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Hook up the Back button
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mMemberHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Get all user data from editor and insert pet into database
     */
    private void savePet() {


        // Get all user data from editor
        String nameString = mNameEditText.getText().toString().trim();
        String fatherString = mFatherEditText.getText().toString().trim();
        String codeString = mCodeEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();
        String dateString = mDateEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();

        // Sanity Check
        if (
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(fatherString) || TextUtils.isEmpty(codeString)
                || TextUtils.isEmpty(phoneString) || TextUtils.isEmpty(dateString) || TextUtils.isEmpty(weightString)
                ) {
            finish();
        }

        // change
        int weight = 0;
        if(!weightString.isEmpty()) {
            weight = Integer.parseInt(weightString);
        }
        int code = 0;
        if(!codeString.isEmpty()) {
            code = Integer.parseInt(codeString);
        }

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MemberEntry.COLUMN_MEMBER_NAME, nameString);
        values.put(MemberEntry.COLUMN_MEMBER_FATHER_NAME, fatherString);
        values.put(MemberEntry.COLUMN_MEMBER_CODE, code);
        values.put(MemberEntry.COLUMN_MEMBER_PHONE, phoneString);
        values.put(MemberEntry.COLUMN_MEMBER_DATE, dateString);
        values.put(MemberEntry.COLUMN_MEMBER_WEIGHT, weight);

        if (mIsNewMember) {
            // Insert the new row, returning the primary key value of the new row
            Uri newRowUri = getContentResolver().insert(MemberEntry.CONTENT_URI, values);

            // Show Toast message for saved member
            Toast.makeText(this, R.string.save_message,Toast.LENGTH_LONG).show();


        } else {
            int mRowUpdated = getContentResolver().update(mCurrentMemberUri, values, null, null);

            if (mRowUpdated == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Show Toast message for updated member
                Toast.makeText(this, R.string.save_message,Toast.LENGTH_LONG).show();
            }

        }


    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // TODO: Implement this method
        if (mCurrentMemberUri != null) {
            int mRowUpdated = getContentResolver().delete(mCurrentMemberUri, null, null);

            if (mRowUpdated == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_delete_member_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Show Toast message for updated member
                Toast.makeText(this, R.string.editor_delete_member_successful,Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentMemberUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                savePet();
                // Exit Activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:

                showDeleteConfirmationDialog();
                // Exit Activity
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mMemberHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MemberEntry._ID,
                MemberEntry.COLUMN_MEMBER_NAME,
                MemberEntry.COLUMN_MEMBER_FATHER_NAME,
                MemberEntry.COLUMN_MEMBER_CODE,
                MemberEntry.COLUMN_MEMBER_PHONE,
                MemberEntry.COLUMN_MEMBER_DATE,
                MemberEntry.COLUMN_MEMBER_WEIGHT
        };

        return new CursorLoader(this, // context
                mCurrentMemberUri, // content provider Uri
                projection, // projection
                null, // selection
                null, // selection args
                null // sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        //  Proceed with moving to the first row of the mCursor and reading data from it
        // (This should be the only row in the mCursor)
        if (data.moveToFirst() ) {

            // Find the columns of member attributes that we're interested in
            int nameIndex = data.getColumnIndex(MemberEntry.COLUMN_MEMBER_NAME);
            int fatherIndex = data.getColumnIndex(MemberEntry.COLUMN_MEMBER_FATHER_NAME);
            int codeIndex = data.getColumnIndex(MemberEntry.COLUMN_MEMBER_CODE);
            int phoneIndex = data.getColumnIndex(MemberEntry.COLUMN_MEMBER_PHONE);
            int dateIndex = data.getColumnIndex(MemberEntry.COLUMN_MEMBER_DATE);
            int weightIndex = data.getColumnIndex(MemberEntry.COLUMN_MEMBER_WEIGHT);

            // Extract out the value from the Cursor for the given column index
            String mName = data.getString(nameIndex);
            String mFatherName = data.getString(fatherIndex);
            int mCode = data.getInt(codeIndex);
            String mPhone = data.getString(phoneIndex);
            String mDate = data.getString(dateIndex);
            int mWeight = data.getInt(weightIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(mName);
            mFatherEditText.setText(mFatherName);
            mCodeEditText.setText(mCode);
            mPhoneEditText.setText(mPhone);
            mDateEditText.setText(mDate);
            mWeightEditText.setText(mWeight);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Setting Text Field blank
        mNameEditText.setText("");
        mFatherEditText.setText("");
        mCodeEditText.setText("");
        mPhoneEditText.setText("");
        mDateEditText.setText("");
        mWeightEditText.setText("");

    }
}