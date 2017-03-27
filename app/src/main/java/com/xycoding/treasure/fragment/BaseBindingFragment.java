package com.xycoding.treasure.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by xuyang on 2016/7/21
 */
public abstract class BaseBindingFragment extends Fragment {

    protected ViewDataBinding binding;
    protected boolean isViewCreated;
    protected CompositeSubscription subscriptions = new CompositeSubscription();

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binding.getRoot();
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        initControls(savedInstanceState);
        setListeners();
        initData(savedInstanceState);
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        subscriptions.unsubscribe();
    }

    /**
     * get layout id for fragment
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * init views
     *
     * @return
     */
    protected abstract void initControls(Bundle savedInstanceState);

    /**
     * set listener for fragment views
     */
    protected abstract void setListeners();

    /**
     * init data
     */
    protected abstract void initData(Bundle savedInstanceState);
}
