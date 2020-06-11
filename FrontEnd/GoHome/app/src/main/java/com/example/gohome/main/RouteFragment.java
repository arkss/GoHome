package com.example.gohome.main;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gohome.MainActivity;
import com.example.gohome.GpsTracker;
import com.example.gohome.R;
import com.example.gohome.SharePositionDialog;
import com.example.gohome.retrofit2.Datum;
import com.example.gohome.retrofit2.Section;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RouteFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TMapView tMapView;
    private TMapPolyLine polyLine[];

    private FloatingActionButton cameraBtn;
    private FloatingActionButton shareBtn;
    private FloatingActionButton locationBtn;

    private GpsTracker gpsTracker;

    private Datum datum;

    private Handler mHandler;
    private boolean flag = false;

    double minLat = 37.423930, maxLat = 37.704151;
    double minLon = 126.761920, maxLon = 127.186964;

    private TMapPoint points[];

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RouteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RouteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RouteFragment newInstance(String param1, String param2) {
        RouteFragment fragment = new RouteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // init variable


        // use onOptionsItemSelected in Fragment
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_route, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // toolbar 생성
        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false); // title 제거
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 생성

        // tmapview 추가
        tMapView = new TMapView(getContext());
        tMapView.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.dot));
        tMapView.setIconVisibility(true);
        LinearLayout tMapLayout = (LinearLayout)view.findViewById(R.id.route_tmap);
        tMapLayout.addView(tMapView);

        // find view by id
        cameraBtn = (FloatingActionButton) view.findViewById(R.id.route_camera_btn);
        shareBtn = (FloatingActionButton) view.findViewById(R.id.route_share_btn);
        locationBtn = (FloatingActionButton) view.findViewById(R.id.route_location_btn);

        // draw line
        this.datum = ((MainActivity)getActivity()).getDatum();
        drawLine();

        // onClick Listener
        cameraBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        locationBtn.setOnClickListener(this);

//        TMapPolyLine polyLine = ((MainActivity)getActivity()).getPolyLine();
//        tMapView.addTMapPolyLine("fastestPath", polyLine);
//        double minLatitude = getMinLatitude(polyLine.getLinePoint());
//        double maxLatitude = getMaxLatitude(polyLine.getLinePoint());
//        double minLongitude = getMinLongitude(polyLine.getLinePoint());
//        double maxLongitude = getMaxLongitude(polyLine.getLinePoint());
//        tMapView.setCenterPoint((minLongitude+maxLongitude)/2, (minLatitude+maxLatitude)/2);
//        tMapView.zoomToSpan(maxLatitude-minLatitude, maxLongitude-minLongitude);
//        tMapView.setIconVisibility(true);
//
//        Location myLocation = ((MainActivity)getActivity()).getLocation();
//        tMapView.setLocationPoint(myLocation.getLongitude(), myLocation.getLatitude());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // click back btn
            case android.R.id.home :
                NavHostFragment.findNavController(RouteFragment.this)
                        .navigate(R.id.action_RouteFragment_to_MapFragment);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    double getMinLatitude(ArrayList<TMapPoint> tMapPoints) {
        double ret = 999;
        for(TMapPoint tMapPoint : tMapPoints) {
            ret = min(ret, tMapPoint.getLatitude());
        }
        return ret;
    }

    double getMaxLatitude(ArrayList<TMapPoint> tMapPoints) {
        double ret = -999;
        for(TMapPoint tMapPoint : tMapPoints) {
            ret = max(ret, tMapPoint.getLatitude());
        }
        return ret;
    }

    double getMinLongitude(ArrayList<TMapPoint> tMapPoints) {
        double ret = 999;
        for(TMapPoint tMapPoint : tMapPoints) {
            ret = min(ret, tMapPoint.getLongitude());
        }
        return ret;
    }

    double getMaxLongitude(ArrayList<TMapPoint> tMapPoints) {
        double ret = -999;
        for(TMapPoint tMapPoint : tMapPoints) {
            ret = max(ret, tMapPoint.getLongitude());
        }
        return ret;
    }

    public void setDatum(Datum datum) {
        this.datum = datum;
        Log.d("TEST", "datum.toString "+datum.toString());
        flag = true;
        Log.d("TEST", "datum received");
    }

    public void drawLine() {
        List<Section> sectionList = datum.getSections();
        Log.d("TEST", sectionList.get(0).getTime()+" ");
        polyLine = new TMapPolyLine[sectionList.size()];
        Log.d("TEST", "size = "+sectionList.size());
        for(int i=0; i<sectionList.size(); i++) {
            polyLine[i] = new TMapPolyLine();

            for(Section section : sectionList) {
                // set line color
                switch (section.getType()) {
                    case 1:
                        polyLine[i].setLineColor(Color.BLACK);
                        break;
                    case 2:
                        polyLine[i].setLineColor(Color.GREEN);
                        break;
                    case 3:
                        polyLine[i].setLineColor(Color.RED);
                        break;
                }

                // set line points
                for (List<Double> point : section.getPoints()) {
                    Log.d("TEST", "point(0)="+point.get(0)+", point(1)="+point.get(1));
                    polyLine[i].addLinePoint(new TMapPoint(point.get(0), point.get(1)));
                }
                tMapView.addTMapPolyLine("line"+i, polyLine[i]);
            }
        }

        for(TMapPolyLine line : polyLine) {
            Log.d("TEST", line.getDistance()+" ");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // position share btn clicked
            case R.id.route_share_btn:
                SharePositionDialog dialog = SharePositionDialog.newInstance("url here");
                dialog.show(getActivity().getSupportFragmentManager(), "share dialog");
                break;

            // current location btn clicked
            case R.id.route_location_btn:
                Location location = ((MainActivity)getActivity()).getLocation();
                tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
                tMapView.setTrackingMode(true);
                break;

            // camera btn clicked
            case R.id.route_camera_btn:
                Toast.makeText(getActivity(), "AR Open", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), com.example.gohome.testActivity.class);
                startActivity(intent);
                break;
        }
    }
}
