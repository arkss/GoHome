package com.example.gohome.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gohome.GpsTracker;
import com.example.gohome.MainActivity;
import com.example.gohome.OnGpsEventListener;
import com.example.gohome.R;
import com.example.gohome.SignUpActivity;
import com.example.gohome.retrofit2.Bikestop;
import com.example.gohome.retrofit2.BikestopData;
import com.example.gohome.retrofit2.RetrofitClientInstance;
import com.example.gohome.retrofit2.RetrofitClientInstance2;
import com.example.gohome.retrofit2.RetrofitService;
import com.example.gohome.retrofit2.RetrofitService2;
import com.google.gson.JsonObject;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.example.gohome.SharePositionDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class MapFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FloatingActionButton shareBtn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DrawerLayout drawerLayout;
    View drawerView;
    TMapView tMapView;

    GpsTracker gpsTracker;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Fragment가 생성될 때 호출
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // onCreate 후 View를 구성
        tMapView = new TMapView(getContext());
        tMapView.setSKTMapApiKey("l7xx7574967eec1847a08c21f9d5c78980d4"); // 찬표 api key

        tMapView.setIconVisibility(true);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 티맵 view 생성
        LinearLayout linearLayoutTmap = getView().findViewById(R.id.linearLayoutTmap);
        linearLayoutTmap.addView(tMapView);

        TMapPoint point1 = new TMapPoint(37.583, 127.054);
        TMapPoint point2 = new TMapPoint(37.580, 127.047);
        ArrayList<TMapPoint> list = new ArrayList<>();
        TMapData tmapdata = new TMapData();
        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                tMapView.addTMapPath(polyLine);
                ArrayList<TMapPoint> t_list = polyLine.getLinePoint();
                for(TMapPoint point : t_list) {
                    Log.d("MapFragment", "l: "+point.getLongitude() + ", l: "+point.getLatitude());
                }
            }
        });


        shareBtn = (FloatingActionButton) view.findViewById(R.id.position_share_btn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePositionDialog dialog = SharePositionDialog.newInstance("url here");
                dialog.show(getActivity().getSupportFragmentManager(), "share dialog");
            }
        });

        view.findViewById(R.id.searchbar_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MapFragment.this)
                        .navigate(R.id.action_MapFragment_to_SearchFragment);
            }
        });

        // Drawer
        drawerLayout = (DrawerLayout)getView().findViewById(R.id.drawer_layout);
        drawerView = (View)getView().findViewById(R.id.drawer);
        ImageButton sMenuBtn = (ImageButton)getView().findViewById(R.id.searchbar_menu);
        sMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView, true);
            }
        });

        // Show bike stops
        //showBikestops();
    }

    void showBikestops() {
        final Retrofit retrofit = RetrofitClientInstance2.getRetrofitInstance();
        final RetrofitService2 service = retrofit.create(RetrofitService2.class);
        Call<BikestopData> request = service.getBikestops();
        request.enqueue(new Callback<BikestopData>() {
            String bikestop = "BIKESTOP";
            @Override
            public void onResponse(Call<BikestopData> call, Response<BikestopData> response) {
                if(response.isSuccessful()) {
                    Log.d(bikestop, "onResponse");

                    BikestopData bikestopData = response.body();

                    // Log the message.
                    String msg = bikestopData.getMessage();
                    Log.d(bikestop, msg);

                    // Set bikestops pins.
                    Bikestop[] bikestops = bikestopData.getBikestops();
                    Integer cnt = 0;
                    for(Bikestop bikestop : bikestops) {
                        TMapMarkerItem markerItem = new TMapMarkerItem();
                        markerItem.setTMapPoint(bikestop.getPoint());
                        tMapView.addMarkerItem("markerItem" + cnt.toString(), markerItem);
                        cnt++;
                    }
                } else
                    Log.d(bikestop,"response is not successful");
            }

            @Override
            public void onFailure(Call<BikestopData> call, Throwable t) {
                Log.d(bikestop,"communicate failed, msg:"+t.getMessage());
            }
        });
    }
}
