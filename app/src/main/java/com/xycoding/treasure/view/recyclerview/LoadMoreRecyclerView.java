package com.xycoding.treasure.view.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xuyang on 2016/7/28.
 */
public class LoadMoreRecyclerView extends RecyclerView {

    private FooterRecyclerViewAdapter mWrapAdapter;
    private View mFooterView;
    private OnLoadMoreListener mMoreListener;
    private EndlessScrollListener mScrollListener;


    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mScrollListener = new EndlessScrollListener();
        addOnScrollListener(mScrollListener);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        //wrap adapter
        mWrapAdapter = new FooterRecyclerViewAdapter(this, adapter);
        adapter.registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                //reset scroll params
                mScrollListener.reset();
                //当数据量为1时，显示调用onScrolled，手动触发load more
                if (mWrapAdapter.getRealItemCount() == 1) {
                    mScrollListener.onScrolled(LoadMoreRecyclerView.this, 0, 0);
                }
            }
        });
        super.setAdapter(mWrapAdapter);
    }

    public void setLoadMoreListener(OnLoadMoreListener listener) {
        mMoreListener = listener;
    }

    public void setFooterView(@NonNull View view) {
        checkAdapter();
        mFooterView = view;
        mWrapAdapter.setFooterView(view);
    }

    private void checkAdapter() {
        if (mWrapAdapter == null) {
            throw new RuntimeException("You must invoke method setAdapter.");
        }
    }

    public class EndlessScrollListener extends RecyclerView.OnScrollListener {

        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 5;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        public void reset() {
            currentPage = 0;
            previousTotalItemCount = 0;
            loading = true;
            startingPageIndex = 0;
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            int totalItemCount = mWrapAdapter.getRealItemCount();
            int lastVisibleItemPosition = findLastVisibleItemPosition();
            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            // threshold should reflect how many total columns there are too
            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                currentPage++;
                if (mMoreListener != null) {
                    mMoreListener.onLoadMore(currentPage, totalItemCount);
                }
                loading = true;
            }
        }

        public int findLastVisibleItemPosition() {
            int lastVisibleItemPosition = 0;
            if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) getLayoutManager()).findLastVisibleItemPositions(null);
                // get maximum element within the list
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
            } else if (getLayoutManager() instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
            } else if (getLayoutManager() instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
            }
            if (mFooterView != null) {
                lastVisibleItemPosition--;
            }
            return lastVisibleItemPosition;
        }

        public int getLastVisibleItem(int[] lastVisibleItemPositions) {
            int maxSize = 0;
            for (int i = 0; i < lastVisibleItemPositions.length; i++) {
                if (i == 0) {
                    maxSize = lastVisibleItemPositions[i];
                } else if (lastVisibleItemPositions[i] > maxSize) {
                    maxSize = lastVisibleItemPositions[i];
                }
            }
            return maxSize;
        }

    }

    public interface OnLoadMoreListener {
        void onLoadMore(int page, int totalItemsCount);
    }

}
