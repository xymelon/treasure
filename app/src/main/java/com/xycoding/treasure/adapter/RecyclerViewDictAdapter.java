package com.xycoding.treasure.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        holder.binding.tvTitle.setText("21世纪英汉词典" + (position + 1));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.itemView.getContext(), "item " + position, Toast.LENGTH_SHORT).show();
            }
        });
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
