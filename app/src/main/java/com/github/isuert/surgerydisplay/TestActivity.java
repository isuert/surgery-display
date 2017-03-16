package com.github.isuert.surgerydisplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.isuert.surgerydisplay.models.Test;
import com.github.isuert.surgerydisplay.models.TestResult;
import com.github.isuert.surgerydisplay.services.MessagingService;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        intentFilter = new IntentFilter(MessagingService.ACTION);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                messageHandler(intent);
            }
        };

        messageHandler(getIntent());
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

    private void messageHandler(Intent intent) {
        String type = intent.getStringExtra("type");

        if (type.equals("test")) {
            Test test = Parcels.unwrap(intent.getParcelableExtra("test"));
            TextView textView = (TextView) findViewById(R.id.textView);
            List<TestResult> results = test.getResults();
            String text = "";
            for (TestResult result : results) {
                text += result.getName() + ": "
                        + result.getValue() + " "
                        + result.getUnit() + "\n";
            }

            setTitle(test.getType()
                    + " ("
                    + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(test.getDatetime())
                    + ")");
            textView.setText(text);

            Log.d(TAG, "current time: " + System.currentTimeMillis());
        } else {
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
