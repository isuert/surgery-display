package com.github.isuert.surgerydisplay.webapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebApiCreator {
    public static final String API_URL = "http://braintech.software/isuert/surgery/api/";

    public static WebApi create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(WebApi.class);
    }
}
