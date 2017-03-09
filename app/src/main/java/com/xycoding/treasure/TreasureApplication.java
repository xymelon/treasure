package com.xycoding.treasure;

import android.app.Application;

import com.xycoding.treasure.utils.PrefUtils;

/**
 * Created by xuyang on 2016/10/28.
 */
public class TreasureApplication extends Application {

    private static TreasureApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        PrefUtils.init(this);
    }

    public static TreasureApplication getInstance() {
        return sInstance;
    }

}
