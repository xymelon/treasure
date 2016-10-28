package com.xycoding.treasure.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xycoding.treasure.R;

import java.lang.ref.WeakReference;

/**
 * 文本后面添加图片，文本多行显示时跟随文字
 * <p>
 * Created by xuyang on 2016/9/18.
 */
public class ImageEndTextView extends TextView {

    private Drawable mImageEnd;
    private boolean mImageEndVisibility;

    public ImageEndTextView(Context context) {
        this(context, null);
    }

    public ImageEndTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageEndTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageEndTextView);
            mImageEnd = typedArray.getDrawable(R.styleable.ImageEndTextView_imageEnd);
            typedArray.recycle();
        }
        if (mImageEnd != null) {
            mImageEnd.setBounds(0, 0, mImageEnd.getIntrinsicWidth(), mImageEnd.getIntrinsicHeight());
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (mImageEndVisibility && mImageEnd != null) {
            String string = text.toString().concat(" ");
            int length = string.length();
            SpannableString spannableString = new SpannableString(string);
            CenteredImageSpan centeredImageSpan = new CenteredImageSpan(mImageEnd);
            spannableString.setSpan(centeredImageSpan, length - 1, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            super.setText(spannableString, type);
        } else {
            super.setText(text, type);
        }
    }

    /**
     * 是否显示尾部图片（必须在设置setText之前调用）
     */
    public void setImageEndVisibility(boolean imageEndVisibility) {
        mImageEndVisibility = imageEndVisibility;
    }

    public class CenteredImageSpan extends ImageSpan {

        private WeakReference<Drawable> mDrawableRef;

        public CenteredImageSpan(Drawable drawable) {
            super(drawable);
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text,
                         int start, int end, float x,
                         int top, int y, int bottom, @NonNull Paint paint) {
            Drawable drawable = getCachedDrawable();
            canvas.save();

            int drawableCenter = drawable.getIntrinsicHeight() / 2;
            int fontTop = paint.getFontMetricsInt().top;
            int fontBottom = paint.getFontMetricsInt().bottom;
            int transY = bottom - drawable.getBounds().bottom - (((fontBottom - fontTop) / 2) - drawableCenter);

            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();
        }

        // Redefined locally because it is a private member from DynamicDrawableSpan
        private Drawable getCachedDrawable() {
            WeakReference<Drawable> drawableRef = mDrawableRef;
            Drawable drawable = null;

            if (drawableRef != null)
                drawable = drawableRef.get();

            if (drawable == null) {
                drawable = getDrawable();
                mDrawableRef = new WeakReference<>(drawable);
            }
            return drawable;
        }
    }

}
