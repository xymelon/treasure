package com.xycoding.treasure.view.richtext.typeface;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.xycoding.treasure.view.richtext.LinkTouchMovementMethod;

/**
 * Created by xuyang on 2017/4/28.
 */
public class ClickSpan extends ClickableSpan implements IStyleSpan {

    private TextView mTextView;
    private int mPressedBackgroundColor;
    private int mNormalTextColor;
    private int mPressedTextColor;
    private OnClickListener mOnClickListener;

    private boolean mPressed;
    private CharSequence mPressedText;

    public ClickSpan(@NonNull TextView textView) {
        this(textView, 0, 0, 0, null);
    }

    public ClickSpan(@NonNull TextView textView, @NonNull OnClickListener listener) {
        this(textView, 0, 0, 0, listener);
    }

    public ClickSpan(@NonNull TextView textView,
                     @ColorInt int normalTextColor,
                     @ColorInt int pressedTextColor,
                     @ColorInt int pressedBackgroundColor,
                     @Nullable OnClickListener listener) {
        mTextView = textView;
        mTextView.setMovementMethod(new LinkTouchMovementMethod());

        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
        mPressedBackgroundColor = pressedBackgroundColor;
        mOnClickListener = listener;
    }

    @Override
    public void onClick(View widget) {
        //do nothing.
    }

    public void onClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(mPressedText);
        }
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        super.updateDrawState(paint);
        if (mNormalTextColor == 0) {
            mNormalTextColor = paint.getColor();
        }
        if (mPressedTextColor == 0) {
            mPressedTextColor = mNormalTextColor;
        }
        paint.setColor(mPressed ? mPressedTextColor : mNormalTextColor);
        paint.bgColor = mPressed ? mPressedBackgroundColor : Color.TRANSPARENT;
        paint.setUnderlineText(false);
    }

    @Override
    public CharacterStyle getStyleSpan() {
        return new ClickSpan(mTextView, mNormalTextColor, mPressedTextColor, mPressedBackgroundColor, mOnClickListener);
    }

    public void setPressed(boolean pressed, CharSequence pressedText) {
        mPressed = pressed;
        mPressedText = pressedText;
    }

    public interface OnClickListener {
        void onClick(CharSequence text);
    }

}
