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
import android.widget.ImageView;

import com.github.isuert.surgerydisplay.models.Xray;
import com.github.isuert.surgerydisplay.services.MessagingService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;

public class XrayActivity extends AppCompatActivity {
    private static final String TAG = "XrayActivity";

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xray);
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

        if (type.equals("xray")) {
            final Xray xray = Parcels.unwrap(intent.getParcelableExtra("xray"));
            ImageView imageView = (ImageView) findViewById(R.id.imageView);

            setTitle(xray.getName()
                    + " ("
                    + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(xray.getDatetime())
                    + ")");

            Picasso picasso = Picasso.with(this);
            picasso.setIndicatorsEnabled(true);
            picasso.load(xray.getImage()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "current time: " + System.currentTimeMillis() + ">>" + xray.getId());
                }

                @Override
                public void onError() {

                }
            });
        } else {
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
