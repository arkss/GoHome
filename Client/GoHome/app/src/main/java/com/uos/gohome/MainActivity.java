package com.uos.gohome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.uos.gohome.main.MapFragment;

import com.uos.gohome.main.OnDataSendListener;
import com.uos.gohome.main.RouteFragment;
import com.uos.gohome.main.SearchFragment;
import com.uos.gohome.retrofit2.DataInUserProfileData;
import com.uos.gohome.retrofit2.Datum;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.uos.gohome.retrofit2.PostRouteData;
import com.uos.gohome.retrofit2.RetrofitClientInstance;
import com.uos.gohome.retrofit2.RetrofitService;
import com.uos.gohome.retrofit2.UserProfileData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements OnGpsEventListener, OnDataSendListener, RouteFragment.tmapSendListener {
    Retrofit retrofit;
    RetrofitService service;

    public String token;

    public double homeLat, homeLon;
    public String poiName;

    MapFragment mapFragment;

    GpsTracker gpsTracker;
    TMapPolyLine polyLine;

    private TMapView tMapView;

    Datum datum;

    private RouteFragment routeFragment;

    public static final int DELAY_TIME = 3000;

    public boolean goHome = false;
    private boolean isShared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        // 로그인 할 때 전달받은 token
        token = getIntent().getStringExtra("token");

        // 사용자 정보 가져오기
        // GET profile/
        retrofit = RetrofitClientInstance.getRetrofitInstance();
        service = retrofit.create(RetrofitService.class);
        Call<UserProfileData> request = service.getProfile(token);
        request.enqueue(new Callback<UserProfileData>() {
            private static final String tag = "getProfile";

            @Override
            public void onResponse(Call<UserProfileData> call, Response<UserProfileData> response) {
                if (response.isSuccessful()) {
                    Log.d(tag, "success");
                    UserProfileData body = response.body();
                    homeLat = ((DataInUserProfileData)body.getData()).getAddressLat();
                    homeLon = ((DataInUserProfileData)body.getData()).getAddressLog();
                    poiName = ((DataInUserProfileData)body.getData()).getDetailAddress();
                    Log.d(tag, "lat: " + Double.toString(homeLat) + ", lon: " + Double.toString(homeLon));
                } else {
                    Log.e(tag, "not success");
                }
            }

            @Override
            public void onFailure(Call<UserProfileData> call, Throwable t) {
                Log.d(tag, "failure");
            }
        });

        gpsInit();
    }

    private void gpsInit() {
        gpsTracker = new GpsTracker(this);
    }

    public Location getLocation() {
        return gpsTracker.getLocation();
    }

    @Override
    public void onGpsEvent(Location location) {
        double lat = location.getLatitude(), lon = location.getLongitude();

        try {
//            Toast.makeText(this, "latitude: " + Double.toString(lat) + ", longitude: " + Double.toString(lon), Toast.LENGTH_SHORT).show();
            mapFragment.setLocationPoint(location);
        } catch(Exception e) {
            Log.e("onGpsEvent", e.getMessage());
        }

        try {
//            Toast.makeText(this, "latitude: " + Double.toString(lat) + ", longitude: " + Double.toString(lon), Toast.LENGTH_SHORT).show();
            routeFragment.setLocationPoint(location);
        } catch(Exception e) {
            Log.e("onGpsEvent", e.getMessage());
        }
    }

    @Override
    public void setDatum(Datum datum) {
        this.datum = datum;
    }

    public Datum getDatum() {
        return datum;
    }

    @Override
    public void setTmapView(TMapView tmapView) {
        this.tMapView = tmapView;
    }

    public TMapView gettMapView() {
        return tMapView;
    }

    public void setPolyLine(TMapPolyLine polyLine) {
        this.polyLine = polyLine;
    }

    public TMapPolyLine getPolyLine() {
        return polyLine;
    }

    public void setMapFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    public void setRouteFragment(RouteFragment routeFragment) {
        this.routeFragment = routeFragment;
    }

    public void startShare(int routeId) {
        isShared = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<JsonObject> request2 = service.postPosition(token, routeId, getLocation().getLatitude(), getLocation().getLongitude());

                request2.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "POST POSITION", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
                if (isShared) {
                    handler.postDelayed(this, DELAY_TIME);
                }
            }
        }, DELAY_TIME);
    }
    public void endShared() {
        isShared = false;
    }
}