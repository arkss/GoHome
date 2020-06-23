package com.uos.gohome;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.google.gson.JsonObject;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.uos.gohome.retrofit2.PositionResponseData;
import com.uos.gohome.retrofit2.RetrofitClientInstance;
import com.uos.gohome.retrofit2.RetrofitService;

public class ShareActivity extends AppCompatActivity {
    private static final String URI_SCHEME = "https";
    private static final String URI_HOST = "gohome-node.com";
    private static final String URI_PATH = "share";
    public static final String URI_DEFAULT = URI_SCHEME+"://"+URI_HOST+"/"+URI_PATH;

    private int routeId;
    private boolean isShared;
    private TMapView tMapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);

        // find view by id
        LinearLayout tMapLayout = (LinearLayout) findViewById(R.id.tmap_share);

        // init tmap view
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xx7574967eec1847a08c21f9d5c78980d4"); // 찬표 api key
        tMapLayout.addView(tMapView);

        // get intent
        Intent intent = getIntent();
        Uri data = intent.getData();

        // get params
        if (data != null) {
            if (data.getScheme().equals(URI_SCHEME) && data.getHost().equals(URI_HOST)) {
                if(data.getQueryParameter("id") != null) {
                    routeId = Integer.parseInt(data.getQueryParameter("id"));
                    isShared = true;
                }
            }
        }


        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                serviceFunction(service, routeId);
                if (isShared) {
                    handler.postDelayed(this, MainActivity.DELAY_TIME);
                }
            }
        }, MainActivity.DELAY_TIME);

    }


    public void serviceFunction(RetrofitService service, int routeId) {
        Call<PositionResponseData> request = service.getPosition(routeId);
        request.enqueue(new Callback<PositionResponseData>() {
            @Override
            public void onResponse(Call<PositionResponseData> call, Response<PositionResponseData> response) {
                if(response.isSuccessful()) {
                    Log.d("TEST", "SHARE SUCCESSFUL");
                    PositionResponseData data = response.body();
                    TMapPolyLine line = new TMapPolyLine();
                    line.setLineWidth(2);
                    line.setLineColor(Color.BLUE);
                    line.setOutLineColor(Color.BLUE);
                    for(PositionResponseData.RouteData e: data.getData()) {
                        line.addLinePoint(new TMapPoint(e.getLat(), e.getLog()));
                    }
                    tMapView.addTMapPath(line);
                    tMapView.setCenterPoint(data.getData().get(0).getLog(), data.getData().get(0).getLat());
                }
                else {
                    Log.d("TEST", "MSG: "+response.message());
                }
            }

            @Override
            public void onFailure(Call<PositionResponseData> call, Throwable t) {
                Log.d("TEST", "failted, "+t.getMessage());
            }
        });
    }
}
