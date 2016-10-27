package com.xycoding.treasure.rx;

import com.xycoding.treasure.rx.event.BaseEvent;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by xuyang on 2016/6/21.
 */
public class RxBus {

    private final static Subject<BaseEvent, BaseEvent> subject =
            new SerializedSubject<>(PublishSubject.<BaseEvent>create());

    private RxBus() {
    }

    public static void send(BaseEvent event) {
        subject.onNext(event);
    }

    public static <T> Observable<T> filter(final Class<T> eventClass) {
        return subject
                .filter(new Func1<BaseEvent, Boolean>() {
                    @Override
                    public Boolean call(BaseEvent event) {
                        return event.getClass().equals(eventClass);
                    }
                })
                .map(new Func1<BaseEvent, T>() {
                    @Override
                    public T call(BaseEvent event) {
                        return (T) event;
                    }
                });
    }

}
