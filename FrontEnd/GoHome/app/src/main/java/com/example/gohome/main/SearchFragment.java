package com.example.gohome.main;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gohome.GpsTracker;
import com.example.gohome.MainActivity;
import com.example.gohome.OnGpsEventListener;
import com.example.gohome.R;
import com.example.gohome.SearchRecycler.InnerData;
import com.example.gohome.SearchRecycler.SearchData;
import com.example.gohome.SearchRecycler.SearchRecyclerAdapter;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gohome.OnGpsEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SearchFragment.this)
                        .navigate(R.id.action_SearchFragment_to_RouteFragment);
            }
        });

        /* Test code */
        ArrayList<SearchData> dataList = new ArrayList<>();
        dataList.add(new SearchData("17분"));
        dataList.add(new SearchData("21분"));

        ArrayList<InnerData> dataInnerList1 = new ArrayList<>();
//        dataInnerList1.add(new InnerData(R.drawable.bicycle_icon, "따릉이 정류소 1"));
//        dataInnerList1.add(new InnerData(R.drawable.bus_icon, "N버스 정류소 1"));
        dataInnerList1.add(new InnerData(R.drawable.bicycle_icon));
        dataInnerList1.add(new InnerData(R.drawable.bus_icon));


        ArrayList<InnerData> dataInnerList2 = new ArrayList<>();
//        dataInnerList2.add(new InnerData(R.drawable.bicycle_icon, "따릉이 정류소 2"));
//        dataInnerList2.add(new InnerData(R.drawable.bus_icon, "N버스 정류소 2"));
//        dataInnerList2.add(new InnerData(R.drawable.taxi_icon, "택시"));

        dataInnerList2.add(new InnerData(R.drawable.bicycle_icon));
        dataInnerList2.add(new InnerData(R.drawable.bus_icon));
        dataInnerList2.add(new InnerData(R.drawable.taxi_icon));

        ArrayList<ArrayList<InnerData>> allInnerData = new ArrayList<>();
        allInnerData.add(dataInnerList1);
        allInnerData.add(dataInnerList2);
        /* test code */

        // init recycler view
        RecyclerView recyclerView = view.findViewById(R.id.search_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // add divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        SearchRecyclerAdapter adapter = new SearchRecyclerAdapter(getActivity(), dataList, allInnerData, this);
        recyclerView.setAdapter(adapter);

        // POI search
        TMapData tmapdata = new TMapData();
        tmapdata.findAllPOI("강남역", new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList poiItem) {
                // 모든 POI 출력.
                for(int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = (TMapPOIItem)poiItem.get(i);
                    Log.d("POI Name: ", item.getPOIName().toString() + ", " +
                            "Address: " + item.getPOIAddress().replace("null", "")  + ", " +
                            "Point: " + item.getPOIPoint().toString());
                }
                TMapPOIItem item = (TMapPOIItem)poiItem.get(0);
                routeSearch(item.getPOIPoint());
            }
        });
    }

    public void routeSearch(TMapPoint dst) {
        Location dpt = ((MainActivity)getActivity()).getLocation();

        Log.d("routeSearch", Double.toString(dpt.getLatitude()) + " " + Double.toString(dpt.getLongitude()));
        Log.d("routeSearch", Double.toString(dst.getLatitude()) + " " + Double.toString(dst.getLongitude()));
    }
}
