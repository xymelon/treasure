package com.xycoding.treasure.activity;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityHandwritingBinding;
import com.xycoding.treasure.rx.RxViewWrapper;
import com.xycoding.treasure.view.handwriting.HandwritingView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.functions.Action1;

/**
 * Created by xuyang on 2016/12/15.
 */
public class HandwritingActivity extends BaseBindingActivity {

    private ActivityHandwritingBinding mBinding;
    private AsyncTask<Void, Void, String> mAsyncTask;
    private String mCurHandwriting = "";
    private int mCurPointSize = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_handwriting;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (ActivityHandwritingBinding) binding;
    }

    @Override
    protected void setListeners() {
        subscriptions.add(RxViewWrapper.clicks(mBinding.btnClear).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mBinding.viewHandwriting.clear();
                mBinding.tvContent.setText("");
                mCurHandwriting = "";
                mCurPointSize = 0;
            }
        }));
        mBinding.viewHandwriting.setOnHandwritingListener(new HandwritingView.OnHandwritingListener() {

            private List<Point> points = new ArrayList<>();

            @Override
            public void onStart() {
                points.clear();
            }

            @Override
            public void onHandwriting(int eventX, int eventY) {
                if (points.size() > 1) {
                    //过滤相同坐标
                    Point point = points.get(points.size() - 1);
                    if (point.x == eventX && point.y == eventY) {
                        return;
                    }
                }
                points.add(new Point(eventX, eventY));
            }

            @Override
            public void onFinished() {
                querySuggest(points);
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTask();
    }

    private void querySuggest(List<Point> points) {
        cancelTask();
        if (points.size() == 0) {
            return;
        }
        for (Point point : points) {
            mCurHandwriting += convertPoint(point.x) + convertPoint(point.y);
        }
        mCurHandwriting += ".";
        mCurPointSize += points.size();
        mAsyncTask = new SuggestTask(mCurPointSize, mCurHandwriting, this);
        mAsyncTask.execute();
    }

    private void callbackContent(String suggest) {
        mBinding.tvContent.setText(suggest);
    }

    private void cancelTask() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
    }

    /**
     * 手写判定只支持三位数，不足三位需补0；
     * point/2 防止数字大于三位数。
     *
     * @param num
     * @return
     */
    private String convertPoint(int num) {
        int intPoint = (int) (num / 2.f);
        if (intPoint < 100) {
            return "0" + intPoint;
        }
        return String.valueOf(intPoint);
    }

    private static class SuggestTask extends AsyncTask<Void, Void, String> {

        private int curPointSize;
        private String curHandwriting;
        private WeakReference<HandwritingActivity> weakReference;

        public SuggestTask(int curPointSize, String curHandwriting, HandwritingActivity activity) {
            this.curPointSize = curPointSize;
            this.curHandwriting = curHandwriting;
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                final Request request = new Request.Builder()
                        .url(String.format("http://dict.youdao.com/handwrite?c=1&n=%d&cs=utf8", curPointSize))
                        .post(new FormBody.Builder().add("d", curHandwriting).build())
                        .build();
                Response response = new OkHttpClient.Builder().build().newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String string) {
            if (weakReference.get() != null) {
                weakReference.get().callbackContent(string);
            }
        }
    }
}
