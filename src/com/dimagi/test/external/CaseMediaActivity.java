package com.dimagi.test.external;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.commcare.commcaresupportlibrary.CaseUtils;

/**
 * Test class used for testing the multimedia content provider of CommCareODK
 */
public class CaseMediaActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_page);
        showCaseData(null, null);
    }

    /**
     * Queries CommCare ODK and displays a list of the currently loaded cases
     */
    private void showCaseData(String selection, String[] selectionArgs) {
        ListView la = this.findViewById(R.id.list_view);
        final Cursor cursor = CaseUtils.getCaseMetaData(this, selection, selectionArgs);

        final SimpleCursorAdapter sca = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, cursor, new String[]{"case_name", "case_id"}, new int[]{android.R.id.text1, android.R.id.text2}, 1);
        la.setOnItemLongClickListener((arg0, arg1, position, arg3) -> {
            cursor.moveToPosition(position);

            String caseType = cursor.getString(cursor.getColumnIndex("case_type"));
            CaseMediaActivity.this.showCaseData("case_type = ? AND\nstatus=?", new String[]{caseType, "open"});
            return true;
        });

        la.setAdapter(sca);
        la.setOnItemClickListener((arg0, v, position, id) -> {
            cursor.moveToPosition(position);
            String caseId = cursor.getString(cursor.getColumnIndex("case_id"));
            CaseMediaActivity.this.moveToMediaAdapter(caseId);
        });
    }

    /**
     * Queries CommCareODK for the multimedia associate with this case and displays
     */
    private void moveToMediaAdapter(String caseId) {
        Cursor cursor = CaseUtils.getCaseAttachmentData(this, caseId);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String filePath = cursor.getString(cursor.getColumnIndex("file-source"));

            if (!"invalid".equals(filePath)) {
                ImageView imageView = this.findViewById(R.id.image_view);

                Bitmap mBmp = BitmapFactory.decodeFile(filePath);
                if (mBmp != null) {
                    imageView.setImageBitmap(mBmp);
                }
            }

            cursor.moveToNext();
        }
    }
}
