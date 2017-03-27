package com.xycoding.treasure.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.FragmentDictResultBinding;

/**
 * Created by xuyang on 2017/3/22.
 */
public class DictResultFragment extends BaseBindingFragment {

    private FragmentDictResultBinding mBinding;
    private FragmentPagerAdapter mPagerAdapter;

    public static DictResultFragment createInstance() {
        return new DictResultFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dict_result;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (FragmentDictResultBinding) binding;
        initViews();
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    private void initViews() {
        mPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 1) {
                    return DictContentFragment.createInstance(2);
                }
                return DictContentFragment.createInstance(50);
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 1) {
                    return "朗文";
                }
                return "详解";
            }
        };
        mBinding.viewPager.setAdapter(mPagerAdapter);
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab());
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab());
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
    }

}
