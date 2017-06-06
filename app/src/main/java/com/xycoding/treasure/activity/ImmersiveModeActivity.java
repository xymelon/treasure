package com.xycoding.treasure.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityImmersiveModeBinding;
import com.xycoding.treasure.utils.DeviceUtils;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by xymelon on 2017/6/2.
 */
public class ImmersiveModeActivity extends BaseBindingActivity {

    private Animation mFadeInAnimation;
    private Animation mFadeOutAnimation;
    private ActivityImmersiveModeBinding mBinding;

    @Override
    public int getLayoutId() {
        return R.layout.activity_immersive_mode;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mBinding = (ActivityImmersiveModeBinding) binding;
        initViews();
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    private void initViews() {
        mFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        mFadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        Glide.with(ImmersiveModeActivity.this)
                .load("http://oimagec6.ydstatic.com/image?url=http://www.make4fun.com/download/wppstore2/25740.jpg&product=PICDICT_EDIT")
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mBinding.photoView);
        mBinding.photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                toggleView();
            }
        });
    }

    private void toggleView() {
        DeviceUtils.toggleHideyBar(getWindow());
        boolean shown = mBinding.toolbar.isShown();
        mBinding.toolbar.startAnimation(shown ? mFadeOutAnimation : mFadeInAnimation);
        mBinding.toolbar.setVisibility(shown ? View.INVISIBLE : View.VISIBLE);
    }

}
