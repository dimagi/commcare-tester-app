package com.dimagi.test.external;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.simprints.libsimprints.Identification;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Respond to a 'fake' Simprints fingerprint identification request by loading
 * the case list, picking the top N cases, adding incrementing confidence score
 * and returning that list to CommCare
 *
 * @author Phillip Mates (pmates@dimagi.com)
 */
public class SimprintsIdentifyTest extends FragmentActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CASE_LOADER = 0;
    private static final int RESULT_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportLoaderManager().initLoader(CASE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        switch (loaderID) {
            case CASE_LOADER:
                Uri tableUri = Uri.parse("content://org.commcare.dalvik.case/casedb/case");
                return new CursorLoader(this, tableUri, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor caseData) {
        Intent indentificationIntent = new Intent();
        ArrayList<Identification> ids = new ArrayList<>();

        caseData.moveToFirst();
        int index = caseData.getColumnIndexOrThrow("case_id");

        float confidence = 0.0f;
        while (!caseData.isAfterLast() && (ids.size() < RESULT_COUNT)) {
            String caseId = caseData.getString(index);
            ids.add(new Identification(caseId, confidence));
            confidence += 0.2f;
            caseData.moveToNext();
        }
        caseData.close();
        Collections.sort(ids);
        indentificationIntent.putParcelableArrayListExtra("identification", ids);
        setResult(RESULT_OK, indentificationIntent);
        finish();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
