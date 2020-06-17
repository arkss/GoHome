package com.uos.gohome.retrofit2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitService {

    @GET("/")
    Call<JsonObject> loginConfirm(@Header("Authorization") String token);

    @POST("login/")
    Call<JsonObject> login(@Body LoginData data);

    @GET("profile/")
    Call<JsonObject> getProfile();

    @POST("signup/")
    Call<JsonObject> signup(@Body SignupData data);

    @GET("signup/")
    Call<JsonArray> allSignup();

    @POST("share/route/")
    Call<PostRouteData> postRoute(@Header("Authorization") String token);

    @GET("share/{route_id}/position/")
    Call<JsonObject> getPosition(@Path("route_id") int routeId);

    @POST("share/{route_id}/position/")
    Call<JsonObject> postPosition(@Header("Authorization") String token, @Path("route_id") int routeId, @Field("lat") double lat, @Field("log") double log);
}
