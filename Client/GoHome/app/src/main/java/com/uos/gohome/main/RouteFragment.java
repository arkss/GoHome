package com.uos.gohome.main;

import android.content.Context;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.uos.gohome.MainActivity;
import com.uos.gohome.GpsTracker;
import com.uos.gohome.R;
import com.uos.gohome.ShareActivity;
import com.uos.gohome.SharePositionDialog;
import com.uos.gohome.retrofit2.Datum;
import com.uos.gohome.retrofit2.PostRouteData;
import com.uos.gohome.retrofit2.RetrofitClientInstance;
import com.uos.gohome.retrofit2.RetrofitService;
import com.uos.gohome.retrofit2.Section;
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

    private tmapSendListener mListener;

    private TMapView tMapView;
    private TMapPolyLine polyLine[];

    private FloatingActionButton cameraBtn;
    private FloatingActionButton shareBtn;
    private FloatingActionButton locationBtn;

    private Datum datum;

    private RetrofitService service;

    private TMapPoint points[];

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
        }

        // init variable
        service = RetrofitClientInstance.getRetrofitInstance().create(RetrofitService.class);

        // use onOptionsItemSelected in Fragment

        Context context = getContext();
        if(context instanceof tmapSendListener) {
            mListener = (tmapSendListener) context;
        }
        else {
            throw new RuntimeException(context.toString()+" must implement event listener");
        }

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

        // minLat, minLon, maxLat, maxLon
        double[] minMaxLatLon = getMinMaxLatLon();
        double minLat = minMaxLatLon[0], minLon = minMaxLatLon[1];
        double maxLat = minMaxLatLon[2], maxLon = minMaxLatLon[3];

        // Move my location and zoom control
        Location myLocation = ((MainActivity)getActivity()).getLocation();
        tMapView.setLocationPoint(myLocation.getLongitude(), myLocation.getLatitude());
        tMapView.zoomToSpan(maxLat-minLat, maxLon-minLon);
        tMapView.setCenterPoint(myLocation.getLongitude(), myLocation.getLatitude());
    }

    // [minLat, minLon, maxLat, maxLon]
    double[] getMinMaxLatLon() {
        double minLat = 999, minLon = 999, maxLat = -999, maxLon = -999;
        List<Section> sectionList = datum.getSections();
        for(Section section : sectionList) {
            for (List<Double> point : section.getPoints()) {
                minLat = min(minLat, point.get(0));
                minLon = min(minLon, point.get(1));
                maxLat = max(maxLat, point.get(0));
                maxLon = max(maxLon, point.get(1));
            }
        }
        double[] minMaxLatLon = {minLat, minLon, maxLat, maxLon};
        return minMaxLatLon;
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

    public void drawLine() {
        List<Section> sectionList = datum.getSections();
        polyLine = new TMapPolyLine[sectionList.size()];
        for (int i = 0; i < sectionList.size(); i++) {
            polyLine[i] = new TMapPolyLine();
            polyLine[i].setLineWidth(2);

            Log.d("TEST", "section size = " + sectionList.size());
            // set line color
            switch (sectionList.get(i).getType()) {
                case 1:
                    polyLine[i].setLineColor(Color.BLACK);
                    polyLine[i].setOutLineColor(Color.BLACK);
                    break;
                case 2:
                    polyLine[i].setLineColor(Color.GREEN);
                    polyLine[i].setOutLineColor(Color.GREEN);
                    break;
                case 3:
                    polyLine[i].setLineColor(Color.RED);
                    polyLine[i].setOutLineColor(Color.RED);
                    break;
            }

            // set line points
            for (List<Double> point : sectionList.get(i).getPoints()) {
                Log.d("TEST", "point(0)=" + point.get(0) + ", point(1)=" + point.get(1));
                polyLine[i].addLinePoint(new TMapPoint(point.get(0), point.get(1)));
            }

            tMapView.addTMapPolyLine("line" + i, polyLine[i]);

        }
    }

    public ArrayList<double[]> getPointList() {
        ArrayList<double[]> list = new ArrayList<>();
        for(Section section : datum.getSections()) {
            for(List<Double> point : section.getPoints()) {
                list.add(new double[]{point.get(0), point.get(1)});
            }
        }
        return list;
    }

    public TMapView gettMapView(){
        return tMapView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // position share btn clicked
            case R.id.route_share_btn:
                Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
                RetrofitService service = retrofit.create(RetrofitService.class);
                openDialog();
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
                Intent intent = new Intent(getActivity(), com.uos.gohome.testActivity.class);
                intent.putExtra("points", getPointList());

                if(mListener != null) {
                    mListener.setTmapView(tMapView);
                }

                startActivity(intent);
                break;
        }
    }

    // openDialog & postRoute
    public void openDialog() {
        Call<PostRouteData> request = service.postRoute(((MainActivity)getActivity()).token);
        request.enqueue(new Callback<PostRouteData>() {
            @Override
            public void onResponse(Call<PostRouteData> call, Response<PostRouteData> response) {
                if(response.isSuccessful()) {
                    PostRouteData data = response.body();
                    int routeId = data.getRoute_Id();
                    Toast.makeText(getActivity(), "route_id:"+routeId, Toast.LENGTH_SHORT).show();

                    SharePositionDialog dialog = SharePositionDialog.newInstance(ShareActivity.URI_DEFAULT, routeId);
                    dialog.show(getActivity().getSupportFragmentManager(), "tag dialog");
                }
                // response isn't successful
                else {
                    Log.d("TEST", "___MSG: "+response.message());
                }
            }

            @Override
            public void onFailure(Call<PostRouteData> call, Throwable t) {
                Log.d("Test", "postRoute falied "+t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
        ((MainActivity)getActivity()).endShared();
    }

    public interface tmapSendListener {
        public void setTmapView(TMapView tmapView);
    }
}
