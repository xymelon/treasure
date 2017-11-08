package com.xycoding.treasure.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityMainBinding;
import com.xycoding.treasure.rx.RxViewWrapper;
import com.xycoding.treasure.service.LocalIntentService;

import io.reactivex.functions.Consumer;

public class MainActivity extends BaseBindingActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

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
        mDisposables.add(RxViewWrapper.clicks(mBinding.fab).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Snackbar.make(mBinding.fab, "Replace with your own action", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewCollapsingToolbar).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                startActivity(new Intent(MainActivity.this, CollapsingToolbarActivity.class));
            }
        }));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewDialog).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                startActivity(new Intent(MainActivity.this, DialogActivity.class));
            }
        }));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewCustom).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                startActivity(new Intent(MainActivity.this, ViewActivity.class));
            }
        }));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewMode).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                startActivity(new Intent(MainActivity.this, ActionModeActivity.class));
            }
        }));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewHandwriting).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                startActivity(new Intent(MainActivity.this, HandwritingActivity.class));
            }
        }));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewSpeech).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                startActivity(new Intent(MainActivity.this, SpeechActivity.class));
            }
        }));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewDict).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                startActivity(new Intent(MainActivity.this, DictActivity.class));
            }
        }));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewChart).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                startActivity(new Intent(MainActivity.this, ChartActivity.class));
            }
        }));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewImmersiveMode).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                startActivity(new Intent(MainActivity.this, ImmersiveModeActivity.class));
            }
        }));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        startService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Toast.makeText(this, "我没打开新页面哦", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onScreenshot(Uri uri) {
        Snackbar.make(mBinding.getRoot(), "截屏：" + uri, Snackbar.LENGTH_INDEFINITE)
                .setAction("好的", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }

    private Handler mHandler = new Handler();

    private void startService() {
        final Intent service = new Intent(this, LocalIntentService.class);
        service.putExtra(LocalIntentService.BUNDLE_KEY_TASK, "task1");
        startService(service);
        service.putExtra(LocalIntentService.BUNDLE_KEY_TASK, "task2");
        startService(service);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                service.putExtra(LocalIntentService.BUNDLE_KEY_TASK, "task3");
                startService(service);
            }
        }, 5000);
    }

}
