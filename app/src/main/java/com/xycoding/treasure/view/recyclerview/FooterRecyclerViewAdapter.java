package com.xycoding.treasure.view.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xuyang on 2016/7/28.
 */
public class FooterRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_FOOTER = Integer.MIN_VALUE;

    private RecyclerView mRecyclerView;

    private View mFooterView;

    private RecyclerView.Adapter<RecyclerView.ViewHolder> mRealAdapter;

    public FooterRecyclerViewAdapter(@NonNull RecyclerView recyclerView,
                                     @NonNull RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        mRecyclerView = recyclerView;
        mRealAdapter = adapter;
        mRealAdapter.registerAdapterDataObserver(
                new RecyclerView.AdapterDataObserver() {

                    @Override
                    public void onChanged() {
                        super.onChanged();
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {
                        super.onItemRangeChanged(positionStart, itemCount);
                        notifyItemRangeChanged(positionStart, itemCount);
                    }

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        notifyItemRangeInserted(positionStart, itemCount);
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        super.onItemRangeRemoved(positionStart, itemCount);
                        notifyItemRangeRemoved(positionStart, itemCount);
                    }

                });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FOOTER) {
            ViewHolder holder = new ViewHolder(mFooterView);
            if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);
                holder.itemView.setLayoutParams(layoutParams);
            }
            return holder;
        }
        return mRealAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) != VIEW_TYPE_FOOTER) {
            mRealAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        int count = mRealAdapter.getItemCount();
        if (count == 0) {
            return 0;
        }
        //add footer
        return mFooterView == null ? count : count + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterView != null && position == mRealAdapter.getItemCount()) {
            return VIEW_TYPE_FOOTER;
        }
        return mRealAdapter.getItemViewType(position);
    }

    public int getRealItemCount() {
        return mRealAdapter.getItemCount();
    }

    public void removeFooterView() {
        mFooterView = null;
    }

    public void setFooterView(@NonNull View view) {
        mFooterView = view;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
