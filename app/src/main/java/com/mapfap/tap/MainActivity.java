package com.mapfap.tap;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;

    private APICaller apiCaller;
    private boolean activeEventExists;

    private TextView eventName;

    FasterAnimationsContainer mFasterAnimationsContainer;
    private static final int[] IMAGE_RESOURCES = {R.drawable.scan_000000, R.drawable.scan_000001, R.drawable.scan_000002, R.drawable.scan_000003, R.drawable.scan_000004, R.drawable.scan_000005, R.drawable.scan_000006, R.drawable.scan_000007, R.drawable.scan_000008, R.drawable.scan_000009, R.drawable.scan_000010, R.drawable.scan_000011, R.drawable.scan_000012, R.drawable.scan_000013, R.drawable.scan_000014, R.drawable.scan_000015, R.drawable.scan_000016, R.drawable.scan_000017, R.drawable.scan_000018, R.drawable.scan_000019, R.drawable.scan_000020, R.drawable.scan_000021, R.drawable.scan_000022, R.drawable.scan_000023, R.drawable.scan_000024, R.drawable.scan_000025, R.drawable.scan_000026, R.drawable.scan_000027, R.drawable.scan_000028, R.drawable.scan_000029, R.drawable.scan_000030, R.drawable.scan_000031, R.drawable.scan_000032, R.drawable.scan_000033, R.drawable.scan_000034, R.drawable.scan_000035, R.drawable.scan_000036, R.drawable.scan_000037, R.drawable.scan_000038, R.drawable.scan_000039, R.drawable.scan_000040, R.drawable.scan_000041, R.drawable.scan_000042, R.drawable.scan_000043, R.drawable.scan_000044, R.drawable.scan_000045, R.drawable.scan_000046, R.drawable.scan_000047, R.drawable.scan_000048, R.drawable.scan_000049, R.drawable.scan_000050, R.drawable.scan_000051, R.drawable.scan_000052, R.drawable.scan_000053, R.drawable.scan_000054, R.drawable.scan_000055, R.drawable.scan_000056, R.drawable.scan_000057, R.drawable.scan_000058, R.drawable.scan_000059, R.drawable.scan_000060, R.drawable.scan_000061, R.drawable.scan_000062, R.drawable.scan_000063, R.drawable.scan_000064, R.drawable.scan_000065, R.drawable.scan_000066, R.drawable.scan_000067, R.drawable.scan_000068, R.drawable.scan_000069, R.drawable.scan_000070, R.drawable.scan_000071, R.drawable.scan_000072, R.drawable.scan_000073, R.drawable.scan_000074, R.drawable.scan_000075, R.drawable.scan_000076, R.drawable.scan_000077, R.drawable.scan_000078, R.drawable.scan_000079, R.drawable.scan_000080, R.drawable.scan_000081, R.drawable.scan_000082, R.drawable.scan_000083, R.drawable.scan_000084, R.drawable.scan_000085, R.drawable.scan_000086, R.drawable.scan_000087, R.drawable.scan_000088, R.drawable.scan_000089, R.drawable.scan_000090, R.drawable.scan_000091, R.drawable.scan_000092, R.drawable.scan_000093, R.drawable.scan_000094, R.drawable.scan_000095, R.drawable.scan_000096, R.drawable.scan_000097, R.drawable.scan_000098, R.drawable.scan_000099, R.drawable.scan_000100, R.drawable.scan_000101, R.drawable.scan_000102, R.drawable.scan_000103, R.drawable.scan_000104, R.drawable.scan_000105, R.drawable.scan_000106, R.drawable.scan_000107, R.drawable.scan_000108, R.drawable.scan_000109, R.drawable.scan_000110, R.drawable.scan_000111, R.drawable.scan_000112, R.drawable.scan_000113, R.drawable.scan_000114, R.drawable.scan_000115, R.drawable.scan_000116, R.drawable.scan_000117, R.drawable.scan_000118, R.drawable.scan_000119, R.drawable.scan_000120, R.drawable.scan_000121, R.drawable.scan_000122, R.drawable.scan_000123, R.drawable.scan_000124, R.drawable.scan_000125, R.drawable.scan_000126, R.drawable.scan_000127, R.drawable.scan_000128, R.drawable.scan_000129, R.drawable.scan_000130, R.drawable.scan_000131, R.drawable.scan_000132, R.drawable.scan_000133, R.drawable.scan_000134, R.drawable.scan_000135, R.drawable.scan_000136, R.drawable.scan_000137, R.drawable.scan_000138, R.drawable.scan_000139, R.drawable.scan_000140, R.drawable.scan_000141, R.drawable.scan_000142, R.drawable.scan_000143, R.drawable.scan_000144, R.drawable.scan_000145, R.drawable.scan_000146, R.drawable.scan_000147, R.drawable.scan_000148, R.drawable.scan_000149, R.drawable.scan_000150};

    private static final int ANIMATION_INTERVAL = 30; // ms
    private static final int SUCCESS_PAGE_REQUEST_CODE = 1; // ms
    private Button manualButton;
    private ProgressDialog progressDialog;
    private String currentEmployeeID;
    private String currentNfcId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        eventName = (TextView) findViewById(R.id.event_name);
        ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        manualButton = (Button) findViewById(R.id.manual_button);

        progressDialog = new ProgressDialog(MainActivity.this);
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
            builder.create().show();
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
        pleaseTap.setTypeface(kanitLight);
        manualButton.setTypeface(kanitSemiBold);
    }

    private void respondManualTap() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.enter_employee);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(et);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String employeeId = et.getText().toString();
                currentEmployeeID = employeeId;
                apiCaller.findEmployeeByEmployeeId(UserState.respondManualTap, currentEmployeeID);
            }
        });

        builder.create().show();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);

    }

    private void respondManualEmployeeFound(APIResponse respond) {
        StringBuilder sb = new StringBuilder();
        sb.append(respond.employeeId);
        sb.append(" ");
        sb.append(respond.employeeName);
        sb.append(" ");
        sb.append(respond.employeeDepartment);
        sb.append(" ");

        String employeeId = respond.employeeId;
        currentEmployeeID = employeeId;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirm Tap");
        builder.setMessage("Employee Matched: " + sb.toString());

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                apiCaller.sendManualTap(UserState.respondManualEmployeeFound, currentEmployeeID, false);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
        builder.create().show();
    }

    private void respondManualEmployeeNotFound(String employeeId) {
        currentEmployeeID = employeeId;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.no_employee_found) + currentEmployeeID);
        builder.setPositiveButton(R.string.create_new_employee, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                responseCreateNewEmployeeWithoutNfc(currentEmployeeID);
            }
        });

        builder.setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                respondManualTap();
            }
        });

        builder.create().show();
    }

    private void responseCreateNewEmployeeWithoutNfc(String employeeId) {
        currentEmployeeID = employeeId;
        apiCaller.sendManualTap(UserState.respondManualEmployeeFound, currentEmployeeID, true);
    }

    private void responseCreateNewEmployeeWithNfc(String nfcId, String employeeId) {
        currentNfcId = nfcId;
        currentEmployeeID = employeeId;
        apiCaller.sendCardTap(UserState.responseNewNfcTap ,currentNfcId, true);
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
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
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
            respondNewNfcTap(toHex(id));

            // TODO: Remove debugging toast.
            Toast.makeText(getApplicationContext(), toHex(id), Toast.LENGTH_SHORT);
        }
    }

    private void respondNewNfcTap(String nfcId) {
        if (!activeEventExists) {
            respondNoActiveEvent();
            return;
        }
        currentNfcId = nfcId;
        apiCaller.sendCardTap(UserState.responseNewNfcTap ,nfcId, false);
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
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
            }
        });
        builder.create().show();
    }

    private void respondTapSuccess(APIResponse response) {
        Intent intent = new Intent(this, SuccessActivity.class);
        intent.putExtra("EMPLOYEE_ID", response.employeeId);
        intent.putExtra("EMPLOYEE_NAME", response.employeeName);
        intent.putExtra("EMPLOYEE_DEPARTMENT", response.employeeDepartment);
        startActivityForResult(intent, SUCCESS_PAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SUCCESS_PAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                respondNewNfcTap(data.getStringExtra("NFC"));
            }
        }
    }

    private void respondUnRegisteredNfc(final String nfcId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.un_registered_nfc);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(et);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String employeeId = et.getText().toString();
                currentEmployeeID = employeeId;
                currentNfcId = nfcId;
                apiCaller.findEmployeeByEmployeeId(UserState.respondUnRegisteredNfc, employeeId);
            }
        });

        builder.create().show();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);
    }

    private void respondEmployeeNotFoundAfterNfc(String employeeId, String nfcId) {

        currentNfcId = nfcId;
        currentEmployeeID = employeeId;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.no_employee_found) + employeeId);
        builder.setPositiveButton(R.string.create_new_employee, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                responseCreateNewEmployeeWithNfc(currentNfcId, currentNfcId);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                respondManualTap();
            }
        });

        builder.create().show();
    }

    private void respondNewNfcEmployeeFound(APIResponse respond, String nfcId) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ");
        sb.append(respond.employeeId);
        sb.append("\nName: ");
        sb.append(respond.employeeName);
        sb.append("\nDepartment: ");
        sb.append(respond.employeeDepartment);

        currentNfcId = nfcId;
        currentEmployeeID = respond.employeeId;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirm Card Registration");
        builder.setMessage("Employee Matched: " + sb.toString());

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                apiCaller.registerEmployeeCard(UserState.respondNewNfcEmployeeFound, currentNfcId, currentEmployeeID);
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
        eventName.setText("Event: N/A");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_active_event);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();
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
                case respondManualTap:
                    onRespondManualTap(response);
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
            eventName.setText(response.activeEvent);
        } else {
            respondNoActiveEvent();
        }
    }

    private void onResponseNewNfcTap(APIResponse response) {
        if (response.isNfcRegistered) {
            respondTapSuccess(response);
        } else {
            respondUnRegisteredNfc(currentNfcId);
        }
    }

    private void onRespondManualTap(APIResponse response) {
        if (response.isEmployeeFound) {
            respondManualEmployeeFound(response);
        } else {
            respondManualEmployeeNotFound(currentEmployeeID);
        }
    }

    private void onRespondManualEmployeeFound(APIResponse response) {
        if (response.isEmployeeFound) {
            respondTapSuccess(response);
        }
    }

    private void onRespondUnRegisteredNfc(APIResponse response) {
        if (response.isEmployeeFound) {
            respondNewNfcEmployeeFound(response, currentNfcId);
        } else {
            respondEmployeeNotFoundAfterNfc(currentEmployeeID, currentNfcId);
        }
    }

}
