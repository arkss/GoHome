package com.example.gohome.retrofit2;

import com.example.gohome.retrofit2.RetrofitData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

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
