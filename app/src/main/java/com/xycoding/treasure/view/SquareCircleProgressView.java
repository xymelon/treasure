package com.xycoding.treasure.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.xycoding.treasure.R;

/**
 * Created by xymelon on 2018/3/30.
 */
public class SquareCircleProgressView extends View {

    private final static float DEFAULT_BAR_WIDTH = 6.f;

    private Paint mBackgroundPaint;
    private Paint mProgressPaint;

    private float mBarWidth;
    private float mHalfBarWidth;
    private RectF mBounds = new RectF();

    private float mSweepAngle;

    public SquareCircleProgressView(Context context) {
        this(context, null);
    }

    public SquareCircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareCircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int barBgColorInt = ContextCompat.getColor(context, android.R.color.transparent);
        int barColorInt = ContextCompat.getColor(context, android.R.color.darker_gray);
        if (attrs != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SquareCircleProgressView);
            barBgColorInt = typedArray.getColor(R.styleable.SquareCircleProgressView_bgBarColor, barBgColorInt);
            barColorInt = typedArray.getColor(R.styleable.SquareCircleProgressView_barColor, barColorInt);
            mBarWidth = typedArray.getDimension(R.styleable.SquareCircleProgressView_barWidth, DEFAULT_BAR_WIDTH);
            updateProgress(typedArray.getInt(R.styleable.SquareCircleProgressView_barProgress, 0));
            typedArray.recycle();
        }
        mHalfBarWidth = mBarWidth / 2;

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setColor(barBgColorInt);
        mBackgroundPaint.setStrokeWidth(mBarWidth);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setColor(barColorInt);
        mProgressPaint.setStrokeWidth(mBarWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (measuredWidth > measuredHeight) {
            measuredWidth = measuredHeight;
        } else {
            measuredHeight = measuredWidth;
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBounds.isEmpty()) {
            mBounds.set(mHalfBarWidth, mHalfBarWidth, getWidth() - mHalfBarWidth, getHeight() - mHalfBarWidth);
        }
        canvas.drawCircle(mBounds.centerX(), mBounds.centerY(), mBounds.width() / 2, mBackgroundPaint);
        canvas.drawArc(mBounds, -90, mSweepAngle, false, mProgressPaint);
    }

    /**
     * 0-100
     *
     * @param progress
     */
    public void updateProgress(int progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > 100) {
            progress = 100;
        }
        mSweepAngle = progress / 100.f * 360;
        invalidate();
    }

}
