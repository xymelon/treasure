package com.xycoding.treasure.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.xycoding.treasure.R;
import com.xycoding.treasure.adapter.RecyclerViewAdapter;
import com.xycoding.treasure.databinding.ActivityRecyclerViewBinding;
import com.xycoding.treasure.view.recyclerview.ItemDragCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyang on 2016/10/31.
 */
public class RecyclerViewActivity extends BaseBindingActivity {

    private ActivityRecyclerViewBinding mBinding;
    private List<String> mData = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recycler_view;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (ActivityRecyclerViewBinding) binding;
        initViews();
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        for (int i = 1; i <= 20; i++) {
            mData.add("Item " + i);
        }
        mBinding.recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void initViews() {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mData);
        mBinding.recyclerView.setAdapter(adapter);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //drag sort
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemDragCallBack(adapter));
        adapter.setDragListener(new RecyclerViewAdapter.OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        });
        itemTouchHelper.attachToRecyclerView(mBinding.recyclerView);
    }
}
