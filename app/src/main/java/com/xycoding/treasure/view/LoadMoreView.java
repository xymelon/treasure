package com.xycoding.treasure.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.LayoutLoadMoreBinding;
import com.xycoding.treasure.rx.RxViewWrapper;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 布局：加载更多
 * <p>
 * Created by xuyang on 2016/7/29.
 */
public class LoadMoreView extends FrameLayout {

    private LayoutLoadMoreBinding mBinding;
    private Disposable mDisposable;

    public LoadMoreView(Context context) {
        this(context, null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        setVisibility(GONE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_load_more, this, true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public void showLoadingView() {
        setVisibility(VISIBLE);
        mBinding.tvLoading.setVisibility(VISIBLE);
        mBinding.tvLoading.postDelayed(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = mBinding.tvLoading.getCompoundDrawables()[0];
                if (drawable instanceof AnimationDrawable) {
                    ((AnimationDrawable) drawable).start();
                }
            }
        }, 200);
        mBinding.tvNoMore.setVisibility(INVISIBLE);
        mBinding.tvReload.setVisibility(INVISIBLE);
    }

    public void showNoMoreView() {
        setVisibility(VISIBLE);
        mBinding.tvNoMore.setVisibility(VISIBLE);
        mBinding.tvLoading.setVisibility(INVISIBLE);
        mBinding.tvReload.setVisibility(INVISIBLE);
    }

    public void showReLoadView() {
        setVisibility(VISIBLE);
        mBinding.tvReload.setVisibility(VISIBLE);
        mBinding.tvNoMore.setVisibility(INVISIBLE);
        mBinding.tvLoading.setVisibility(INVISIBLE);
    }

    public void setReloadClickListener(final OnClickListener listener) {
        mDisposable = RxViewWrapper.clicks(mBinding.tvReload)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        listener.onClick(mBinding.tvReload);
                    }
                });
    }

}
