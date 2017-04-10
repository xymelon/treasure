package com.xycoding.treasure.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.FragmentDictResultBinding;
import com.xycoding.treasure.rx.RxViewWrapper;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by xuyang on 2017/3/22.
 */
public class DictResultFragment extends BaseBindingFragment {

    private FragmentDictResultBinding mBinding;
    private FragmentPagerAdapter mPagerAdapter;
    public List<DictContentFragment> mFragments;

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
        RxViewWrapper.clicks(mBinding.layoutHeader.fitTextView).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Toast.makeText(getContext(), mBinding.layoutHeader.fitTextView.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        RxViewWrapper.clicks(mBinding.fab).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mBinding.headerViewPager.scrollToTop();
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    private void initViews() {
        mFragments = new ArrayList<>();
        mFragments.add(DictContentFragment.createInstance(20, true));
        mFragments.add(DictContentFragment.createInstance(2, false));
        mFragments.add(DictContentFragment.createInstance(10, false));

        mPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {

            public String[] titles = new String[]{"详解", "朗文", "柯林斯"};

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        };
        mBinding.viewPager.setAdapter(mPagerAdapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);

        mBinding.headerViewPager.setCurrentScrollableContainer(mFragments.get(0));
        mBinding.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mBinding.headerViewPager.setCurrentScrollableContainer(mFragments.get(position));
            }
        });
    }

}
