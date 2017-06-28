package com.mapfap.tap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;

    private APICaller apiCaller;
    private boolean activeEventExists;

    private TextView eventName;

    FasterAnimationsContainer mFasterAnimationsContainer;
    private static final int[] IMAGE_RESOURCES = {R.drawable.scan_000000, R.drawable.scan_000001, R.drawable.scan_000002, R.drawable.scan_000003, R.drawable.scan_000004, R.drawable.scan_000005, R.drawable.scan_000006, R.drawable.scan_000007, R.drawable.scan_000008, R.drawable.scan_000009, R.drawable.scan_000010, R.drawable.scan_000011, R.drawable.scan_000012, R.drawable.scan_000013, R.drawable.scan_000014, R.drawable.scan_000015, R.drawable.scan_000016, R.drawable.scan_000017, R.drawable.scan_000018, R.drawable.scan_000019, R.drawable.scan_000020, R.drawable.scan_000021, R.drawable.scan_000022, R.drawable.scan_000023, R.drawable.scan_000024, R.drawable.scan_000025, R.drawable.scan_000026, R.drawable.scan_000027, R.drawable.scan_000028, R.drawable.scan_000029, R.drawable.scan_000030, R.drawable.scan_000031, R.drawable.scan_000032, R.drawable.scan_000033, R.drawable.scan_000034, R.drawable.scan_000035, R.drawable.scan_000036, R.drawable.scan_000037, R.drawable.scan_000038, R.drawable.scan_000039, R.drawable.scan_000040, R.drawable.scan_000041, R.drawable.scan_000042, R.drawable.scan_000043, R.drawable.scan_000044, R.drawable.scan_000045, R.drawable.scan_000046, R.drawable.scan_000047, R.drawable.scan_000048, R.drawable.scan_000049, R.drawable.scan_000050, R.drawable.scan_000051, R.drawable.scan_000052, R.drawable.scan_000053, R.drawable.scan_000054, R.drawable.scan_000055, R.drawable.scan_000056, R.drawable.scan_000057, R.drawable.scan_000058, R.drawable.scan_000059, R.drawable.scan_000060, R.drawable.scan_000061, R.drawable.scan_000062, R.drawable.scan_000063, R.drawable.scan_000064, R.drawable.scan_000065, R.drawable.scan_000066, R.drawable.scan_000067, R.drawable.scan_000068, R.drawable.scan_000069, R.drawable.scan_000070, R.drawable.scan_000071, R.drawable.scan_000072, R.drawable.scan_000073, R.drawable.scan_000074, R.drawable.scan_000075, R.drawable.scan_000076, R.drawable.scan_000077, R.drawable.scan_000078, R.drawable.scan_000079, R.drawable.scan_000080, R.drawable.scan_000081, R.drawable.scan_000082, R.drawable.scan_000083, R.drawable.scan_000084, R.drawable.scan_000085, R.drawable.scan_000086, R.drawable.scan_000087, R.drawable.scan_000088, R.drawable.scan_000089, R.drawable.scan_000090, R.drawable.scan_000091, R.drawable.scan_000092, R.drawable.scan_000093, R.drawable.scan_000094, R.drawable.scan_000095, R.drawable.scan_000096, R.drawable.scan_000097, R.drawable.scan_000098, R.drawable.scan_000099, R.drawable.scan_000100, R.drawable.scan_000101, R.drawable.scan_000102, R.drawable.scan_000103, R.drawable.scan_000104, R.drawable.scan_000105, R.drawable.scan_000106, R.drawable.scan_000107, R.drawable.scan_000108, R.drawable.scan_000109, R.drawable.scan_000110, R.drawable.scan_000111, R.drawable.scan_000112, R.drawable.scan_000113, R.drawable.scan_000114, R.drawable.scan_000115, R.drawable.scan_000116, R.drawable.scan_000117, R.drawable.scan_000118, R.drawable.scan_000119, R.drawable.scan_000120, R.drawable.scan_000121, R.drawable.scan_000122, R.drawable.scan_000123, R.drawable.scan_000124, R.drawable.scan_000125, R.drawable.scan_000126, R.drawable.scan_000127, R.drawable.scan_000128, R.drawable.scan_000129, R.drawable.scan_000130, R.drawable.scan_000131, R.drawable.scan_000132, R.drawable.scan_000133, R.drawable.scan_000134, R.drawable.scan_000135, R.drawable.scan_000136, R.drawable.scan_000137, R.drawable.scan_000138, R.drawable.scan_000139, R.drawable.scan_000140, R.drawable.scan_000141, R.drawable.scan_000142, R.drawable.scan_000143, R.drawable.scan_000144, R.drawable.scan_000145, R.drawable.scan_000146, R.drawable.scan_000147, R.drawable.scan_000148, R.drawable.scan_000149, R.drawable.scan_000150};

    private static final int ANIMATION_INTERVAL = 30; // ms
    private static final int SUCCESS_PAGE_REQUEST_CODE = 1;
    private static final int MANUAL_TAP_NUMPAD_REQUEST_CODE = 2;
    private static final int LINK_NFC_NUMPAD_REQUEST_CODE = 3;
    private Button manualButton;

    private static final int MAX_EVENT_NAME_LENGTH = 18;
    private TextView eventCheckMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        eventName = (TextView) findViewById(R.id.event_name);
        eventCheckMode = (TextView) findViewById(R.id.event_check_mode);
        ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        manualButton = (Button) findViewById(R.id.manual_button);

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Sending...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);

        apiCaller = new APICaller(this, progressDialog);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActiveEvent();
            }
        });
        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                respondManualTap();
            }
        });

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_nfc);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        getActiveEvent();

        initArtwork();
    }

    private void initArtwork() {
        ImageView img = (ImageView) findViewById(R.id.nfc_emit_view);
        mFasterAnimationsContainer = FasterAnimationsContainer.getInstance(img);
        mFasterAnimationsContainer.setContext(getApplicationContext());
        mFasterAnimationsContainer.addAllFrames(IMAGE_RESOURCES, ANIMATION_INTERVAL);

        Typeface kanitSemiBold = Typeface.createFromAsset(getAssets(), "Kanit-SemiBold.ttf");
        Typeface kanitMedium = Typeface.createFromAsset(getAssets(), "Kanit-Medium.ttf");
        Typeface kanitLight = Typeface.createFromAsset(getAssets(), "Kanit-Light.ttf");


        TextView appName = (TextView) findViewById(R.id.app_name);
        TextView pleaseTap = (TextView) findViewById(R.id.please_tap);

        appName.setTypeface(kanitMedium);
        eventName.setTypeface(kanitSemiBold);
        eventCheckMode.setTypeface(kanitSemiBold);
        pleaseTap.setTypeface(kanitLight);
        manualButton.setTypeface(kanitSemiBold);
    }

    private void respondManualTap() {
        Intent intent = new Intent(this, NumpadActivity.class);
        intent.putExtra("MESSAGE", getResources().getString(R.string.enter_employee));
        intent.putExtra("NUMBER", "");
        startActivityForResult(intent, MANUAL_TAP_NUMPAD_REQUEST_CODE);

    }

    private void respondManualEmployeeFound(APIResponse respond) {
        String sb = "ID: " +
                respond.employeeId +
                "\nName: " +
                respond.employeeName +
                "\nDepartment: " +
                respond.employeeDepartment;

        final String employeeId = respond.employeeId;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Tap");
        builder.setMessage("[Employee Matched]\n" + sb);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                apiCaller.sendManualTap(UserState.respondManualEmployeeFound, employeeId, false);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void respondManualEmployeeNotFound(final String employeeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.no_employee_found) + " " + employeeId);
        builder.setPositiveButton(R.string.create_new_employee, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                responseCreateNewEmployeeWithoutNfc(employeeId);
            }
        });

        builder.setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                respondManualTap();
            }
        });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void responseCreateNewEmployeeWithoutNfc(String employeeId) {
        apiCaller.sendManualTap(UserState.respondManualTapForceCreate, employeeId, true);
    }

    private void responseCreateNewEmployeeWithNfc(String nfcId, String employeeId) {
        apiCaller.sendCardTap(UserState.responseNewNfcTap, nfcId, true, employeeId);
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
        mFasterAnimationsContainer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
        mFasterAnimationsContainer.stop();
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
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            respondNewNfcTap(toHex(id));
        }
    }

    private void respondNewNfcTap(String nfcId) {
        if (!activeEventExists) {
            respondNoActiveEvent();
            return;
        }
        apiCaller.sendCardTap(UserState.responseNewNfcTap, nfcId, false, "");
    }

    private void respondError(APIResponse response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String errorMessage;
        if (response.errorDetails == null) {
            errorMessage = "Error";
        } else {
            errorMessage = response.errorDetails;
        }
        builder.setMessage(errorMessage);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void respondTapSuccess(APIResponse response) {
        Intent intent = new Intent(this, SuccessActivity.class);
        intent.putExtra("EMPLOYEE_ID", response.employeeId);
        intent.putExtra("EMPLOYEE_NAME", response.employeeName);
        intent.putExtra("EMPLOYEE_DEPARTMENT", response.employeeDepartment);
        intent.putExtra("DUPLICATE", response.isDuplicateTap);
        intent.putExtra("EVENT_STATUS", response.activeEventCheckMode);
        startActivityForResult(intent, SUCCESS_PAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SUCCESS_PAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                respondNewNfcTap(data.getStringExtra("NFC"));
            }
        } else if (requestCode == MANUAL_TAP_NUMPAD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String employeeId = data.getStringExtra("NUMBER");
                apiCaller.findEmployeeByEmployeeId(UserState.respondManualTapFirstEntry, employeeId, "NO_NFC_VALUE_FOR_THIS_SCENARIO");
            }
        } else if (requestCode == LINK_NFC_NUMPAD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String employeeId = data.getStringExtra("NUMBER");
                String nfcId = data.getStringExtra("NUMBER2");
                apiCaller.findEmployeeByEmployeeId(UserState.respondUnRegisteredNfc, employeeId, nfcId);
            }
        }
    }

    private void respondUnRegisteredNfc(final String nfcId) {
        Intent intent = new Intent(this, NumpadActivity.class);
        intent.putExtra("MESSAGE", "NFC#" + nfcId + R.string.un_registered_nfc);
        intent.putExtra("NUMBER", "");
        intent.putExtra("NUMBER2", nfcId);
        startActivityForResult(intent, LINK_NFC_NUMPAD_REQUEST_CODE);
    }

    private void respondEmployeeNotFoundAfterNfc(final String employeeId, final String nfcId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.no_employee_found) + " " + employeeId);
        builder.setPositiveButton(R.string.create_new_employee, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                responseCreateNewEmployeeWithNfc(nfcId, employeeId);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                respondManualTap();
            }
        });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void respondNewNfcEmployeeFound(APIResponse respond, final String nfcId) {
        String sb = "ID: " +
                respond.employeeId +
                "\nName: " +
                respond.employeeName +
                "\nDepartment: " +
                respond.employeeDepartment;

        final String employeeId = respond.employeeId;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirm Card Registration");
        builder.setMessage("Employee Matched: " + sb);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                apiCaller.registerEmployeeCard(UserState.respondNewNfcEmployeeFound, nfcId, employeeId);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                respondUnRegisteredNfc(nfcId);
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void respondNoActiveEvent() {
        eventName.setText(R.string.default_event);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_active_event);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b).toUpperCase());
        }

        return sb.toString();
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    public void getActiveEvent() {
        if (mFasterAnimationsContainer != null) {
            mFasterAnimationsContainer.stop();
        }
        apiCaller.getActiveEvent(UserState.getActiveEvent);
    }

    public void onAPIRespond(APIResponse response) {
        if (mFasterAnimationsContainer != null) {
            mFasterAnimationsContainer.start();
        }
        if (response.isError) {
            respondError(response);
        } else {
            switch (response.userState) {
                case respondManualEmployeeFound:
                    onRespondManualEmployeeFound(response);
                    break;
                case responseNewNfcTap:
                    onResponseNewNfcTap(response);
                    break;
                case respondUnRegisteredNfc:
                    onRespondUnRegisteredNfc(response);
                    break;
                case getActiveEvent:
                    onGetActiveEvent(response);
                    break;
                case respondNewNfcEmployeeFound:
                    onRespondNewNfcEmplyoeeFound(response);
                    break;
                case respondManualTapFirstEntry:
                    onRespondManualTapFirstEntry(response);
                    break;
                case respondManualTapForceCreate:
                    onRespondManualTapForceCreate(response);
                    break;
            }
        }
    }

    private void onRespondNewNfcEmplyoeeFound(APIResponse response) {
        if (response.isNfcRegistered) {
            respondTapSuccess(response);
        }
    }

    private void onGetActiveEvent(APIResponse response) {
        activeEventExists = response.activeEventExists;

        if (activeEventExists) {

            String shortenedEventName = response.activeEvent;
            if (shortenedEventName.length() > MAX_EVENT_NAME_LENGTH) {
                shortenedEventName = shortenedEventName.substring(0, MAX_EVENT_NAME_LENGTH - 2) + "..";
            }
            eventName.setText(shortenedEventName);

            eventCheckMode.setText(response.activeEventCheckMode);
        } else {
            respondNoActiveEvent();
        }
    }

    private void onResponseNewNfcTap(APIResponse response) {
        if (response.isNfcRegistered) {
            respondTapSuccess(response);
        } else {
            respondUnRegisteredNfc(response.requestingNfcId);
        }
    }

    // Click "Register" -> Result from Numpad Page -> Search for employee
    private void onRespondManualTapFirstEntry(APIResponse response) {
        if (response.isEmployeeFound) {
            respondManualEmployeeFound(response);
        } else {
            respondManualEmployeeNotFound(response.requestingEmployeeId);
        }
    }

    // Click "Register" -> Result from Numpad Page -> Search for employee *NOT FOUND* -> Click "Create Employee"
    private void onRespondManualTapForceCreate(APIResponse response) {
        if (response.isEmployeeFound) {
            respondTapSuccess(response);
        }
    }

    private void onRespondManualEmployeeFound(APIResponse response) {
        if (response.isEmployeeFound) {
            respondTapSuccess(response);
        }
    }

    private void onRespondUnRegisteredNfc(APIResponse response) {
        if (response.isEmployeeFound) {
            respondNewNfcEmployeeFound(response, response.requestingNfcId);
        } else {
            respondEmployeeNotFoundAfterNfc(response.requestingEmployeeId, response.requestingNfcId);
        }
    }

}
