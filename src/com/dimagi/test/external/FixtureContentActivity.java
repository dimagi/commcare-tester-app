package com.dimagi.test.external;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.commcare.commcaresupportlibrary.FixtureUtils;

/**
 * Tests whether the [Fixture Context Provider APIs]
 * (https://github.com/dimagi/commcare-android/wiki/Accessing-Fixture-Data)
 * are working
 *
 * Renders a list of all fixtures; clicking a fixture shows its raw XML data
 *
 */
public class FixtureContentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_page);
        showFixtureData();
    }

    private void showFixtureData() {
        ListView listView = this.findViewById(R.id.list_view);
        final Cursor cursor = FixtureUtils.getFixtureList(this);

        final SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                this, android.R.layout.two_line_list_item, cursor,
                new String[]{"_id", "instance_id"},
                new int[]{android.R.id.text1, android.R.id.text2}, 1);

        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener((arg0, v, position, id) -> {
            cursor.moveToPosition(position);
            String fixtureId = cursor.getString(cursor.getColumnIndex("instance_id"));
            FixtureContentActivity.this.moveToDataAtapter(fixtureId);
        });
    }

    private void moveToDataAtapter(String fixtureId) {
        Cursor cursor = FixtureUtils.getFixtureData(this, fixtureId);
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                this, android.R.layout.two_line_list_item, cursor,
                new String[]{"instance_id", "content"},
                new int[]{android.R.id.text1, android.R.id.text2}, 1);
        ListView la = this.findViewById(R.id.list_view);

        la.setAdapter(cursorAdapter);
        la.setOnItemClickListener(null);
    }
}
