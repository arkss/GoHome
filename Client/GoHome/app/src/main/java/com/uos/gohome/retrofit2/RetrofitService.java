package com.uos.gohome.retrofit2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RetrofitService {

    @GET("/")
    Call<JsonObject> loginConfirm(@Header("Authorization") String token);

    @POST("login/")
    Call<JsonObject> login(@Body LoginData data);

    @POST("signup/")
    Call<JsonObject> signup(@Body SignupData data);

    @GET("signup/")
    Call<JsonArray> allSignup();
}
