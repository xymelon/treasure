package com.xycoding.treasure.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityRxjavaTestBinding;
import com.xycoding.treasure.utils.Logcat;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @author xymelon
 * @date 2017/12/6
 */
public class RxJavaTestActivity extends BaseBindingActivity<ActivityRxjavaTestBinding> {

    private static final String TAG = RxJavaTestActivity.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rxjava_test;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
    }

    @Override
    protected void setListeners() {
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        suggest();
    }

    private void suggest() {
        mDisposables.add(Observable.create(
                new ObservableOnSubscribe<CharSequence>() {
                    @Override
                    public void subscribe(ObservableEmitter<CharSequence> e) throws Exception {
                        final TextWatcher watcher = new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                e.onNext(s);
                            }
                        };
                        mBinding.etInput.addTextChangedListener(watcher);
                    }
                })
                .debounce(200, TimeUnit.MILLISECONDS)
                .filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(CharSequence charSequence) throws Exception {
                        return charSequence.length() > 0;
                    }
                })
                .switchMap(new Function<CharSequence, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(CharSequence charSequence) throws Exception {
                        return doSearch(charSequence);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mBinding.tvHint.setText(s);
                    }
                }));

//        mDisposables.add(RxTextView.afterTextChangeEvents(mBinding.etInput)
//                .debounce(200, TimeUnit.MILLISECONDS)
//                .map(TextViewAfterTextChangeEvent::editable)
//                .filter(editable -> editable.length() > 0)
//                .switchMap(this::doSearch)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(s -> mBinding.tvHint.setText(s)));
    }

    private Observable<String> doSearch(CharSequence input) {
        return Observable.create(e -> {
            try {
                Logcat.i(TAG, "开始请求：" + input);
                Thread.sleep((long) (100 + Math.random() * 500));
                e.onNext("suggest for " + input);
                e.onComplete();
                Logcat.i(TAG, "结束请求：" + input);
            } catch (Exception e1) {
                //do nothing.
            }
        });
    }

}
