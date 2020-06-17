package com.uos.gohome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapView;
import com.uos.gohome.AddressRecycler.AddressData;
import com.uos.gohome.AddressRecycler.AddressRecyclerAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AddressSearch extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager layoutManager;
    ArrayList<AddressData> addressDataList;

    TMapView tMapView;
    TMapData tmapdata;

    Handler notifyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_search);

        // RecyclerView에 표시할 데이터 리스트 생성.
        addressDataList = new ArrayList<AddressData>();
        addressDataList.add(new AddressData("가", 0, 0));

        recyclerView = (RecyclerView)findViewById(R.id.address_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 구분선 그리기
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // specify an adapter (see also next example)
        mAdapter = new AddressRecyclerAdapter(addressDataList);
        recyclerView.setAdapter(mAdapter);

        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xx7574967eec1847a08c21f9d5c78980d4"); // 찬표 api key
        tmapdata = new TMapData();

        // 버튼 누르면 주소 검색
        Button searchButton = (Button)findViewById(R.id.address_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextPOI = (EditText)findViewById(R.id.editTextPOI);
                String text = editTextPOI.getText().toString();
                getAddresses(text);  // update addressDataList
            }
        });

        notifyHandler = new Handler(){
            public void handleMessage(Message msg){
                mAdapter.notifyDataSetChanged();
            }
        };

//        addressDataList.add(new AddressData("나"));
//        mAdapter.notifyDataSetChanged();
//
//        addressDataList.add(new AddressData("다"));
//        mAdapter.notifyDataSetChanged();

//        Intent data = new Intent();
//        data.putExtra("latitude", 37.58396415159224);
//        data.putExtra("longitude", 127.0590090751652);
//        data.putExtra("address", "서울시립대학교");
//        setResult(RESULT_OK, data);
//        finish();
    }

    void getAddresses(String text) {
        addressDataList.clear();
        tmapdata.findAllPOI(text, 10, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList poiItem) {
                // 모든 주소, 위도 경도 저장.
                for(int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = (TMapPOIItem)poiItem.get(i);
                    String address = item.getPOIAddress().replace(" null", "") + " " + item.getPOIName();
                    double latitude = item.getPOIPoint().getLatitude();
                    double longitude = item.getPOIPoint().getLongitude();
                    addressDataList.add(new AddressData(address, latitude, longitude));
                }
                Message msg = notifyHandler.obtainMessage();
                notifyHandler.sendMessage(msg);
            }
        });
    }
}