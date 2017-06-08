package com.xycoding.treasure.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.util.Pools;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.LayoutQuickPositioningBinding;
import com.xycoding.treasure.databinding.LayoutQuickPositioningItemBinding;
import com.xycoding.treasure.utils.DeviceUtils;

import java.util.List;

/**
 * Created by xymelon on 2017/6/8.
 */
public class QuickPositioningDialog {

    private final Pools.Pool<LayoutQuickPositioningItemBinding> mTextViewPools = new Pools.SimplePool<>(5);
    private LayoutQuickPositioningBinding mBinding;
    private Dialog mDialog;
    private int mPadding;
    private int mItemHeight;
    private OnQuickClickListener mClickListener;

    public QuickPositioningDialog(Context context) {
        initViews(context);
    }

    private void initViews(Context context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_quick_positioning, null, false);
        mPadding = DeviceUtils.dp2px(context, 8);
        mItemHeight = DeviceUtils.dp2px(context, 36);
        mBinding.getRoot().setPadding(0, mPadding, 0, mPadding);
        mDialog = new AlertDialog
                .Builder(context, R.style.Dialog_NoTitleAndTransparent_SlideRight)
                .setView(mBinding.getRoot())
                .create();
        if (mDialog.getWindow() != null) {
            mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
            layoutParams.width = DeviceUtils.dp2px(context, 112);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mDialog.getWindow().setAttributes(layoutParams);
        }
    }

    public void show(@NonNull List<String> data, int centerYInScreen, int startYInScreen, int endYInScreen) {
        renderData(data);
        if (mDialog.getWindow() != null) {
            final int halfHeight = (mItemHeight * data.size() + mPadding * 2) / 2;
            //scroll bar上方剩余空间
            final int topLeft = centerYInScreen - startYInScreen;
            //scroll bar下方剩余空间
            final int bottomLeft = endYInScreen - centerYInScreen;
            if (halfHeight <= topLeft && halfHeight <= bottomLeft) {
                //对话框中心与scroll bar对齐
                mDialog.getWindow().getAttributes().y = centerYInScreen - halfHeight;
            } else if (halfHeight > topLeft) {
                //对话框顶部与起始位置对齐
                mDialog.getWindow().getAttributes().y = startYInScreen;
            } else {
                //对话框底部与结束位置对齐
                mDialog.getWindow().getAttributes().y = endYInScreen - halfHeight * 2;
            }
        }
        mDialog.show();
    }

    public void setOnQuickClickListener(OnQuickClickListener listener) {
        mClickListener = listener;
    }

    private void dispatchClickListener(int position) {
        if (mClickListener != null) {
            mClickListener.onClick(position);
        }
        mDialog.dismiss();
    }

    private void renderData(List<String> data) {
        final int count = mBinding.layoutQuickPositioning.getChildCount();
        for (int i = 0; i < count; i++) {
            Object tag = mBinding.layoutQuickPositioning.getChildAt(i).getTag();
            if (tag instanceof LayoutQuickPositioningItemBinding) {
                mTextViewPools.release((LayoutQuickPositioningItemBinding) tag);
            }
        }
        mBinding.layoutQuickPositioning.removeAllViews();
        for (int i = 0; i < data.size(); i++) {
            LayoutQuickPositioningItemBinding binding = newItemBinding();
            binding.tvDict.setText(data.get(i));
            final int finalI = i;
            binding.tvDict.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchClickListener(finalI);
                }
            });
            mBinding.layoutQuickPositioning.addView(binding.getRoot());
        }
    }

    private LayoutQuickPositioningItemBinding newItemBinding() {
        LayoutQuickPositioningItemBinding binding = mTextViewPools.acquire();
        if (binding == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.getRoot().getContext()),
                    R.layout.layout_quick_positioning_item, mBinding.layoutQuickPositioning, false);
            binding.getRoot().setTag(binding);
        }
        return binding;
    }

    public interface OnQuickClickListener {
        void onClick(int position);
    }

}
