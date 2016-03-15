package com.dimagi.test.external;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class FixtureContentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_page);
        showFixtureData(null, null);
    }

    private void showFixtureData(String selection, String[] selectionArgs) {
        ListView la = (ListView)this.findViewById(R.id.list_view);
        Cursor c = this.managedQuery(Uri.parse("content://org.commcare.dalvik.fixture/fixturedb/"), null, selection, selectionArgs, null);

        final SimpleCursorAdapter sca = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, c, new String[]{"_id", "instance_id"}, new int[]{android.R.id.text1, android.R.id.text2});

        la.setAdapter(sca);
        la.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                Cursor cursor = sca.getCursor();
                cursor.moveToPosition(position);

                String fixtureId = cursor.getString(cursor.getColumnIndex("instance_id"));
                FixtureContentActivity.this.moveToDataAtapter(fixtureId);
            }
        });
    }

    private void moveToDataAtapter(String fixtureId) {
        Cursor c = this.managedQuery(Uri.parse("content://org.commcare.dalvik.fixture/fixturedb/" + fixtureId), null, null, null, null);

        SimpleCursorAdapter sca = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, c, new String[]{"instance_id", "content"}, new int[]{android.R.id.text1, android.R.id.text2});
        ListView la = (ListView)this.findViewById(R.id.list_view);

        la.setAdapter(sca);
        la.setOnItemClickListener(null);
    }
}
