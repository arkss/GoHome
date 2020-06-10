package com.example.gohome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.gohome.main.MapFragment;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.gohome.main.RouteFragment;
import com.example.gohome.main.SearchFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skt.Tmap.TMapPolyLine;

public class MainActivity extends AppCompatActivity implements OnGpsEventListener {
    MapFragment mapFragment;

    GpsTracker gpsTracker;
    TMapPolyLine polyLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

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
        try {
            double lat = location.getLatitude(), lon = location.getLongitude();
            Toast.makeText(this, "latitude: " + Double.toString(lat) + ", longitude: " + Double.toString(lon), Toast.LENGTH_SHORT).show();
            mapFragment.setLocationPoint(location);
        } catch(Exception e) {
            Log.e("onGpsEvent", e.getMessage());
        }
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
}