package com.xycoding.treasure.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.xycoding.treasure.R;

import java.lang.ref.WeakReference;

public class RippleBackground extends RelativeLayout {

    private static final int VOLUME_MESSAGE = 1024;
    private static final int DEFAULT_DURATION_TIME = 100;
    private static final int DEFAULT_RIPPLE_RADIUS = 50;
    private static final float DEFAULT_RIPPLE_SCALE = 1.f;
    private static final float DEFAULT_RIPPLE_SCALE_MAX = 1.5f;

    private int mRippleColor;
    private float mRippleRadius;
    private int mRippleAnimationTime;
    private float mCurrentRippleScale = DEFAULT_RIPPLE_SCALE;
    private float mNextRippleScale = DEFAULT_RIPPLE_SCALE;
    private Paint mPaint;
    private AnimatorSet mAnimatorSet;
    private RippleView mRippleView;
    private LayoutParams mRippleLayoutParams;
    private VolumeHandler mVolumeHandler;

    public RippleBackground(Context context) {
        super(context);
    }

    public RippleBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        if (attrs == null) {
            throw new IllegalArgumentException("Attributes should be provided to this view,");
        }
        mVolumeHandler = new VolumeHandler(this);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleBackground);
        mRippleColor = typedArray.getColor(R.styleable.RippleBackground_rb_color, ContextCompat.getColor(getContext(), R.color.rippelColor));
        mRippleRadius = typedArray.getDimension(R.styleable.RippleBackground_rb_radius, DEFAULT_RIPPLE_RADIUS);
        mRippleAnimationTime = typedArray.getInt(R.styleable.RippleBackground_rb_duration, DEFAULT_DURATION_TIME);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mRippleColor);

        mRippleLayoutParams = new LayoutParams((int) (2 * mRippleRadius), (int) (2 * (mRippleRadius)));
        mRippleLayoutParams.addRule(CENTER_IN_PARENT, TRUE);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        mRippleView = new RippleView(getContext());
        addView(mRippleView, mRippleLayoutParams);
    }

    private class RippleView extends View {

        public RippleView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int radius = (Math.min(getWidth(), getHeight())) / 2;
            canvas.drawCircle(radius, radius, radius, mPaint);
        }
    }

    public void stopRippleAnimation() {
        mRippleView.setVisibility(INVISIBLE);
        mRippleView.setScaleX(DEFAULT_RIPPLE_SCALE);
        mRippleView.setScaleY(DEFAULT_RIPPLE_SCALE);
        if (mAnimatorSet.isRunning()) {
            mAnimatorSet.end();
        }
        mVolumeHandler.removeMessages(VOLUME_MESSAGE);
    }

    public void setRippleScale(float scale) {
        mNextRippleScale = DEFAULT_RIPPLE_SCALE + scale * (DEFAULT_RIPPLE_SCALE_MAX - DEFAULT_RIPPLE_SCALE);
        if (!mVolumeHandler.hasMessages(VOLUME_MESSAGE)) {
            mVolumeHandler.sendEmptyMessage(VOLUME_MESSAGE);
        }
    }

    private Runnable mVolumeRunnable = new Runnable() {
        @Override
        public void run() {
            System.out.println(mCurrentRippleScale + ":" + mNextRippleScale);
            mRippleView.setVisibility(VISIBLE);
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mRippleView, "ScaleX", mCurrentRippleScale, mNextRippleScale);
            scaleXAnimator.setDuration(mRippleAnimationTime);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mRippleView, "ScaleY", mCurrentRippleScale, mNextRippleScale);
            scaleYAnimator.setDuration(mRippleAnimationTime);
            mAnimatorSet.playTogether(scaleXAnimator, scaleYAnimator);
            mAnimatorSet.start();
            mCurrentRippleScale = mNextRippleScale;
        }
    };

    private static class VolumeHandler extends Handler {

        private WeakReference<RippleBackground> weakReference;

        VolumeHandler(RippleBackground rippleBackground) {
            weakReference = new WeakReference<>(rippleBackground);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference != null && weakReference.get() != null) {
                weakReference.get().mVolumeRunnable.run();
                sendEmptyMessageDelayed(VOLUME_MESSAGE, weakReference.get().mRippleAnimationTime);
            }
        }
    }

}
