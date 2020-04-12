package com.example.gohome;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_route);

        // toolbar 생성
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // title 제거
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 생성

        // tmapview 추가
        TMapView tMapView = new TMapView(this);
        LinearLayout tMapLayout = (LinearLayout)findViewById(R.id.route_tmap);
        tMapLayout.addView(tMapView);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // 툴바의 뒤로가기 버튼을 눌렀을 때
            case android.R.id.home :
                finish();
                return true;
            default :

        }
        return super.onOptionsItemSelected(item);
    }
}
