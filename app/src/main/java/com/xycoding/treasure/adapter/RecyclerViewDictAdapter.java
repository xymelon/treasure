package com.xycoding.treasure.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.xycoding.treasure.docker.DictDockerManager;

/**
 * Created by xuyang on 2017/3/22.
 */
public class RecyclerViewDictAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int mItems;
    private boolean mHasClass;

    public RecyclerViewDictAdapter(int items, boolean hasClass) {
        mItems = items;
        mHasClass = hasClass;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return DictDockerManager.createViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        DictDockerManager.bindViewHolder(holder, null, position);
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
            return DictDockerManager.VIEW_TYPE_EC;
        }
        return DictDockerManager.VIEW_TYPE_EE;
    }

}
