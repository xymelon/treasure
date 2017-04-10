package com.xycoding.treasure.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.LayoutDictHorizontalItemBinding;

/**
 * Created by xuyang on 2017/3/22.
 */
public class RecyclerViewDictHorizontalAdapter extends RecyclerView.Adapter<RecyclerViewDictHorizontalAdapter.RecyclerViewHolder> {

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dict_horizontal_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        holder.binding.tvContent.setText("精品课" + (position + 1));
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private LayoutDictHorizontalItemBinding binding;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

}
