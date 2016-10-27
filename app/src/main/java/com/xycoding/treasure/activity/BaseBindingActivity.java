package com.xycoding.treasure.activity;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.xycoding.treasure.R;
import com.xycoding.treasure.utils.ViewUtils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by xuyang on 2016/7/21.
 */
public abstract class BaseBindingActivity extends AppCompatActivity {

    private Dialog loadingDialog;
    protected ViewDataBinding binding;
    protected CompositeSubscription subscriptions = new CompositeSubscription();

    @CallSuper
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getLayoutId();

        if (layoutId != -1) {
            binding = DataBindingUtil.setContentView(this, layoutId);
        }
        if (findViewById(R.id.toolbar) != null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                if (hideTitle()) {
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                }
            }
        }
        initControls(savedInstanceState);
        setListeners();
        initData(savedInstanceState);
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }

    /**
     * hide title
     *
     * @return true : hide
     */
    protected boolean hideTitle() {
        return false;
    }

    protected void showLoadingDialog() {
        dismissLoadingDialog();
        loadingDialog = ViewUtils.createLoadingDialog(this);
        loadingDialog.show();
    }

    protected void showLoadingDialog(boolean cancelable) {
        dismissLoadingDialog();
        loadingDialog = ViewUtils.createLoadingDialog(this, cancelable);
        loadingDialog.show();
    }

    protected void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    /**
     * get layout id for activity
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * init views
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
