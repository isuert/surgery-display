package com.github.isuert.surgerydisplay.services;

import android.provider.Settings;
import android.util.Log;

import com.github.isuert.surgerydisplay.webapi.WebApi;
import com.github.isuert.surgerydisplay.webapi.WebApiCreator;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "SurgeryFirebaseIIS";

    @Override
    public void onTokenRefresh() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "token refreshed");
        registerToken(deviceId, refreshedToken);
    }

    private void registerToken(String id, String token) {
        WebApi webApi = WebApiCreator.create();
        webApi.registerDisplay(id, token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }
}
