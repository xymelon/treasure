package com.xycoding.treasure.view;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 扩展TextView，使其文本超过控件高度时可滚动且支持单击事件
 * <p>
 * Created by xuyang on 2016/9/28.
 */
public class ScrollTextView extends TextView {

    private GestureDetector mGestureDetector;
    private OnClickListener mOnClickListener;

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setMovementMethod(ScrollingMovementMethod.getInstance());
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(ScrollTextView.this);
            }
            return true;
        }

    }

}
