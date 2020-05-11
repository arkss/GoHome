package com.example.gohome.retrofit2;

import com.example.gohome.retrofit2.RetrofitData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("/posts")
    Call<List<RetrofitData>> getPosts();

    @GET("/posts")
    Call<List<RetrofitData>> getPost(@Query("id") Integer id);
}
