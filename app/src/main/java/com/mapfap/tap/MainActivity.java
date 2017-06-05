package com.mapfap.tap;


import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;

    private AlertDialog mDialog;
    private APICaller apiCaller;
    private boolean activeEventExists;

    private TextView mainText;
    private TextView eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resolveIntent(getIntent());

        mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            showMessage(R.string.error, R.string.no_nfc);
            finish();
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        eventName = (TextView) findViewById(R.id.event_name);
        mainText = (TextView) findViewById(R.id.main_text);
        Button refreshButton = (Button) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActiveEvent();
            }
        });

        apiCaller = new APICaller();

        getActiveEvent();

    }

    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
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

    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.nfc_disabled);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            respondNewTap(toHex(id));
        }
    }

    private void respondNewTap(String nfcId) {
        if (!activeEventExists) {
            respondNoActiveEvent();
            return;
        }

        APIResponse response = apiCaller.sendCardTap(nfcId);
        if (response.isError) {
            respondError(response);
        } else if (response.isNfcRegistered) {
            respondTapSuccess(response);
        } else {
            respondUnRegisteredNfc(nfcId);
        }
    }

    private void respondError(APIResponse response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(response.errorDetails);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    private void respondTapSuccess(APIResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append(response.employeeId);
        sb.append(" ");
        sb.append(response.employeeName);
        sb.append(" ");
        sb.append(response.employeeDepartment);
        sb.append(" ");
        sb.append(response.employeeIsPreRegistered);
        sb.append(" ");
        mainText.setText(sb);
    }

    private void respondUnRegisteredNfc(final String nfcId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.enter_employee);

        final EditText et = new EditText(this);

        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.requestFocus();
        builder.setView(et);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                APIResponse response = apiCaller.findEmployee(et.getText().toString());
                if (response.isError) {
                    respondError(response);
                } else if (response.isEmployeeFound) {
                    respondEmployeeFound(response, nfcId);
                } else {
                    respondUnRegisteredNfc(nfcId);
                }
            }
        });


        builder.create().show();
    }

    private void respondEmployeeFound(APIResponse respond, String nfcId) {
        StringBuilder sb = new StringBuilder();
        sb.append(respond.employeeId);
        sb.append(" ");
        sb.append(respond.employeeName);
        sb.append(" ");
        sb.append(respond.employeeDepartment);
        sb.append(" ");

        final String currentNfcId = nfcId;
        final String employeeId = respond.employeeId;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirm Card Registration");
        builder.setMessage("Employee Matched: " + sb.toString());

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                APIResponse registrationResponse = apiCaller.registerEmployeeCard(currentNfcId, employeeId);
                if (registrationResponse.isError) {
                    respondError(registrationResponse);
                } else if (registrationResponse.isNfcRegistered) {
                    respondTapSuccess(registrationResponse);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                respondUnRegisteredNfc(currentNfcId);
            }
        });
        builder.create().show();
    }

    private void respondNoActiveEvent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_active_event);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();
    }

    private void respondUnknownNfc() {
        mainText.setText("UNKNOWN NFC FORMAT");
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    public void getActiveEvent() {
        APIResponse response = apiCaller.getActiveEvent();
        if (response.isError) {
            respondError(response);
        } else {
            activeEventExists = response.activeEventExists;

            if (activeEventExists) {
                eventName.setText(response.activeEvent);
            } else {
                respondNoActiveEvent();
            }
        }
    }
}
