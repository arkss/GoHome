package com.uos.gohome;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapView;

public class ShareActivity extends AppCompatActivity {
    private static final String URI_SCHEME = "uosgohome";
    private static final String URI_HOST = "share";
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
                String param = data.getQueryParameter("id");
                textPram.setText(param);
            }
        }

    }
}
