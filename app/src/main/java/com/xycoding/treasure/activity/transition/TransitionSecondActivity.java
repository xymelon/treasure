package com.xycoding.treasure.activity.transition;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.xycoding.treasure.R;
import com.xycoding.treasure.activity.BaseBindingActivity;
import com.xycoding.treasure.databinding.ActivityTransitionSecondBinding;

/**
 * Created by xymelon on 2018/2/6.
 */
public class TransitionSecondActivity extends BaseBindingActivity<ActivityTransitionSecondBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transition_second;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {

    }

    @Override
    protected void setListeners() {
        mBinding.ivTest.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
}
