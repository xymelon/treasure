package com.xycoding.treasure.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.xycoding.treasure.R;
import com.xycoding.treasure.adapter.RecyclerViewAdapter;
import com.xycoding.treasure.databinding.ActivityRecyclerViewBinding;
import com.xycoding.treasure.rx.RxViewWrapper;
import com.xycoding.treasure.view.LoadMoreView;
import com.xycoding.treasure.view.recyclerview.GridBottomDividerItemDecoration;
import com.xycoding.treasure.view.recyclerview.ItemDragCallBack;
import com.xycoding.treasure.view.recyclerview.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;

/**
 * Created by xuyang on 2016/10/31.
 */
public class RecyclerViewActivity extends BaseBindingActivity {

    private ActivityRecyclerViewBinding mBinding;
    private List<String> mData = new ArrayList<>();
    private LoadMoreView mLoadMoreView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recycler_view;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (ActivityRecyclerViewBinding) binding;
        initLinearRecyclerView();
        initGridRecyclerView();
        switchRecyclerView();
    }

    @Override
    protected void setListeners() {
        mLoadMoreView.setReloadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreItems();
            }
        });
        mBinding.recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreItems();
            }
        });
        subscriptions.add(RxViewWrapper.clicks(mBinding.btnSwitch).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                switchRecyclerView();
            }
        }));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        addDummyItems();
    }

    private void initLinearRecyclerView() {
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(mData);
        mBinding.recyclerView.setAdapter(adapter);
        mLoadMoreView = new LoadMoreView(this);
        mBinding.recyclerView.setFooterView(mLoadMoreView);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //drag sort
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemDragCallBack(adapter));
        adapter.setDragListener(new RecyclerViewAdapter.OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        });
        adapter.setRemoveListener(new RecyclerViewAdapter.OnItemRemoveListener() {
            @Override
            public void onItemRemove(final int position, final String string) {
                Snackbar.make(mBinding.recyclerView, "删除 " + string, Snackbar.LENGTH_LONG)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mData.add(position, string);
                                adapter.notifyItemInserted(position);
                            }
                        }).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(mBinding.recyclerView);
    }

    private void initGridRecyclerView() {
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(loadMoreDummyItems(5));
        mBinding.recyclerViewGrid.setAdapter(adapter);
        int columns = 3;
        mBinding.recyclerViewGrid.setLayoutManager(new GridLayoutManager(this, columns));
        mBinding.recyclerViewGrid.addItemDecoration(
                new GridBottomDividerItemDecoration(
                        ContextCompat.getDrawable(this, R.drawable.shape_grid_divider),
                        columns));
    }

    private void switchRecyclerView() {
        if (mBinding.recyclerView.getVisibility() != View.VISIBLE) {
            mBinding.recyclerView.setVisibility(View.VISIBLE);
            mBinding.recyclerViewGrid.setVisibility(View.INVISIBLE);
        } else {
            mBinding.recyclerView.setVisibility(View.INVISIBLE);
            mBinding.recyclerViewGrid.setVisibility(View.VISIBLE);
        }
    }

    private void addDummyItems() {
        for (int i = 1; i <= 20; i++) {
            mData.add("Item " + i);
        }
        mBinding.recyclerView.getAdapter().notifyDataSetChanged();
    }

    private List<String> loadMoreDummyItems(int num) {
        List<String> items = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            items.add("Item " + (mData.size() + i));
        }
        return items;
    }

    private void loadMoreItems() {
        subscriptions.add(Observable
                .defer(new Func0<Observable<List<String>>>() {
                    @Override
                    public Observable<List<String>> call() {
                        return Observable.just(loadMoreDummyItems(20));
                    }
                })
                .delay(3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {

                    @Override
                    public void onStart() {
                        mLoadMoreView.showLoadingView();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mLoadMoreView.showReLoadView();
                    }

                    @Override
                    public void onNext(List<String> strings) {
                        if (strings == null || strings.size() == 0) {
                            //no more result
                            mLoadMoreView.showNoMoreView();
                        } else {
                            int startPos = mData.size();
                            mData.addAll(strings);
                            mBinding.recyclerView.getAdapter().notifyItemRangeInserted(startPos, strings.size());
                        }
                    }
                }));
    }
}
