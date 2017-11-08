package com.xycoding.treasure.rx;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.AdapterViewItemClickEvent;
import com.jakewharton.rxbinding2.widget.RxAutoCompleteTextView;
import com.xycoding.treasure.rx.transformer.ClickTransformer;

import io.reactivex.Observable;

/**
 * Created by xuyang on 2016/8/22.
 */
public class RxViewWrapper {

    /**
     * Create an observable which emits on {@code view} click events. The emitted value is
     * unspecified and should only be used as notification.
     * <p/>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p/>
     * <em>Warning:</em> The created observable uses {@link View#setOnClickListener} to observe
     * clicks. Only one observable can be used for a view at a time.
     */
    @NonNull
    public static Observable<Object> clicks(@NonNull View view) {
        return RxView.clicks(view).compose(new ClickTransformer<>());
    }

    /**
     * Create an observable of item click events on {@code view}.
     * <p/>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @NonNull
    public static Observable<AdapterViewItemClickEvent> itemClickEvents(@NonNull AutoCompleteTextView view) {
        return RxAutoCompleteTextView.itemClickEvents(view)
                .compose(new ClickTransformer<AdapterViewItemClickEvent>());
    }

}
