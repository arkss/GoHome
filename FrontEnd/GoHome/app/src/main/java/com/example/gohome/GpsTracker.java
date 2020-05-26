package com.example.gohome;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gohome.main.MapFragment;
import com.example.gohome.OnGpsEventListener;

import static android.content.Context.LOCATION_SERVICE;

public class GpsTracker {
    //위도 경도 형식으로 받아온 배열값
//    private double[][] gpsNodePoint = {
//            {37.586488, 127.054158} // 전농파출소 위도, 경도
//            //{ 37.583484, 127.054825 } // 정문 세븐일레븐
//            //{ 37.4219983, -127.084}
//    };

    //GPS 표시를 위한 변수, 상수
    private boolean isGPSEnabled;
    private boolean isNetEnabled;
    private Location myLocation = new Location("myLoc");

    private OnGpsEventListener onGpsEventListener;

    public GpsTracker(Context mainContext, Fragment mapFragment) {
        onGpsEventListener = (OnGpsEventListener)mapFragment;

        //checkPermission();
        Log.e("GpsTracker", "GpsTracker creating...");

        LocationManager lm = (LocationManager)mainContext.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
//
//                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                isNetEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                myLocation = location;
                onGpsEventListener.onGpsEvent(myLocation);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        assert lm != null;
        if (ActivityCompat.checkSelfPermission(mainContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,1, locationListener);
    }

    public Location getLocation() {
        if(myLocation != null)
            return myLocation;
        return null;
    }
}
