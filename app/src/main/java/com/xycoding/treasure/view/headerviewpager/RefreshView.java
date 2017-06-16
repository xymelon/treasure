package com.xycoding.treasure.view.headerviewpager;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.LayoutRefreshViewBinding;

/**
 * Created by xuyang on 15/11/24.
 */
public class RefreshView extends BaseRefreshView {

    private LayoutRefreshViewBinding mBinding;

    public RefreshView(Context context) {
        this(context, null);
    }

    public RefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_refresh_view, this, true);
        if (isInEditMode()) {
            return;
        }
        mBinding = DataBindingUtil.bind(view.findViewById(R.id.layout_container));
    }

    @Override
    public void releaseToLoad(boolean isLoad) {
        if (isLoad) {
            mBinding.tvLoadHint.setText("松开清空");
        } else {
            mBinding.tvLoadHint.setText("下拉清空");
        }
    }

    @Override
    public void start() {
        mBinding.tvLoadHint.setVisibility(INVISIBLE);
        mBinding.progressBar.setVisibility(VISIBLE);
    }

    @Override
    public void stop() {
        mBinding.tvLoadHint.setVisibility(VISIBLE);
        mBinding.progressBar.setVisibility(INVISIBLE);
        mBinding.tvLoadHint.setText("下拉清空");
    }

}
