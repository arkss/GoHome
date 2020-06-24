package com.uos.gohome.RouteRecycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uos.gohome.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RouteRecyclerAdapter extends RecyclerView.Adapter<RouteRecyclerAdapter.RViewHolder> {

    private ArrayList<RouteData> mData = null; // recycler view 에서 사용하는 data list, 각 item에 대한 정보

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class RViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textStationStart;
        TextView textStationEnd;

        public RViewHolder(View item) {
            super(item);

            imageView = item.findViewById(R.id.route_img);
            textStationStart = item.findViewById(R.id.route_station_start);
            textStationEnd = item.findViewById(R.id.route_station_end);
        }
    }

    // 외부에서 아이템에 입력할 데이터를 입력받음
    public RouteRecyclerAdapter(ArrayList<RouteData> list) {
        mData = list;
    }

    /* Create new views (invoked by the layout manager) */
    @NonNull
    @Override
    public RouteRecyclerAdapter.RViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.route_item, parent, false);
        RouteRecyclerAdapter.RViewHolder viewHolder = new RouteRecyclerAdapter.RViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RViewHolder holder, int position) {
        // - get element from your dataset at this position
        int img = mData.get(position).getImg();
        String stationStart = mData.get(position).getStationStart();
        String stationEnd = mData.get(position).getStationEnd();

        // - replace the contents of the view with that element
        Log.d("TEST", "img = "+img);
        holder.imageView.setImageResource(img);
        holder.textStationStart.setText(stationStart);
        holder.textStationEnd.setText(stationEnd);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }
}