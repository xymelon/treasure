package com.xycoding.treasure;

import android.app.Application;

import com.xycoding.treasure.utils.PrefUtils;

/**
 * Created by xuyang on 2016/10/28.
 */
public class TreasureApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PrefUtils.init(this);
    }
}
