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
    private Location myLocation = new Location("myLoc");

    private OnGpsEventListener onGpsEventListener;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;

    double minLat = 37.423930, maxLat = 37.704151;
    double minLon = 126.761920, maxLon = 127.186964;

    // GPS 권한 체크
    private void checkPermission(Activity activity) {
        try {
            //권한 얻기 - GPS
            if(ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(activity, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        }
        catch(Exception e) {
            Log.e("GPS permission exception", e.getMessage());
        }
    }

    public GpsTracker(Activity activity) {
        onGpsEventListener = (OnGpsEventListener)activity;

        checkPermission(activity);
        Log.e("GpsTracker", "GpsTracker creating...");

        LocationManager lm = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude(), lon = location.getLongitude();
                if(lat >= minLat && lat <= maxLat && lon >= minLon && lon <= maxLon) {
                    myLocation = location;
                    onGpsEventListener.onGpsEvent(myLocation);
                }
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
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
