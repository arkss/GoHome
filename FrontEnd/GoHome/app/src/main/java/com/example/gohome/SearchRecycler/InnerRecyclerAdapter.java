package com.example.gohome.SearchRecycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gohome.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Search inner recycler adapter 정의
public class InnerRecyclerAdapter extends RecyclerView.Adapter<InnerRecyclerAdapter.InnerViewHolder> {

    private ArrayList<InnerData> mData = null; // recycler view 에서 사용하는 data list, 각 item에 대한 정보

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class InnerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public InnerViewHolder(View item) {
            super(item);

            imageView = item.findViewById(R.id.trans_img);
            textView = item.findViewById(R.id.trans_name);
        }
    }

    // 외부에서 아이템에 입력할 데이터를 입력받음
    public InnerRecyclerAdapter(ArrayList<InnerData> list) {
        mData = list;
    }

    /* Create new views (invoked by the layout manager) */
    @NonNull
    @Override
    public InnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.search_item_inner, parent, false);
        InnerRecyclerAdapter.InnerViewHolder viewHolder = new InnerRecyclerAdapter.InnerViewHolder(view);
        return viewHolder;
    }

    /* Replace the contents of a view (invoked by the layout manager) */
    @Override
    public void onBindViewHolder(@NonNull InnerViewHolder holder, int position) {
        // - get element from your dataset at this position
        int img = mData.get(position).getImgResource();
        String name = mData.get(position).getTransName();

        // - replace the contents of the view with that element
        holder.imageView.setImageResource(img);
        holder.textView.setText(name);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }
}
