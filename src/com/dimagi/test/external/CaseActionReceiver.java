package com.dimagi.test.external;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Exemplifies how to receive case create/update/select broadcasts
 *
 * @author Phillip Mates (pmates@dimagi.com)
 */
public class CaseActionReceiver extends BroadcastReceiver {
    private static final String TAG = CaseActionReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.endsWith("update")) {
            ArrayList<String> caseIds = intent.getStringArrayListExtra("cases");
            if (caseIds != null) {
                for (String caseId : caseIds) {
                    Log.d(TAG, caseId + " was updated or created");
                }
            }
        } else if (action.endsWith("selected")) {
            String caseId = intent.getStringExtra("case_id");
            Log.d(TAG, caseId + " was selected");
        }
    }
}
