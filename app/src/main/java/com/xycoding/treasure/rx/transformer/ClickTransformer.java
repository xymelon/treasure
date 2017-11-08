package com.xycoding.treasure.rx.transformer;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

/**
 * Created by xuyang on 2016/8/4.
 */
public class ClickTransformer<T> implements ObservableTransformer<T, T> {

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.throttleFirst(500, TimeUnit.MILLISECONDS);
    }

}
