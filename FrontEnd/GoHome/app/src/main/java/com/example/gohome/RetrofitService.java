package com.example.gohome;

import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("/posts")
    Call<List<RetrofitData>> getPosts();

    @GET("/posts")
    Call<List<RetrofitData>> getPost(@Query("id") Integer id);
}
