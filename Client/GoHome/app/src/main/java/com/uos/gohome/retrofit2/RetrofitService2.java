package com.uos.gohome.retrofit2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService2 {
    @GET("api/bikestops")
    Call<BikestopData> getBikestops();

    @GET("api/routes")
    Call<RouteSearchResult> getRoutes(@Query("lat_start") double lat_start, @Query("lon_start") double lon_start, @Query("lat_end") double lat_end, @Query("lon_end") double lon_end, @Query("include_bike") String include_bike, @Query("include_bus") String include_bus);
}