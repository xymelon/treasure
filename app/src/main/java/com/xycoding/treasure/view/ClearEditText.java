package com.xycoding.treasure.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.xycoding.treasure.R;

/**
 * 扩展EditText，使用drawableRight作为清除按钮
 * <p>
 * Created by xuyang on 15/5/6.
 */
public class ClearEditText extends EditText {

    private final int DRAWABLE_LEFT = 0;
    private final int DRAWABLE_TOP = 1;
    private final int DRAWABLE_RIGHT = 2;
    private final int DRAWABLE_BOTTOM = 3;

    /**
     * StateListDrawable
     */
    private Drawable mDrawableClear;
    /**
     * 未按下状态时Drawable
     * <p/>
     * 注：单独设置此drawable的目的在于将drawableRight的state list与EditText分开，
     * 防止点击EditText时drawableRight显示改变
     */
    private Drawable mDrawableClearNormal;

    public ClearEditText(Context context) {
        super(context);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //若未设置android:drawableRight，则使用默认
        mDrawableClear = getCompoundDrawables()[DRAWABLE_RIGHT];
        if (mDrawableClear == null) {
            mDrawableClear = ContextCompat.getDrawable(getContext(), R.drawable.ic_clear_white_24dp);
            mDrawableClear.setBounds(0, 0, mDrawableClear.getIntrinsicWidth(), mDrawableClear.getIntrinsicHeight());
            mDrawableClear.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }
        //获取未按下状态Drawable
        mDrawableClearNormal = mDrawableClear.getCurrent();
        //若有初始值时，则显示clear
        setClearDrawable(mDrawableClearNormal);

        setOnTouchListener(mListener);
        addTextChangedListener(mWatcher);
    }

    /**
     * 设置EditText drawableRight
     *
     * @param drawable
     */
    private void setClearDrawable(Drawable drawable) {
        if (TextUtils.isEmpty(getText())) {
            setCompoundDrawables(getCompoundDrawables()[DRAWABLE_LEFT], getCompoundDrawables()[DRAWABLE_TOP],
                    null, getCompoundDrawables()[DRAWABLE_BOTTOM]);
        } else {
            setCompoundDrawables(getCompoundDrawables()[DRAWABLE_LEFT], getCompoundDrawables()[DRAWABLE_TOP],
                    drawable, getCompoundDrawables()[DRAWABLE_BOTTOM]);
        }
    }

    /**
     * 判断是否点击了clear drawable
     *
     * @param event
     * @return
     */
    private boolean isTouchClear(MotionEvent event) {
        return event.getX() > getWidth() - getPaddingRight() - mDrawableClear.getIntrinsicWidth();
    }


    private OnTouchListener mListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //判断down action位置是否为clear，是则替换drawable为StateListDrawable
                    if (isTouchClear(event)) {
                        ClearEditText.this.setClearDrawable(mDrawableClear);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isTouchClear(event)) {
                        ClearEditText.this.setText("");
                    }
                    break;
            }
            return false;
        }
    };

    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            ClearEditText.this.setClearDrawable(mDrawableClearNormal);
        }
    };
}
