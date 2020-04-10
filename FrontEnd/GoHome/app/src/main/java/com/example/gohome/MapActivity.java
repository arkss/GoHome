package com.example.gohome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapView;

public class MapActivity extends AppCompatActivity {
    final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
    final View drawerView = (View)findViewById(R.id.drawer);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // 티맵 view 생성
        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        TMapView tMapView = new TMapView(this);
//        tMapView.setSKTMapApiKey("l7xx696f792e752c433bacdc8fa3b644bf9b");
        tMapView.setSKTMapApiKey("l7xx7574967eec1847a08c21f9d5c78980d4"); // 찬표 api key
        linearLayoutTmap.addView(tMapView);

        // search bar의 menu버튼 클릭시 map_drawer 레이아웃 열림
        ImageButton sMenuBtn = (ImageButton) findViewById(R.id.searchbar_menu);
        sMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView, true);
            }
        });

    }
}
