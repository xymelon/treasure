package com.xycoding.treasure.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.LayoutDictHorizontalBinding;
import com.xycoding.treasure.databinding.LayoutDictVerticalBinding;

/**
 * Created by xuyang on 2017/3/22.
 */
public class RecyclerViewDictAdapter extends RecyclerView.Adapter<RecyclerViewDictAdapter.RecyclerViewHolder> {

    private static final int ITEM_TYPE_CLASS = 1024;
    private static final int ITEM_TYPE_DICT = 1025;

    private int mItems;
    private boolean mHasClass;

    public RecyclerViewDictAdapter(int items, boolean hasClass) {
        mItems = items;
        mHasClass = hasClass;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CLASS) {
            return new RecyclerViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dict_horizontal, parent, false));
        }
        return new RecyclerViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dict_vertical, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(null);
        switch (holder.getItemViewType()) {
            case ITEM_TYPE_DICT:
                ((LayoutDictVerticalBinding) holder.binding).tvTitle.setText("21世纪英汉词典" + (position + 1));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(holder.itemView.getContext(), "item " + position, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case ITEM_TYPE_CLASS:
                RecyclerView recyclerView = ((LayoutDictHorizontalBinding) holder.binding).recyclerViewHorizontal;
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                        holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(new RecyclerViewDictHorizontalAdapter());
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mHasClass) {
            return mItems + 1;
        }
        return mItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHasClass && position == mItems % 3) {
            return ITEM_TYPE_CLASS;
        }
        return ITEM_TYPE_DICT;
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

}
