package com.example.gohome.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gohome.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/* search_item 레이아웃을 리스트 형식으로 보여주기 위한 어댑터 클래스 */
public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SearchData> sData = null; // 시간, 등을 표시하는데 필요한 데이터

    // inner recycler view에서 사용하는 데이터. 정류장 위치, 이미지 등을 포함
    private ArrayList<ArrayList<InnerData>> sInData = null;
    private Fragment fragment;

    // 아이템 뷰를 저장하는 뷰 홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView; // search_item의 textview
        RecyclerView innerRecyclerView; // search_item의 recycler view

        public ViewHolder(View item, Fragment fragment) {
            super(item);

            // Item click event 처리
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition(); // 클린된 item의 position
                    NavHostFragment.findNavController(fragment)
                            .navigate(R.id.action_SearchFragment_to_RouteFragment);
                }
            });

            textView = item.findViewById(R.id.required_time);
            innerRecyclerView = item.findViewById(R.id.search_inner_recycler);
        }
    }

    // 데이터를 전달받음
    public SearchRecyclerAdapter(Context context, ArrayList<SearchData> sList, ArrayList<ArrayList<InnerData>> sInList, Fragment fragment) {
        this.context = context;
        this.sData = sList;
        this.sInData = sInList;
        this.fragment = fragment;
    }

    /* view holder를 생성하고, 반환하는 매서드 */
    @Override
    public SearchRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.search_item, parent, false);
        SearchRecyclerAdapter.ViewHolder viewHolder = new SearchRecyclerAdapter.ViewHolder(view, fragment);

        return viewHolder;
    }

    /* position 위치의 아이템(View) 설정 */
    @Override
    public void onBindViewHolder(SearchRecyclerAdapter.ViewHolder holder, int position) {
        String time = sData.get(position).getTime();
        InnerRecyclerAdapter innerAdapter = new InnerRecyclerAdapter(sInData.get(position));

        holder.textView.setText(time);
        holder.innerRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.innerRecyclerView.setAdapter(innerAdapter);
    }

    /* 리턴값에 따라 표시할 수 있는 갯수가 정해진다 */
    @Override
    public int getItemCount() {
        return Math.min(sData.size(), sInData.size());
    }
}