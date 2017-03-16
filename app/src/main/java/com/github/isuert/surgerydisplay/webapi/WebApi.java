package com.github.isuert.surgerydisplay.webapi;

import com.github.isuert.surgerydisplay.models.DisplayConfig;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WebApi {
    @GET("get-display-config.php")
    Call<DisplayConfig> getDisplayConfig(@Query("id") String id);

    @FormUrlEncoded
    @POST("register-display.php")
    Call<Void> registerDisplay(@Field("id") String id, @Field("token") String token);
}
