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
import com.example.gohome.OnGpsEventListener;
import com.example.gohome.R;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.example.gohome.SharePositionDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skt.Tmap.TMapView;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class MapFragment extends Fragment implements OnGpsEventListener {
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

        // GPS init
        gpsTracker = new GpsTracker(this.getContext(), this);
    }

    @Override
    public void onGpsEvent(Location location)
    {
        if(location != null) {
            double lat = location.getLatitude(), lon = location.getLongitude();
            Toast.makeText(this.getContext(), "latitude: " + Double.toString(lat) + ", longitude: " + Double.toString(lon), Toast.LENGTH_SHORT).show();

            // 내 위치로 이동
            tMapView.setLocationPoint(lon, lat);
            tMapView.setCenterPoint(lon, lat);
        }
    }
}
