package com.xycoding.treasure.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityMainBinding;
import com.xycoding.treasure.rx.RxViewWrapper;

import rx.functions.Action1;

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
        showNotify();

        mBinding.tvJapan.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/DroidSansJapanese.ttf"));
        mBinding.tvJapan.setText("ヮ打开等哈看对哈萨克的和喀什的卡上");
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
                startActivity(new Intent(MainActivity.this, DialogActivity.class));
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewCustom).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(MainActivity.this, ViewActivity.class));
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewMode).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(MainActivity.this, ActionModeActivity.class));
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewHandwriting).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(MainActivity.this, HandwritingActivity.class));
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewSpeech).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(MainActivity.this, SpeechActivity.class));
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Toast.makeText(this, "我没打开新页面哦", Toast.LENGTH_SHORT).show();
    }

    private void showNotify() {
        //MainActivity需设置singleTask
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.layout_notification);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_search_white_24dp)
                .setContent(contentView);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

}
