package com.mapfap.tap;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by mapfap on 6/16/2017 AD.
 */

public class SuccessActivity extends AppCompatActivity {

    private Intent thisIntent;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        String id = getIntent().getStringExtra("EMPLOYEE_ID");
        String name = getIntent().getStringExtra("EMPLOYEE_NAME");
        String department = getIntent().getStringExtra("EMPLOYEE_DEPARTMENT");
        thisIntent = getIntent();

        TextView appName = (TextView) findViewById(R.id.app_name2);
        TextView success = (TextView) findViewById(R.id.success);
        TextView idView = (TextView) findViewById(R.id.employee_code);
        TextView nameView = (TextView) findViewById(R.id.employee_name);
        TextView departmentView = (TextView) findViewById(R.id.employee_department);
        Button nextButton = (Button) findViewById(R.id.next_button);

        idView.setText(id);
        nameView.setText(name);
        departmentView.setText(department);

        Typeface kanitSemiBold = Typeface.createFromAsset(getAssets(), "Kanit-SemiBold.ttf");
        Typeface kanitMedium = Typeface.createFromAsset(getAssets(), "Kanit-Medium.ttf");

        appName.setTypeface(kanitMedium);
        success.setTypeface(kanitSemiBold);
        idView.setTypeface(kanitSemiBold);
        nameView.setTypeface(kanitSemiBold);
        departmentView.setTypeface(kanitSemiBold);
        nextButton.setTypeface(kanitSemiBold);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisIntent.putExtra("NFC", "-");
                setResult(RESULT_CANCELED, thisIntent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            thisIntent.putExtra("NFC", MainActivity.toHex(id));
            setResult(RESULT_OK, thisIntent);
            finish();
        }
    }
}
