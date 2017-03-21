package com.xycoding.treasure.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by xuyang on 2017/3/17.
 */
public class LocalIntentService extends IntentService {

    private static final String TAG = LocalIntentService.class.getSimpleName();
    public static final String BUNDLE_KEY_TASK = "local_intent_service_task";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public LocalIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate__" + hashCode());
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand : " + intent.getStringExtra(BUNDLE_KEY_TASK) + "__" + hashCode());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "receive task : " + intent.getStringExtra(BUNDLE_KEY_TASK) + "__" + hashCode());
        SystemClock.sleep(2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "service destroyed.");
    }

}
