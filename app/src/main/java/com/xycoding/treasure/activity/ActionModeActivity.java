package com.xycoding.treasure.activity;

import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityActionModeBinding;
import com.xycoding.treasure.rx.RxViewWrapper;

import rx.functions.Action1;

/**
 * Created by xuyang on 2016/11/3.
 */
public class ActionModeActivity extends BaseBindingActivity {

    private ActivityActionModeBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_action_mode;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (ActivityActionModeBinding) binding;
    }

    @Override
    protected void setListeners() {
        subscriptions.add(RxViewWrapper.clicks(mBinding.btnAction).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        getMenuInflater().inflate(R.menu.menu_action, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        mode.finish();
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                });
            }
        }));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
}
