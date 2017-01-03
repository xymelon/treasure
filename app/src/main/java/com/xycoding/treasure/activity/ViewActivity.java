package com.xycoding.treasure.activity;

import android.content.Intent;
import android.os.Bundle;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityViewBinding;
import com.xycoding.treasure.rx.RxViewWrapper;

import rx.functions.Action1;

/**
 * Created by xuyang on 2016/10/28.
 */
public class ViewActivity extends BaseBindingActivity {

    private ActivityViewBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_view;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (ActivityViewBinding) binding;
        initViews();
    }

    @Override
    protected void setListeners() {
        subscriptions.add(RxTextView.afterTextChangeEvents(mBinding.clearEditText).subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent event) {
                mBinding.fitTextView.setText(event.editable());
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.btnRecyclerView).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(ViewActivity.this, RecyclerViewActivity.class));
            }
        }));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mBinding.clearEditText.setText("输入：TextView自适应");
        mBinding.autoEditText.setText("输入：EditText自适应");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mBinding.autoEditText.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mBinding.autoEditText.requestFocus();
//                KeyboardUtils.showSoftKeyBoard(ViewActivity.this, mBinding.autoEditText);
//            }
//        }, 500);
    }

    private void initViews() {
        mBinding.autoEditText.shouldBlinkOnMeiZu(true);
    }
}
