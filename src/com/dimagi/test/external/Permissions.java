package com.dimagi.test.external;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Acquire Android permissions needed by the app.
 */
public class Permissions {

    public final static int ALL_PERMISSIONS_REQUEST = 1;

    public static final String ORG_COMMCARE_DALVIK_PROVIDER_CASES_READ_PERMISSION = "org.commcare.dalvik.provider.cases.read";
    public static final String ORG_COMMCARE_DALVIK_DEBUG_PROVIDER_CASES_READ_PERMISSION = "org.commcare.dalvik.debug.provider.cases.read";

    public static boolean acquireAllAppPermissions(Activity activity, int permRequestCode) {
        String[] permissions = getAppPermissions();

        if (missingAppPermission(activity, permissions)) {
            requestNeededPermissions(activity, permRequestCode);
            return true;
        } else {
            return false;
        }
    }

    private static boolean missingAppPermission(Activity activity,
                                                String[] permissions) {
        for (String perm : permissions) {
            if (missingAppPermission(activity, perm)) {
                return true;
            }
        }
        return false;
    }

    public static boolean missingAppPermission(Activity activity,
                                               String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED;
    }

    public static String[] getAppPermissions() {
        return new String[]{
                ORG_COMMCARE_DALVIK_PROVIDER_CASES_READ_PERMISSION,
                ORG_COMMCARE_DALVIK_DEBUG_PROVIDER_CASES_READ_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void requestNeededPermissions(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, getAppPermissions(), requestCode);
    }
}
