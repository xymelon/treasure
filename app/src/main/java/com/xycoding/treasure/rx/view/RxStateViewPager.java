package com.xycoding.treasure.rx.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;

import rx.Observable;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Created by xuyang on 2016/8/4.
 */
public final class RxStateViewPager {
    /**
     * Create an observable of page scroll state change events on {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Note:</em> A value will be emitted immediately on subscribe.
     */
    @CheckResult
    @NonNull
    public static Observable<Integer> pageScrollStateChanged(@NonNull ViewPager view) {
        checkNotNull(view, "view == null");
        return Observable.create(new ViewPagerPageScrollStateChangedOnSubscribe(view));
    }

    private RxStateViewPager() {
        throw new AssertionError("No instances.");
    }
}
