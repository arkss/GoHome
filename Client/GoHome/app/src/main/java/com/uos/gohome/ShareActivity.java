package com.uos.gohome;

import android.content.Intent;
import android.graphics.BitmapFactory;
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
import com.skt.Tmap.TMapView;
import com.uos.gohome.retrofit2.RetrofitClientInstance;
import com.uos.gohome.retrofit2.RetrofitService;

public class ShareActivity extends AppCompatActivity {
    private static final String URI_SCHEME = "uosgohome";
    private static final String URI_HOST = "share";
    public static final String URI_DEFAULT = "uosgohome://share/";

    private int routeId;
    private boolean isShared;
    private TMapView tMapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);

        // find view by id
        TextView textPram = (TextView)findViewById(R.id.url_params);
        LinearLayout tMapLayout = (LinearLayout)findViewById(R.id.tmap_share);

        // init tmap view
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xx7574967eec1847a08c21f9d5c78980d4"); // 찬표 api key
        tMapView.setIconVisibility(true);
        tMapView.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.dot));
        tMapLayout.addView(tMapView);

        // get intent
        Intent intent = getIntent();
        Uri data = intent.getData();

        // get params
        if(data != null) {
            if(data.getScheme().equals(URI_SCHEME) && data.getHost().equals(URI_HOST)) {
                routeId = Integer.parseInt(data.getQueryParameter("id"));
                isShared = true;
                textPram.setText(routeId);
            }
        }


        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                serviceFunction(service, routeId);
                if(isShared) {
                    handler.postDelayed(this, 2000);
                }
            }
        }, 1000);

    }

    public void serviceFunction(RetrofitService service, int routeId) {
        Call<JsonObject> request = service.getPosition(routeId);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    JsonObject data = response.body();
                    String msg = data.get("response").getAsString();
                    Log.d("TEST", "Share activity: "+ msg);
                }
                else {
                    Log.d("TEST", "MSG: "+response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("TEST", "failted, "+t.getMessage());
            }
        });
    }
}
