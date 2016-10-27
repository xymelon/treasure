package com.xycoding.treasure.rx.transformer;

import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Created by xuyang on 2016/8/4.
 */
public class ClickTransformer<T> implements Observable.Transformer<T, T> {

    @Override
    public Observable<T> call(Observable<T> voidObservable) {
        return voidObservable.throttleFirst(500, TimeUnit.MILLISECONDS);
    }

}
