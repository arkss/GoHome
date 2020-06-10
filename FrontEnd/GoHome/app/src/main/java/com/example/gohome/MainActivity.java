package com.example.gohome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements OnGpsEventListener {
    GpsTracker gpsTracker;

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
        } catch(Exception e) {
            Log.e("onGpsEvent", e.getMessage());
        }
    }
}