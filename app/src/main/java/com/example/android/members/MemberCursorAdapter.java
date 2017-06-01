package com.example.android.members;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.members.data.MemberContract.MemberEntry;

/**
 * Created by Ashutosh on 28-05-2017.
 */
public class MemberCursorAdapter extends CursorAdapter {

    public MemberCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_MEMBER_NAME));
        String father = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_MEMBER_FATHER_NAME));
        // Populate fields with extracted properties
        nameTextView.setText(name);
        summaryTextView.setText(String.valueOf(father));
    }
}
