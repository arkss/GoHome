package com.uos.gohome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.uos.gohome.AddressRecycler.AddressData;
import com.uos.gohome.AddressRecycler.AddressRecyclerAdapter;

import java.util.ArrayList;

public class AddressSearch extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_search);

        // RecyclerView에 표시할 데이터 리스트 생성.
        ArrayList<AddressData> addressDataList = new ArrayList<AddressData>();
        addressDataList.add(new AddressData("가"));
        addressDataList.add(new AddressData("나"));
        addressDataList.add(new AddressData("다"));

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
}