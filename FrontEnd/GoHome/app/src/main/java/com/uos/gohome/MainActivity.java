package com.uos.gohome;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.uos.gohome.main.MapFragment;

import com.uos.gohome.main.RouteFragment;
import com.uos.gohome.main.SearchFragment;
import com.uos.gohome.retrofit2.Datum;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

public class MainActivity extends AppCompatActivity implements OnGpsEventListener, SearchFragment.OnDataSendListener, RouteFragment.tmapSendListener {
    MapFragment mapFragment;

    GpsTracker gpsTracker;
    TMapPolyLine polyLine;

    private TMapView tMapView;

    Datum datum;

    private RouteFragment routeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        routeFragment = new RouteFragment();
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
//            Toast.makeText(this, "latitude: " + Double.toString(lat) + ", longitude: " + Double.toString(lon), Toast.LENGTH_SHORT).show();
            mapFragment.setLocationPoint(location);
        } catch(Exception e) {
            Log.e("onGpsEvent", e.getMessage());
        }
    }

    @Override
    public void setDatum(Datum datum) {
        this.datum = datum;
    }

    public Datum getDatum() {
        return datum;
    }

    @Override
    public void setTmapView(TMapView tmapView) {
        this.tMapView = tmapView;
    }

    public TMapView gettMapView() {
        return tMapView;
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