package com.example.gohome.main;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.gohome.MainActivity;

import com.example.gohome.R;
import com.example.gohome.SearchRecycler.InnerData;
import com.example.gohome.SearchRecycler.SearchData;
import com.example.gohome.SearchRecycler.SearchRecyclerAdapter;
import com.example.gohome.retrofit2.Datum;
import com.example.gohome.retrofit2.RetrofitClientInstance2;
import com.example.gohome.retrofit2.RetrofitService2;
import com.example.gohome.retrofit2.RouteSearchResult;
import com.example.gohome.retrofit2.Section;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skt.Tmap.TMapPolyLine;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {

    private OnDataSendListener mListener;

    private ImageButton backBtn;
    private ImageButton swapBtn;
    private ImageButton okBtn;

    private EditText text_destination;
    private EditText text_departure;

    private ArrayList<SearchData> searchDataList;
    private ArrayList<ArrayList<InnerData>> innerDataList;

    private List<Datum> datumList;

    TMapData tmapdata;

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
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        Context context = getContext();
        if(context instanceof OnDataSendListener) {
            mListener = (OnDataSendListener) context;
        }
        else {
            throw new RuntimeException(context.toString()+" must implement event listener");
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

        // find view
        backBtn = (ImageButton)view.findViewById(R.id.imageButtonBackFromSearch);
        swapBtn = (ImageButton)view.findViewById(R.id.imageButtonSwap);
        okBtn = (ImageButton)view.findViewById(R.id.imageButtonMoreVert);
        text_departure = (EditText)view.findViewById(R.id.editTextDeparture);
        text_destination = (EditText)view.findViewById(R.id.editTextDestination);

        backBtn.setOnClickListener(this);
        swapBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);

        // init recycler view
        RecyclerView recyclerView = view.findViewById(R.id.search_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // init data list
        searchDataList = new ArrayList<>();
        innerDataList = new ArrayList<>();

        // add divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        SearchRecyclerAdapter adapter = new SearchRecyclerAdapter(getActivity(), searchDataList, innerDataList, this);
        recyclerView.setAdapter(adapter);

        // when search button is pressed
        ImageButton searchButton = (ImageButton)getActivity().findViewById(R.id.imageButtonMoreVert);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // destination
//                String destinationText = destination.getText().toString();
                String destinationText = "서울시립대학교";

                // POI search
                tmapdata = new TMapData();
                tmapdata.findAllPOI(destinationText, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList poiItem) {
                        // 모든 POI 출력.
                        for(int i = 0; i < poiItem.size(); i++) {
                            TMapPOIItem item = (TMapPOIItem)poiItem.get(i);
                            Log.d("POI Name: ", item.getPOIName().toString() + ", " +
                                    "Address: " + item.getPOIAddress().replace("null", "")  + ", " +
                                    "Point: " + item.getPOIPoint().toString());
                        }
                        // get first poi item
                        TMapPOIItem item = (TMapPOIItem)poiItem.get(0);

                        // get departure, destination point
                        TMapPoint destination = item.getPOIPoint();
                        Location departure_location = ((MainActivity)getActivity()).getLocation();
                        TMapPoint departure = new TMapPoint(departure_location.getLatitude(), departure_location.getLongitude());
                        Log.d("TEST", "current latitude: "+departure.getLatitude()+", longitude: "+departure.getLongitude());

                        // request routes
                        Retrofit retrofit = RetrofitClientInstance2.getRetrofitInstance();
                        RetrofitService2 service = retrofit.create(RetrofitService2.class);
                        Call<RouteSearchResult> request = service.getRoutes(departure_location.getLatitude(), departure_location.getLongitude(), destination.getLatitude(), destination.getLongitude(), "Y", "N");
                        request.enqueue(new Callback<RouteSearchResult>() {
                            @Override
                            public void onResponse(Call<RouteSearchResult> call, Response<RouteSearchResult> response) {
                                if(response.isSuccessful()) {
                                    Log.d("TEST", "CONNECT SUCCESSFUL");
                                    RouteSearchResult data = response.body();
                                    int result = data.getResult();
                                    // 정상 처리됨
                                    if(result == 1) {
                                        String message = data.getMessage();
                                        datumList = data.getData();
                                        for(Datum datum : datumList) {
                                            Log.d("TEST", "total time: "+datum.getTime()+", total distance: "+datum.getDistance());
                                            int minute = datum.getTime()/60;
                                            int walkingTime = 0;
                                            ArrayList<InnerData> in = new ArrayList<>();

                                            for(Section section : datum.getSections()) {
                                                // 도보인 경우
                                                if(section.getType() == 1) {
                                                    Log.d("TEST", "도보시간: "+section.getTime()+", 도보 거리: "+section.getDistance());
                                                    walkingTime += section.getTime();
                                                }
                                                switch(section.getType()) {
                                                    case 1:
                                                        in.add(new InnerData(R.drawable.walking_icon));
                                                        break;
                                                    case 2:
                                                        in.add(new InnerData(R.drawable.bicycle_icon));
                                                        break;
                                                    case 3:
                                                        in.add(new InnerData(R.drawable.bus_icon));
                                                }
                                            }
                                            searchDataList.add(new SearchData("총 "+minute+"분", "도보시간 "+walkingTime+"분"));
                                            innerDataList.add(in);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                    // 오류
                                    else if(result == -1) {
                                        Log.e("TEST", "result == -1, error");
                                    }
                                }
                                else {
                                    Log.d("TEST", response.toString());
                                }
                            }

                            @Override
                            public void onFailure(Call<RouteSearchResult> call, Throwable t) {
                                Log.e("TEST", t.getMessage());
                            }
                        });



//                        routeSearch(departure, destination);
                    }
                });
            }
        });
    }

    public void routeSearch(TMapPoint dpt, TMapPoint dst) {
        Log.d("routeSearch", Double.toString(dpt.getLatitude()) + " " + Double.toString(dpt.getLongitude()));
        Log.d("routeSearch", Double.toString(dst.getLatitude()) + " " + Double.toString(dst.getLongitude()));

        try {
            TMapPolyLine polyLine = tmapdata.findPathData(dpt, dst);
            ((MainActivity)getActivity()).setPolyLine(polyLine);
            NavHostFragment.findNavController(SearchFragment.this)
                    .navigate(R.id.action_SearchFragment_to_RouteFragment);
        } catch(Exception e) {
            Log.e("routeSearch", e.getMessage());
        }
    }

    // recycler view에서 아이템이 선택됐을 때, recycler view에서 호출하는 메소드
    public void selectItem(int position) {
        if(mListener != null) {
            mListener.onDataInteraction(datumList.get(position));
        }
        NavHostFragment.findNavController(SearchFragment.this)
                .navigate(R.id.action_SearchFragment_to_RouteFragment);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButtonBackFromSearch:
                NavHostFragment.findNavController(SearchFragment.this)
                        .navigate(R.id.action_SearchFragment_to_MapFragment);
                break;
            case R.id.imageButtonSwap:
                String tmp = text_departure.getText().toString();
                text_departure.setText(text_destination.getText().toString());
                text_destination.setText(tmp);
                break;
            case R.id.imageButtonMoreVert:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    // fragment간 통신을 위한 인터페이스
    public interface OnDataSendListener {
        void onDataInteraction(Datum datum);
    }
}

