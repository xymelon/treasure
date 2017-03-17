package com.xycoding.treasure.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.xycoding.treasure.R;

public class RippleBackground1 extends RelativeLayout {

    private static final int DEFAULT_RIPPLE_RADIUS = 50;
    private static final int DEFAULT_DURATION_TIME = 200;
    private static final long DEFAULT_BREATHING_TIME = 800;
    private static final float DEFAULT_RIPPLE_SCALE = 1.f;
    private static final float DEFAULT_RIPPLE_SCALE_MAX = 1.4f;
    private static final float DEFAULT_BREATHING_MAX = 1.1f;

    private int mRippleColor;
    private float mRippleRadius;
    private int mRippleAnimationTime;
    private float mCurrentRippleScale = DEFAULT_RIPPLE_SCALE;
    private float mNextRippleScale = DEFAULT_RIPPLE_SCALE;
    private Paint mPaint;
    private AnimatorSet mVolumeAnimatorSet;
    private RippleView mRippleView;

    private ObjectAnimator mVolumeScaleX;
    private ObjectAnimator mVolumeScaleY;
    private ObjectAnimator mBreathingAnimator;

    public RippleBackground1(Context context) {
        super(context);
    }

    public RippleBackground1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleBackground1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopBreathingAnimation();
    }

    private void init(final Context context, final AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        if (attrs == null) {
            throw new IllegalArgumentException("Attributes should be provided to this view,");
        }
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleBackground);
        mRippleColor = typedArray.getColor(R.styleable.RippleBackground_rb_color, ContextCompat.getColor(getContext(), R.color.ripple_color));
        mRippleRadius = typedArray.getDimension(R.styleable.RippleBackground_rb_radius, DEFAULT_RIPPLE_RADIUS);
        mRippleAnimationTime = typedArray.getInt(R.styleable.RippleBackground_rb_duration, DEFAULT_DURATION_TIME);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mRippleColor);

        LayoutParams layoutParams = new LayoutParams((int) (2 * mRippleRadius), (int) (2 * (mRippleRadius)));
        layoutParams.addRule(CENTER_IN_PARENT, TRUE);
        mRippleView = new RippleView(getContext());
        addView(mRippleView, layoutParams);
    }

    private void startBreathingAnimation() {
        if (mBreathingAnimator == null) {
            mBreathingAnimator = ObjectAnimator.ofPropertyValuesHolder(
                    mRippleView,
                    PropertyValuesHolder.ofFloat("ScaleX", DEFAULT_BREATHING_MAX),
                    PropertyValuesHolder.ofFloat("ScaleY", DEFAULT_BREATHING_MAX));
            mBreathingAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mRippleView.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mBreathingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRippleScale = DEFAULT_RIPPLE_SCALE + (DEFAULT_BREATHING_MAX - DEFAULT_RIPPLE_SCALE) * animation.getAnimatedFraction();
                }
            });
            mBreathingAnimator.setDuration(DEFAULT_BREATHING_TIME);
            mBreathingAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            mBreathingAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        }
        if (!mBreathingAnimator.isRunning()) {
            mBreathingAnimator.start();
        }
    }

    private void ensureObjectAnimator() {
        if (mVolumeScaleX == null) {
            mVolumeScaleX = ObjectAnimator.ofFloat(mRippleView, "ScaleX", DEFAULT_RIPPLE_SCALE, DEFAULT_RIPPLE_SCALE);
        }
        if (mVolumeScaleY == null) {
            mVolumeScaleY = ObjectAnimator.ofFloat(mRippleView, "ScaleY", DEFAULT_RIPPLE_SCALE, DEFAULT_RIPPLE_SCALE);
        }
    }

    public void stopVolumeAnimation() {
        if (mVolumeAnimatorSet != null && mVolumeAnimatorSet.isRunning()) {
            mVolumeAnimatorSet.end();
        }
        startBreathingAnimation();
    }

    public void stopBreathingAnimation() {
        mRippleView.setVisibility(INVISIBLE);
        if (mVolumeAnimatorSet != null && mVolumeAnimatorSet.isRunning()) {
            mVolumeAnimatorSet.end();
        }
        stopBreathing();
    }

    private void stopBreathing() {
        if (mBreathingAnimator != null && mBreathingAnimator.isRunning()) {
            mBreathingAnimator.end();
        }
    }

    private boolean isBreathing() {
        return mBreathingAnimator != null && mBreathingAnimator.isRunning();
    }

    private boolean isVolumeRunning() {
        return mVolumeAnimatorSet != null && mVolumeAnimatorSet.isRunning();
    }

    public void setRippleScale(float scale) {
        if (scale > 0.f) {
            mNextRippleScale = DEFAULT_BREATHING_MAX + scale * (DEFAULT_RIPPLE_SCALE_MAX - DEFAULT_BREATHING_MAX);
            if (!isVolumeRunning()) {
                mVolumeRunnable.run();
            }
        } else {
            if (!isBreathing() && !isVolumeRunning()) {
                startBreathingAnimation();
            }
        }
    }

    private Runnable mVolumeRunnable = new Runnable() {
        @Override
        public void run() {
            mRippleView.setVisibility(VISIBLE);
            stopBreathing();
            ensureObjectAnimator();

            mVolumeScaleX.setFloatValues(mCurrentRippleScale, mNextRippleScale);
            mVolumeScaleX.removeAllUpdateListeners();
            mVolumeScaleX.setDuration(mRippleAnimationTime);

            mVolumeScaleY.setFloatValues(mCurrentRippleScale, mNextRippleScale);
            mVolumeScaleY.setDuration(mRippleAnimationTime);

            mCurrentRippleScale = mNextRippleScale;

            mVolumeAnimatorSet = new AnimatorSet();
            mVolumeAnimatorSet.playTogether(mVolumeScaleX, mVolumeScaleY);
            mVolumeAnimatorSet.start();
        }
    };

    private class RippleView extends View {

        public RippleView(Context context) {
            super(context);
            setVisibility(INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int radius = (Math.min(getWidth(), getHeight())) / 2;
            canvas.drawCircle(radius, radius, radius, mPaint);
        }
    }

}
