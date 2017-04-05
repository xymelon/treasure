package com.xycoding.treasure.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.xycoding.treasure.R;
import com.xycoding.treasure.adapter.RecyclerViewDictAdapter;
import com.xycoding.treasure.databinding.FragmentDictContentBinding;
import com.xycoding.treasure.view.HeaderViewPager;
import com.xycoding.treasure.view.recyclerview.LinearDividerItemDecoration;

/**
 * Created by xuyang on 2017/3/24.
 */
public class DictContentFragment extends BaseBindingFragment implements HeaderViewPager.ScrollableContainer {

    private final static String BUNDLE_KEY_ITEMS = "bundle_key_items";
    private FragmentDictContentBinding mBinding;

    public static DictContentFragment createInstance(int items) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_ITEMS, items);
        DictContentFragment fragment = new DictContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dict_content;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (FragmentDictContentBinding) binding;
        initViews();
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    private void initViews() {
        mBinding.recyclerView.setAdapter(new RecyclerViewDictAdapter(getArguments().getInt(BUNDLE_KEY_ITEMS)));
        mBinding.recyclerView.addItemDecoration(new LinearDividerItemDecoration(getContext()));
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public View getScrollableView() {
        return mBinding.recyclerView;
    }

}