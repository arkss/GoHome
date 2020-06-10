package com.example.gohome.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.gohome.R;
import com.example.gohome.SearchRecycler.InnerData;
import com.example.gohome.SearchRecycler.SearchData;
import com.example.gohome.SearchRecycler.SearchRecyclerAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {

    private ImageButton backBtn;
    private ImageButton swapBtn;
    private ImageButton okBtn;

    private EditText destination;
    private EditText departure;

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
        departure = (EditText)view.findViewById(R.id.editTextDeparture);
        destination = (EditText)view.findViewById(R.id.editTextDestination);

        backBtn.setOnClickListener(this);
        swapBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButtonBackFromSearch:
                NavHostFragment.findNavController(SearchFragment.this)
                        .navigate(R.id.action_SearchFragment_to_MapFragment);
                break;
            case R.id.imageButtonSwap:
                String tmp = departure.getText().toString();
                departure.setText(destination.getText().toString());
                destination.setText(tmp);
                break;
            case R.id.imageButtonMoreVert:
                break;
        }
    }
}
