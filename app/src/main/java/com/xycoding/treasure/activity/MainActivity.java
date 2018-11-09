package com.xycoding.treasure.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.xycoding.treasure.BuildConfig;
import com.xycoding.treasure.R;
import com.xycoding.treasure.activity.layout.LayoutTextViewActivity;
import com.xycoding.treasure.activity.transition.TransitionActivity;
import com.xycoding.treasure.databinding.ActivityMainBinding;
import com.xycoding.treasure.rx.RxViewWrapper;
import com.xycoding.treasure.service.LocalIntentService;
import com.xycoding.treasure.utils.YoudaoLanguageUtil;

import java.lang.reflect.Field;

public class MainActivity extends BaseBindingActivity<ActivityMainBinding> {

    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        mBinding.tvFlavor.setText("flavor_" + BuildConfig.FLAVOR);
//        YoudaoLanguageUtil.languageChineseUtil(this);
//        YoudaoLanguageUtil.languageEnglishUtil(this);
//        YoudaoLanguageUtil.convertTransLanguage(this);
//        YoudaoLanguageUtil.languageCreateEnum(this);
//        YoudaoLanguageUtil.convertLanguages(this);
        YoudaoLanguageUtil.createEnum(this);

        try {
            Field mLayout = mBinding.tvFlavor.getClass().getSuperclass().getDeclaredField("mLayout");
            mLayout.setAccessible(true);
            mBinding.tvFlavor.setText("如果一定要说江山云著「均好度」的短板，那只能怪翁梅这个板块缺少光环了。");
            mBinding.tvFlavor.post(() -> {
                try {
                    Log.i("hehe1: ", String.valueOf(mBinding.tvFlavor.toString() + " " + mLayout.get(mBinding.tvFlavor)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBinding.tvFlavor.setText("如果一定要说江山云著「均好度」的短板，那只能怪翁梅这个板块缺少光环了。");
                            mBinding.tvFlavor.post(() -> {
                                try {
                                    Log.i("hehe2: ", String.valueOf(mBinding.tvFlavor.toString() + " " + mLayout.get(mBinding.tvFlavor)));
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setListeners() {
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewRxjava).subscribe(o ->
                startActivity(new Intent(MainActivity.this, RxJavaTestActivity.class))));
        mDisposables.add(RxViewWrapper.clicks(mBinding.fab).subscribe(o ->
                Snackbar.make(mBinding.fab, "Replace with your own action", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show()));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewCollapsingToolbar).subscribe(o ->
                startActivity(new Intent(MainActivity.this, CollapsingToolbarActivity.class))));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewDialog).subscribe(o ->
                startActivity(new Intent(MainActivity.this, DialogActivity.class))));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewCustom).subscribe(o ->
                startActivity(new Intent(MainActivity.this, ViewActivity.class))));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewMode).subscribe(o ->
                startActivity(new Intent(MainActivity.this, ActionModeActivity.class))));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewHandwriting).subscribe(o ->
                startActivity(new Intent(MainActivity.this, HandwritingActivity.class))));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewDict).subscribe(o ->
                startActivity(new Intent(MainActivity.this, DictActivity.class))));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewChart).subscribe(o ->
                startActivity(new Intent(MainActivity.this, ChartActivity.class))));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewImmersiveMode).subscribe(o ->
                startActivity(new Intent(MainActivity.this, ImmersiveModeActivity.class))));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewTransition).subscribe(o ->
                startActivity(new Intent(MainActivity.this, TransitionActivity.class))));
        mDisposables.add(RxViewWrapper.clicks(mBinding.cardViewLayout).subscribe(o ->
                startActivity(new Intent(MainActivity.this, LayoutTextViewActivity.class))));
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
