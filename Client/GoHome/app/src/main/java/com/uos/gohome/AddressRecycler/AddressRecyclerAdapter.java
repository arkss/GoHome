package com.uos.gohome.AddressRecycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uos.gohome.R;
import com.uos.gohome.SearchRecycler.InnerData;
import com.uos.gohome.SearchRecycler.InnerRecyclerAdapter;
import com.uos.gohome.SearchRecycler.SearchData;
import com.uos.gohome.main.SearchFragment;

import java.util.ArrayList;


/* search_item 레이아웃을 리스트 형식으로 보여주기 위한 어댑터 클래스 */
public class AddressRecyclerAdapter extends RecyclerView.Adapter<AddressRecyclerAdapter.MyViewHolder> {
    public interface OnListItemSelectedInterface {
        void onItemSelected(View v, int position);
    }

    private ArrayList<AddressData> mDataset;
    static OnListItemSelectedInterface mListener;
    Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public AddressData mData;
        public MyViewHolder(View v) {
            super(v);
            textView = (TextView)v.findViewById(R.id.address_text_view);

            // 클릭하면 실행할 것 정의
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemSelected(v, getAdapterPosition());
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AddressRecyclerAdapter(ArrayList<AddressData> mDataset,
                                  Context context,
                                  OnListItemSelectedInterface listener) {
        this.mDataset = mDataset;
        this.mContext = context;
        this.mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AddressRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        AddressData mData = mDataset.get(position);
        holder.textView.setText(mData.getAddress() + " " + mData.getAddressDetail());
        holder.mData = mData;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d("AddressRecyclerAdapter", Integer.toString(mDataset.size()));
        return mDataset.size();
    }
}