package com.xycoding.treasure.activity;

import android.os.Bundle;
import android.view.MotionEvent;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityHandwritingBinding;
import com.xycoding.treasure.view.handwriting.HandwritingView;

/**
 * Created by xuyang on 2016/12/15.
 */
public class HandwritingActivity extends BaseBindingActivity {

    private ActivityHandwritingBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_handwriting;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (ActivityHandwritingBinding) binding;
    }

    @Override
    protected void setListeners() {
        mBinding.viewHandwriting.setOnHandwritingListener(new HandwritingView.OnHandwritingListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onTouchEvent(MotionEvent event) {
                switch (event.getAction()) {
                }
            }

            @Override
            public void onClear() {

            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
}
