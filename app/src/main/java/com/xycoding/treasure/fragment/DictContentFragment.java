package com.xycoding.treasure.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xycoding.treasure.R;
import com.xycoding.treasure.adapter.RecyclerViewDictAdapter;
import com.xycoding.treasure.databinding.FragmentDictContentBinding;
import com.xycoding.treasure.view.headerviewpager.HeaderViewPager;
import com.xycoding.treasure.view.recyclerview.LinearDividerItemDecoration;

/**
 * Created by xuyang on 2017/3/24.
 */
public class DictContentFragment extends BaseBindingFragment implements HeaderViewPager.ScrollableContainer {

    private final static String BUNDLE_KEY_ITEMS = "bundle_key_items";
    private final static String BUNDLE_KEY_HAS_CLASS = "bundle_key_has_class";
    private FragmentDictContentBinding mBinding;

    public static DictContentFragment createInstance(int items, boolean hasClass) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_ITEMS, items);
        bundle.putBoolean(BUNDLE_KEY_HAS_CLASS, hasClass);
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
        mBinding.recyclerView.setAdapter(new RecyclerViewDictAdapter(
                getArguments().getInt(BUNDLE_KEY_ITEMS), getArguments().getBoolean(BUNDLE_KEY_HAS_CLASS)));
        mBinding.recyclerView.addItemDecoration(new LinearDividerItemDecoration(getContext()));
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public RecyclerView getScrollableView() {
        return mBinding.recyclerView;
    }

}