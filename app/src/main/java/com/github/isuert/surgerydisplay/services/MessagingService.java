package com.github.isuert.surgerydisplay.services;

import android.content.Intent;
import android.util.Log;

import com.github.isuert.surgerydisplay.models.Test;
import com.github.isuert.surgerydisplay.models.Xray;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.parceler.Parcels;

public class MessagingService extends FirebaseMessagingService {

    public static final String ACTION = "action";
    private static final String TAG = "SurgeryFirebaseMS";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "message received");

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();

            Intent intent = new Intent();
            intent.setAction(ACTION);

            String type = remoteMessage.getData().get("type");
            intent.putExtra("type", type);

            if (type.equals("xray")) {
                Xray xray = gson.fromJson(remoteMessage.getData().get("xray"), Xray.class);
                intent.putExtra("xray", Parcels.wrap(xray));
            } else if (type.equals("test")) {
                Test test = gson.fromJson(remoteMessage.getData().get("test"), Test.class);
                intent.putExtra("test", Parcels.wrap(test));
            }

            sendBroadcast(intent);
        }
    }


}
