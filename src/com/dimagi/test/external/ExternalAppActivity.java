package com.dimagi.test.external;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.commcare.commcaresupportlibrary.CaseUtils;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class ExternalAppActivity extends Activity {

    private Button login;
    private Button sync;

    private byte[] publicKey;
    private String keyId;

    private static final int KEY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button startCommCareButton = this.findViewById(R.id.btn_start_cc);
        CaseUtils.setPackageExtension(this, null);
        startCommCareButton.setOnClickListener(v -> {
            Intent i = new Intent("org.commcare.dalvik.action.CommCareSession");
            String sssd = "";
            sssd +=
                    "COMMAND_ID" + " " + "m0" + " " +
                            "COMMAND_ID" + " " + "m0-f0";

            i.putExtra("ccodk_session_request", sssd);
            ExternalAppActivity.this.startActivity(i);
        });

        Button acquireKeyButton = this.findViewById(R.id.acquire_key);
        acquireKeyButton.setOnClickListener(v -> {
            Intent i = new Intent("org.commcare.dalvik.action.CommCareKeyAccessRequest");
            ExternalAppActivity.this.startActivityForResult(i, KEY_REQUEST_CODE);
        });

        login = this.findViewById(R.id.log_in_local);
        login.setOnClickListener(v -> {
            Intent i = new Intent("org.commcare.dalvik.api.action.ExternalAction");
            i.putExtra("commcare_sharing_key_id", keyId);
            Bundle action = new Bundle();
            action.putString("commcareaction", "login");
            action.putString("username", "test");
            action.putString("password", "234");
            Pair<byte[], byte[]> serializedBundle = serializeBundle(action);

            i.putExtra("commcare_sharing_key_symetric", serializedBundle.first);
            i.putExtra("commcare_sharing_key_callout", serializedBundle.second);

            ExternalAppActivity.this.sendBroadcast(i);
        });

        sync = this.findViewById(R.id.sync);
        sync.setOnClickListener(v -> {
            Intent i = new Intent("org.commcare.dalvik.api.action.ExternalAction");
            i.putExtra("commcare_sharing_key_id", keyId);
            i.setComponent(new ComponentName("org.commcare.dalvik", "org.commcare.provider.ExternalApiReceiver"));
            Bundle action = new Bundle();
            action.putString("commcareaction", "sync");
            Pair<byte[], byte[]> serializedBundle = serializeBundle(action);

            i.putExtra("commcare_sharing_key_symetric", serializedBundle.first);
            i.putExtra("commcare_sharing_key_callout", serializedBundle.second);

            ExternalAppActivity.this.sendBroadcast(i);
        });

        Button media = this.findViewById(R.id.button_media);
        media.setOnClickListener(v -> {
            Intent i = new Intent(ExternalAppActivity.this, CaseMediaActivity.class);
            ExternalAppActivity.this.startActivity(i);
        });

        Button getCaseDataButton = this.findViewById(R.id.button_case_data);
        getCaseDataButton.setOnClickListener(v -> {
            Intent i = new Intent(ExternalAppActivity.this, CaseContentActivity.class);
            ExternalAppActivity.this.startActivity(i);
        });

        Button getFixtureDataButton = this.findViewById(R.id.button_fixture_data);
        getFixtureDataButton.setOnClickListener(v -> {
            Intent i = new Intent(ExternalAppActivity.this, FixtureContentActivity.class);
            ExternalAppActivity.this.startActivity(i);
        });


        Button receiver = this.findViewById(R.id.button_receiver);
        receiver.setOnClickListener(v -> {
            Intent i = new Intent(ExternalAppActivity.this, IntentReceiverTest.class);
            ExternalAppActivity.this.startActivity(i);
        });

        Permissions.acquireAllAppPermissions(this, Permissions.ALL_PERMISSIONS_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                keyId = data.getStringExtra("commcare_sharing_key_id");
                publicKey = data.getByteArrayExtra("commcare_sharing_key_payload");
            } else {
                Toast.makeText(this, "Key Request Denied!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (keyId != null) {
            sync.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
        } else {
            sync.setVisibility(View.INVISIBLE);
            login.setVisibility(View.INVISIBLE);
        }
    }

    private Pair<byte[], byte[]> serializeBundle(Bundle b) {
        Parcel p = Parcel.obtain();
        p.setDataPosition(0);
        p.writeBundle(b);
        Pair<byte[], byte[]> keyAndEncryptedInput = encrypt(p.marshall());
        p.recycle();
        return keyAndEncryptedInput;
    }

    private Pair<byte[], byte[]> encrypt(byte[] input) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256, new SecureRandom());
            SecretKey aesKey = generator.generateKey();

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            KeySpec ks = new X509EncodedKeySpec(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey)keyFactory.generatePublic(ks);

            Cipher keyCipher = Cipher.getInstance("RSA");
            keyCipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encryptedAesKey = keyCipher.doFinal(aesKey.getEncoded());

            Cipher dataCipher = Cipher.getInstance("AES");
            dataCipher.init(Cipher.ENCRYPT_MODE, aesKey);

            return new Pair<>(encryptedAesKey, dataCipher.doFinal(input));
        } catch (GeneralSecurityException gse) {
            gse.printStackTrace();
            Toast.makeText(this, "Problem with keys! Check log", Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
