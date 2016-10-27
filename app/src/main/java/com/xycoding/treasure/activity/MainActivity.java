package com.xycoding.treasure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityMainBinding;
import com.xycoding.treasure.rx.RxViewWrapper;

import rx.functions.Action1;

public class MainActivity extends BaseBindingActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (ActivityMainBinding) binding;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    protected void setListeners() {
        subscriptions.add(RxViewWrapper.clicks(mBinding.fab).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Snackbar.make(mBinding.fab, "Replace with your own action", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewCollapsingToolbar).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(MainActivity.this, CollapsingToolbarActivity.class));
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewDialog).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

            }
        }));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
