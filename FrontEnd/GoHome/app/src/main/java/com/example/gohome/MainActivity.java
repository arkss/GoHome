package com.example.gohome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.gohome.main.MapFragment;
import com.example.gohome.main.RouteFragment;
import com.example.gohome.main.SearchFragment;

import com.skt.Tmap.TMapView;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    View drawerView;

    private FragmentManager fragmentManager;
    private MapFragment mapFragment;
    private SearchFragment searchFragment;
    private RouteFragment routeFragment;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        // Fragment switch
//        fragmentManager = getSupportFragmentManager();
//
//        mapFragment = new MapFragment();
//        searchFragment = new SearchFragment();
////        routeFragment = new RouteFragment();
////
//        transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.mainFrameLayout, mapFragment).commitAllowingStateLoss();



//        ImageButton sSearchBtn = (ImageButton)findViewById(R.id.searchbar_search);
//        sSearchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, RouteActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        EditText sSearchText = (EditText)findViewById(R.id.searchbar_text);
//        sSearchText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, RouteSearchActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
