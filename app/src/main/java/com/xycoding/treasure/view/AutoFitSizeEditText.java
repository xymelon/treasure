package com.xycoding.treasure.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.SingleLineTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.TextView;

import com.xycoding.treasure.R;

/**
 * Created by xuyang on 2016/11/14.
 */
public class AutoFitSizeEditText extends EditText {

    private static final int TEXT_SIZE_GAP = 2;

    // No limit (Integer.MAX_VALUE means no limit)
    private static final int NO_LIMIT_LINES = Integer.MAX_VALUE;

    // Registered resize listener
    private AutoFitSizeTextView.OnTextResizeListener mTextResizeListener;

    // Flag for text and/or size changes to force a resize
    private boolean mNeedsResize = false;

    // Text size that is set from code. This acts as a starting point for resizing
    private float mTextSize;

    // Temporary upper bounds on the starting text size
    private float mMaxTextSize;

    // Lower bounds for text size
    private float mMinTextSize;

    // Text view line spacing multiplier
    private float mSpacingMult = 1.0f;

    // Text view additional line spacing
    private float mSpacingAdd = 0.0f;

    // Default constructor override
    public AutoFitSizeEditText(Context context) {
        super(context);
        init(context, null);
    }

    // Default constructor when inflating from XML file
    public AutoFitSizeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    // Default constructor override
    public AutoFitSizeEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Resize text after measuring
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed || mNeedsResize) {
            int widthLimit = (right - left) - getCompoundPaddingLeft() - getCompoundPaddingRight();
            int heightLimit = (bottom - top) - getCompoundPaddingBottom() - getCompoundPaddingTop();
            resizeText(widthLimit, heightLimit);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * When text changes, set the force resize flag to true and reset the text size.
     */
    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        mNeedsResize = true;
        resizeText();
    }

    /**
     * If the text view size changed, set the force resize flag to true
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            mNeedsResize = true;
        }
    }

    /**
     * Override the set text size to update our internal reference values
     */
    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        mTextSize = getTextSize();
    }

    /**
     * Override the set text size to update our internal reference values
     */
    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        mTextSize = getTextSize();
    }

    /**
     * Override the set line spacing to update our internal reference values
     */
    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        mSpacingMult = mult;
        mSpacingAdd = add;
    }

    private void init(Context context, AttributeSet attrs) {
        mTextSize = getTextSize();
        mMaxTextSize = mTextSize;
        mMinTextSize = mTextSize;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoFitSizeEditText);
            mMinTextSize = typedArray.getDimension(R.styleable.AutoFitSizeEditText_minTextSize, mTextSize);
            typedArray.recycle();
        }
    }

    /**
     * Set the upper text size limit and invalidate the view
     *
     * @param maxTextSize
     */
    public void setMaxTextSize(float maxTextSize) {
        mMaxTextSize = maxTextSize;
        requestLayout();
        invalidate();
    }

    /**
     * Return upper text size limit
     *
     * @return
     */
    public float getMaxTextSize() {
        return mMaxTextSize;
    }

    /**
     * Set the lower text size limit and invalidate the view
     *
     * @param minTextSize
     */
    public void setMinTextSize(float minTextSize) {
        mMinTextSize = minTextSize;
        requestLayout();
        invalidate();
    }

    /**
     * Return lower text size limit
     *
     * @return
     */
    public float getMinTextSize() {
        return mMinTextSize;
    }

    /**
     * Resize the text size with default width and height
     */
    public void resizeText() {
        int heightLimit = getHeight() - getPaddingBottom() - getPaddingTop();
        int widthLimit = getWidth() - getPaddingLeft() - getPaddingRight();
        resizeText(widthLimit, heightLimit);
    }

    /**
     * Resize the text size with specified width and height
     *
     * @param width
     * @param height
     */
    public void resizeText(int width, int height) {
        CharSequence text = getText();
        // Do not resize if the view does not have dimensions or there is no text, or min text size equals max text size.
        if (text == null || text.length() == 0 || height <= 0 || width <= 0 || mTextSize == 0 || mMaxTextSize == mMinTextSize) {
            return;
        }
        int fitLine = getMaxLines(this);
        // Do not resize if the view wrap content.
        if (fitLine == NO_LIMIT_LINES) {
            return;
        }
        if (getTransformationMethod() != null) {
            text = getTransformationMethod().getTransformation(text, this);
        }

        // Get the text view's paint object
        TextPaint textPaint = getPaint();

        float targetTextSize = mTextSize;
        // fit with max line, or fit with TextView's height
        boolean hasFitted = false;
        int lineCount = getTextLines(text, textPaint, width, targetTextSize);
        // Until we either fit our lines or we had reached our min text size, incrementally try smaller sizes
        while (lineCount > fitLine && targetTextSize > mMinTextSize) {
            hasFitted = true;
            targetTextSize = Math.max(targetTextSize - TEXT_SIZE_GAP, mMinTextSize);
            lineCount = getTextLines(text, textPaint, width, targetTextSize);
        }
        // Find best text size when lineCount == fitLine.
        while (!hasFitted && lineCount == fitLine && targetTextSize < mMaxTextSize) {
            float tempSize = Math.min(targetTextSize + TEXT_SIZE_GAP, mMaxTextSize);
            lineCount = getTextLines(text, textPaint, width, tempSize);
            if (lineCount == fitLine) {
                targetTextSize = tempSize;
            }
        }

        // Some devices try to auto adjust line spacing, so force default line spacing
        // and invalidate the layout as a side effect
        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);
        // Reset force resize flag
        mNeedsResize = false;
    }

    private int getTextLines(CharSequence source, TextPaint paint, int width, float textSize) {
        TextPaint paintCopy = new TextPaint(paint);
        paintCopy.setTextSize(textSize);
        StaticLayout layout = new StaticLayout(source, paintCopy, width, Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
        return layout.getLineCount();
    }

    private static int getMaxLines(TextView view) {
        int maxLines = NO_LIMIT_LINES;
        TransformationMethod method = view.getTransformationMethod();
        if (method != null && method instanceof SingleLineTransformationMethod) {
            maxLines = 1;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // setMaxLines() and getMaxLines() are only available on android-16+
            maxLines = view.getMaxLines();
        }
        return maxLines;
    }

}
