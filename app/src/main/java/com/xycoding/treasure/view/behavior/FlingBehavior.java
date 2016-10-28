package com.xycoding.treasure.view.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xuyang on 16/9/17.
 */
public final class FlingBehavior extends AppBarLayout.Behavior {

    public FlingBehavior() {
    }

    public FlingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
        ScrollingView scrollingTarget = findScrollingTarget(target);
        if (scrollingTarget != null) {
            consumed = velocityY > 0 || scrollingTarget.computeVerticalScrollOffset() > 0;
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    private ScrollingView findScrollingTarget(View target) {
        if (target instanceof ScrollingView) {
            return (ScrollingView) target;
        }
        if (target instanceof ViewPager) {
            //just care visible item
            target = ((ViewPager) target).getChildAt(((ViewPager) target).getCurrentItem());
        }
        if (target instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) target).getChildCount(); i++) {
                return findScrollingTarget(((ViewGroup) target).getChildAt(i));
            }
        }
        return null;
    }

}
