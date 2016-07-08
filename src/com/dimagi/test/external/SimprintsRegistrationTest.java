package com.dimagi.test.external;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.simprints.libsimprints.Constants;
import com.simprints.libsimprints.Registration;

import java.util.UUID;

/**
 * Respond to a 'fake' Simprints fingerprint identification request by loading
 * the case list, picking the top N cases, adding incrementing confidence score
 * and returning that list to CommCare
 *
 * @author Phillip Mates (pmates@dimagi.com)
 */
public class SimprintsRegistrationTest extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent calloutIntent = getIntent();

        String guid = calloutIntent.getStringExtra("guid");
        int fingerCount = 2;
        if (calloutIntent.hasExtra("count")) {
            fingerCount = Integer.valueOf(calloutIntent.getStringExtra("count"));
        }
        byte[] rightIndex, rightThumb, leftIndex, leftThumb;
        rightThumb = null;
        leftThumb = null;

        switch (fingerCount) {
            case 4:
                rightThumb = UUID.randomUUID().toString().getBytes();
                leftThumb = UUID.randomUUID().toString().getBytes();
                // fall through
            case 2:
                rightIndex = UUID.randomUUID().toString().getBytes();
                leftIndex = UUID.randomUUID().toString().getBytes();
                Registration registration = new Registration(guid, rightIndex, rightThumb, leftIndex, leftThumb);
                Intent registrationIntent = new Intent();
                registrationIntent.putExtra(Constants.SIMPRINTS_REGISTRATION, registration);
                setResult(RESULT_OK, registrationIntent);
                break;
            default:
                setResult(RESULT_CANCELED);
        }
        finish();
    }
}
