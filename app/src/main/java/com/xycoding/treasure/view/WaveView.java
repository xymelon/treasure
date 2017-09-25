package com.xycoding.treasure.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import com.xycoding.treasure.R;

/**
 * Created by xymelon on 17-9-22.
 */
public class WaveView extends View implements Animator.AnimatorListener {

    private final static float INVALID = Float.MAX_VALUE;
    private final static float DEFAULT_MIN_SCALE = 1.f;
    private final static int DEFAULT_DURATION = 800;

    private AnimatorSet mAnimatorSet;
    private ObjectAnimator mAnimatorX;
    private ObjectAnimator mAnimatorY;
    private float mMaxScale;
    private int mDuration;
    /**
     * Indicates whether breathing animation is running.
     */
    private boolean mBreathing = false;
    /**
     * Indicates whether normal wave animation is running.
     */
    private boolean mWaving = false;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            initStartBreathing();
        } else {
            stopAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    private void init(final Context context, final AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
            mMaxScale = typedArray.getFloat(R.styleable.WaveView_wave_max_scale, INVALID);
            mDuration = typedArray.getInt(R.styleable.WaveView_wave_duration, DEFAULT_DURATION);
            typedArray.recycle();
        } else {
            mMaxScale = INVALID;
            mDuration = DEFAULT_DURATION;
        }
        initStartBreathing();
    }

    private void initStartBreathing() {
        if (getWidth() != 0 && getHeight() != 0) {
            startBreathing();
            return;
        }
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                startBreathing();
                return true;
            }
        });
    }

    public void updateWavePercent(float percent) {
        if (percent <= 0.f) {
            return;
        }
        if (mBreathing || !mWaving) {
            percent = 1 + percent;
            if (percent > maxPercent()) {
                percent = maxPercent();
            }
            mWaving = true;
            startAnimation(percent, false);
        }
    }

    private void startAnimation(float percent, boolean loop) {
        mBreathing = loop;
        if (mAnimatorX == null) {
            mAnimatorX = ObjectAnimator.ofFloat(this, "scaleX", DEFAULT_MIN_SCALE, DEFAULT_MIN_SCALE);
            mAnimatorX.setInterpolator(new LinearInterpolator());
        }
        if (mAnimatorY == null) {
            mAnimatorY = ObjectAnimator.ofFloat(this, "scaleY", DEFAULT_MIN_SCALE, DEFAULT_MIN_SCALE);
            mAnimatorY.setInterpolator(new LinearInterpolator());
        }
        mAnimatorX.setFloatValues(DEFAULT_MIN_SCALE, percent, DEFAULT_MIN_SCALE);
        mAnimatorY.setFloatValues(DEFAULT_MIN_SCALE, percent, DEFAULT_MIN_SCALE);
        if (loop) {
            mAnimatorX.setRepeatMode(ValueAnimator.REVERSE);
            mAnimatorX.setRepeatCount(ValueAnimator.INFINITE);
            mAnimatorY.setRepeatMode(ValueAnimator.REVERSE);
            mAnimatorY.setRepeatCount(ValueAnimator.INFINITE);
        } else {
            mAnimatorX.setRepeatMode(ValueAnimator.RESTART);
            mAnimatorX.setRepeatCount(0);
            mAnimatorY.setRepeatMode(ValueAnimator.RESTART);
            mAnimatorY.setRepeatCount(0);
        }
        if (mAnimatorSet != null) {
            mAnimatorSet.removeAllListeners();
            mAnimatorSet.cancel();
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(mAnimatorX, mAnimatorY);
        mAnimatorSet.addListener(this);
        mAnimatorSet.setDuration(mDuration).start();
    }

    private void startBreathing() {
        if (mBreathing) {
            return;
        }
        startAnimation(maxPercent(), true);
    }

    private void stopAnimation() {
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.end();
        }
    }

    private float maxPercent() {
        float scaleLimit = DEFAULT_MIN_SCALE;
        if (getWidth() != 0 && getHeight() != 0) {
            final float widthPercent = ((ViewGroup) getParent()).getWidth() * 1.f / getWidth();
            final float heightPercent = ((ViewGroup) getParent()).getHeight() * 1.f / getHeight();
            scaleLimit = Math.min(widthPercent, heightPercent);
        }
        if (mMaxScale != INVALID) {
            return Math.min(mMaxScale, scaleLimit);
        } else {
            return scaleLimit;
        }
    }

    @Override
    public void onAnimationStart(Animator animator) {
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        mWaving = false;
        startBreathing();
    }

    @Override
    public void onAnimationCancel(Animator animator) {
        mWaving = false;
        startBreathing();
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
    }

}
