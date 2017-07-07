package com.yu.espressotest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yu.espressotest.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-7-7.
 * adapter
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ItemHolder>{

    private LayoutInflater inflater;
    private ArrayList<String> mDataList;
    private OnItemClickedListener mListener;

    public ListAdapter(Context context, ArrayList<String> dataList) {
        inflater = LayoutInflater.from(context);
        mDataList = dataList;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = inflater.inflate(R.layout.item_list, null, false);

        return new ItemHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        holder.tvItem.setText(mDataList.get(position));
        if (mListener != null) {
            holder.tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tvItem;

        public ItemHolder(View itemView) {
            super(itemView);

            tvItem = (TextView) itemView.findViewById(R.id.tv_item);
        }
    }

    public void setOnItemClickListener(OnItemClickedListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickedListener {
        void onItemClick(int pos);
    }
}
