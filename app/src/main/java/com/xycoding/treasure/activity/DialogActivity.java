package com.xycoding.treasure.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityDialogBinding;
import com.xycoding.treasure.databinding.LayoutSheetDialogBinding;
import com.xycoding.treasure.rx.RxViewWrapper;
import com.xycoding.treasure.utils.ViewUtils;

import rx.functions.Action1;

/**
 * Created by xuyang on 2016/10/28.
 */
public class DialogActivity extends BaseBindingActivity {

    private ActivityDialogBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dialog;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (ActivityDialogBinding) binding;
    }

    @Override
    protected void setListeners() {
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewBottomSheet).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showBottomSheetDialog();
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewCustomSheet).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showCustomDialog();
            }
        }));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    private void showBottomSheetDialog() {
        LayoutSheetDialogBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.layout_sheet_dialog, null, false);
        BottomSheetDialog sheetDialog = new BottomSheetDialog(this);
        sheetDialog.setContentView(binding.getRoot());
        sheetDialog.show();
    }

    private void showCustomDialog() {
        LayoutSheetDialogBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.layout_sheet_dialog, null, false);
        ViewUtils.createSheetDialog(this, binding.getRoot()).show();
    }
}
