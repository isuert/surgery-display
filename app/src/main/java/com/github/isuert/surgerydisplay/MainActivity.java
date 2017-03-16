package com.github.isuert.surgerydisplay;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.isuert.surgerydisplay.models.DisplayConfig;
import com.github.isuert.surgerydisplay.services.MessagingService;
import com.github.isuert.surgerydisplay.webapi.WebApi;
import com.github.isuert.surgerydisplay.webapi.WebApiCreator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int AVAILABILITY_REQUEST = 0;
    private static final int ACTIVITY_REQUEST = 1;

    private String deviceId;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private ProgressBar progressBar;
    private TextView displayNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        intentFilter = new IntentFilter(MessagingService.ACTION);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                messageHandler(intent);
            }
        };
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        displayNameTextView = (TextView) findViewById(R.id.displayNameTextView);

        showDeviceId();
        checkPlayServicesAndContinue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AVAILABILITY_REQUEST && resultCode == RESULT_OK) {
            checkTokenAndContinue();
        }

        if (requestCode == ACTIVITY_REQUEST && resultCode == RESULT_OK) {
            messageHandler(data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showDeviceId() {
        TextView deviceIdTextView = (TextView) findViewById(R.id.displayIdTextView);
        deviceIdTextView.setText(getString(R.string.text_display_id, deviceId));
    }

    private void checkPlayServicesAndContinue() {
        GoogleApiAvailability avail = GoogleApiAvailability.getInstance();
        int resultCode = avail.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            checkTokenAndContinue();
        } else {
            if (avail.isUserResolvableError(resultCode)) {
                Dialog dialog = avail.getErrorDialog(this, resultCode, AVAILABILITY_REQUEST);
                dialog.show();
            } else {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.alert_play_services_title)
                        .setMessage(R.string.alert_play_services_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.alert_exit_app, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create();
                dialog.show();
            }
        }
    }

    private void checkTokenAndContinue() {
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            getDisplayConfig();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.alert_token_title)
                    .setMessage(R.string.alert_token_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkTokenAndContinue();
                        }
                    })
                    .setNegativeButton(R.string.alert_exit_app, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    private void getDisplayConfig() {
        WebApi webApi = WebApiCreator.create();

        progressBar.setVisibility(View.VISIBLE);
        webApi.getDisplayConfig(deviceId).enqueue(new Callback<DisplayConfig>() {
            @Override
            public void onResponse(Call<DisplayConfig> call, Response<DisplayConfig> response) {
                DisplayConfig config = response.body();
                displayNameTextView.setText(config.getName());
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<DisplayConfig> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.alert_config_title)
                        .setMessage(R.string.alert_config_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.alert_retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getDisplayConfig();
                            }
                        })
                        .setNegativeButton(R.string.alert_exit_app, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }

    private void messageHandler(Intent intent) {
        String type = intent.getStringExtra("type");

        if (type.equals("xray")) {
            intent.setClass(this, XrayActivity.class);
            startActivityForResult(intent, ACTIVITY_REQUEST);
        } else if (type.equals("test")) {
            intent.setClass(this, TestActivity.class);
            startActivityForResult(intent, ACTIVITY_REQUEST);
        }
    }
}
