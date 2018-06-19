package com.dimagi.test.external;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.commcare.commcaresupportlibrary.CaseUtils;

public class CaseContentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_page);
        showCaseData(null, null);
    }

    private void showCaseData(String selection, String[] selectionArgs) {
        ListView la = this.findViewById(R.id.list_view);
        Cursor c = CaseUtils.getCaseDataCursor(this, null, selection, selectionArgs);

        final SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, c, new String[]{"case_name", "case_id"}, new int[]{android.R.id.text1, android.R.id.text2}, 1);

        la.setOnItemLongClickListener((arg0, arg1, position, arg3) -> {
            Cursor cursor = cursorAdapter.getCursor();
            cursor.moveToPosition(position);

            String caseType = cursor.getString(cursor.getColumnIndex("case_type"));
            CaseContentActivity.this.showCaseData("case_type = ? AND" +
                    "\nstatus=?", new String[]{caseType, "open"});
            return true;
        });

        la.setAdapter(cursorAdapter);
        la.setOnItemClickListener((arg0, v, position, id) -> {
            Cursor cursor = cursorAdapter.getCursor();
            cursor.moveToPosition(position);

            String caseId = cursor.getString(cursor.getColumnIndex("case_id"));
            CaseContentActivity.this.moveToDataAtapter(caseId);
        });
    }

    private void moveToDataAtapter(String caseId) {
        Cursor cursor = CaseUtils.getCaseDataCursor(this, caseId);
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, cursor, new String[]{"value", "datum_id"}, new int[]{android.R.id.text1, android.R.id.text2}, 1);
        ListView la = this.findViewById(R.id.list_view);

        la.setAdapter(cursorAdapter);
        la.setOnItemClickListener(null);
    }
}
