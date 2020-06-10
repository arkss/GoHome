package com.example.gohome.retrofit2;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitService2 {
    @GET("api/bikestops")
    Call<BikestopData> getBikestops();

    @GET("routes")
    Call<RouteSearchResult> getRoutes(RouteSearchQuery routeSearchQuery);
}