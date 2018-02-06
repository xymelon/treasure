package com.xycoding.treasure.view.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

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

                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                        super.onItemRangeChanged(positionStart, itemCount, payload);
                        notifyItemRangeChanged(positionStart, itemCount, payload);
                    }

                    @Override
                    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                        super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                        notifyItemMoved(fromPosition, toPosition);
                    }
                });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FOOTER) {
            FooterViewHolder holder = new FooterViewHolder(mFooterView);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (getItemViewType(position) != VIEW_TYPE_FOOTER) {
            mRealAdapter.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (!(holder instanceof FooterViewHolder)) {
            mRealAdapter.onViewRecycled(holder);
        }
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        if (!(holder instanceof FooterViewHolder)) {
            return mRealAdapter.onFailedToRecycleView(holder);
        }
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (!(holder instanceof FooterViewHolder)) {
            mRealAdapter.onViewAttachedToWindow(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (!(holder instanceof FooterViewHolder)) {
            mRealAdapter.onViewDetachedFromWindow(holder);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRealAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mRealAdapter.onDetachedFromRecyclerView(recyclerView);
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

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
