package com.xycoding.treasure.view.headerviewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by xuyang on 15/11/24.
 */
public abstract class BaseRefreshView extends FrameLayout {

    public BaseRefreshView(Context context) {
        this(context, null);
    }

    public BaseRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPercent(float percent) {
    }

    public abstract void releaseToLoad(boolean isLoad);

    public abstract void start();

    public abstract void stop();

}
