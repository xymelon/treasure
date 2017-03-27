package com.xycoding.treasure.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.LayoutDictItemBinding;

/**
 * Created by xuyang on 2017/3/22.
 */
public class RecyclerViewDictAdapter extends RecyclerView.Adapter<RecyclerViewDictAdapter.RecyclerViewHolder> {

    private int mItems;

    public RecyclerViewDictAdapter(int items) {
        mItems = items;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dict_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mItems;
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private LayoutDictItemBinding binding;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

}
