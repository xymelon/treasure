package com.xycoding.treasure.view.handwriting;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xycoding.treasure.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xuyang on 2016/11/14.
 */
public class HandwritingView extends View {
    //View state
    private List<Path> mPaths = new ArrayList<>();
    private Path mCurrentPath;
    private List<List<PointF>> mPathPoints = new ArrayList<>();
    private List<PointF> mCurrentPathPoints;
    private float mLastActionDownX, mPreX;
    private float mLastActionDownY, mPreY;
    private RectF mDirtyRect;

    //Default attribute values
    private final int DEFAULT_ATTR_PEN_WIDTH_DP = 3;
    private final int DEFAULT_ATTR_PEN_COLOR = Color.BLACK;
    private static final int INVALID_POINTER = -1;

    //Configurable parameters
    private int mPenWidth;
    private OnHandwritingListener mOnHandwritingListener;
    private Paint mPaint = new Paint();
    private int mHandwritingPointerId = INVALID_POINTER;
    private boolean mHandwritingFinished = false;

    public HandwritingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.HandwritingView,
                0, 0);

        //Configurable parameters
        try {
            mPenWidth = a.getDimensionPixelSize(R.styleable.HandwritingView_penWidth, convertDpToPx(DEFAULT_ATTR_PEN_WIDTH_DP));
            mPaint.setColor(a.getColor(R.styleable.HandwritingView_penColor, DEFAULT_ATTR_PEN_COLOR));
        } finally {
            a.recycle();
        }

        //Fixed parameters
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(mPenWidth);

        //Dirty rectangle to update only the changed portion of the view
        mDirtyRect = new RectF();

        clear();
    }

    /**
     * Set the pen color from a given resource.
     * If the resource is not found, {@link Color#BLACK} is assumed.
     *
     * @param colorRes the color resource.
     */
    public void setPenColorRes(int colorRes) {
        try {
            setPenColor(getResources().getColor(colorRes));
        } catch (Resources.NotFoundException ex) {
            setPenColor(Color.parseColor("#000000"));
        }
    }

    /**
     * Set the pen color from a given color.
     *
     * @param color the color.
     */
    public void setPenColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 撤销最近笔画
     */
    public void undo() {
        if (mPaths.size() > 0) {
            mPaths.remove(mPaths.size() - 1);
            mPathPoints.remove(mPathPoints.size() - 1);
            if (mCurrentPathPoints != null) {
                mCurrentPathPoints.clear();
            }
            callbackListener();
            invalidate();
        }
    }

    /**
     * 清除所有
     */
    public void clear() {
        mPaths.clear();
        mPathPoints.clear();
        if (mCurrentPathPoints != null) {
            mCurrentPathPoints.clear();
        }
        callbackListener();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                mHandwritingFinished = false;
                //Record handwriting pointer id.
                mHandwritingPointerId = event.getPointerId(0);
                mPreX = mLastActionDownX = eventX;
                mPreY = mLastActionDownY = eventY;
                //New path.
                mCurrentPath = new Path();
                mCurrentPath.moveTo(eventX, eventY);
                //若path中只有相同坐标时不会绘制任何图像，+0.1f强制使得path绘点。
                mCurrentPath.lineTo(eventX + 0.1f, eventY + 0.1f);
                mPaths.add(mCurrentPath);
                //New path points.
                mCurrentPathPoints = new ArrayList<>();
                addNewPoint(eventX, eventY);
                break;
            case MotionEvent.ACTION_MOVE:
                //Only care about handwriting pointer id.
                if (isHandwritingPointerId(event)) {
                    movePath(eventX, eventY);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //If handwriting pointer id ACTION_POINTER_UP, then finish.
                if (isHandwritingPointerId(event)) {
                    mHandwritingFinished = true;
                    onHandwritingPointerUp(eventX, eventY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mHandwritingFinished) {
                    onHandwritingPointerUp(eventX, eventY);
                }
                break;
            default:
                return false;
        }
        invalidate(
                (int) (mDirtyRect.left - mPenWidth),
                (int) (mDirtyRect.top - mPenWidth),
                (int) (mDirtyRect.right + mPenWidth),
                (int) (mDirtyRect.bottom + mPenWidth));
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Path path : mPaths) {
            canvas.drawPath(path, mPaint);
        }
    }

    private boolean isHandwritingPointerId(MotionEvent ev) {
        return mHandwritingPointerId == ev.getPointerId(MotionEventCompat.getActionIndex(ev));
    }

    private void movePath(float eventX, float eventY) {
        resetDirtyRect(eventX, eventY);
        addNewPoint(eventX, eventY);
        //Calculate Bezier control point.
        float middleX = (mPreX + eventX) / 2;
        float middleY = (mPreY + eventY) / 2;
        mCurrentPath.quadTo(mPreX, mPreY, middleX, middleY);
        mPreX = eventX;
        mPreY = eventY;
    }

    private void addNewPoint(float eventX, float eventY) {
        //至少返回两个坐标
        if (mCurrentPathPoints.size() > 1) {
            //过滤相同坐标
            PointF point = mCurrentPathPoints.get(mCurrentPathPoints.size() - 1);
            if (point.x == eventX && point.y == eventY) {
                return;
            }
        }
        mCurrentPathPoints.add(new PointF(eventX, eventY));
    }

    private void onHandwritingPointerUp(float eventX, float eventY) {
        movePath(eventX, eventY);
        mPathPoints.add(mCurrentPathPoints);
        callbackListener();
    }

    public void setOnHandwritingListener(OnHandwritingListener listener) {
        mOnHandwritingListener = listener;
    }

    private void callbackListener() {
        if (mOnHandwritingListener != null) {
            mOnHandwritingListener.onWritingFinished(mPathPoints);
        }
    }

    /**
     * Called when replaying history to ensure the dirty region includes all
     * mPoints.
     *
     * @param historicalX the previous x coordinate.
     * @param historicalY the previous y coordinate.
     */
    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < mDirtyRect.left) {
            mDirtyRect.left = historicalX;
        } else if (historicalX > mDirtyRect.right) {
            mDirtyRect.right = historicalX;
        }
        if (historicalY < mDirtyRect.top) {
            mDirtyRect.top = historicalY;
        } else if (historicalY > mDirtyRect.bottom) {
            mDirtyRect.bottom = historicalY;
        }
    }

    /**
     * Resets the dirty region when the motion event occurs.
     *
     * @param eventX the event x coordinate.
     * @param eventY the event y coordinate.
     */
    private void resetDirtyRect(float eventX, float eventY) {
        //The mLastActionDownX and mLastActionDownY were set when the ACTION_DOWN motion event occurred.
        mDirtyRect.left = Math.min(mLastActionDownX, eventX);
        mDirtyRect.right = Math.max(mLastActionDownX, eventX);
        mDirtyRect.top = Math.min(mLastActionDownY, eventY);
        mDirtyRect.bottom = Math.max(mLastActionDownY, eventY);
    }

    private int convertDpToPx(float dp) {
        return Math.round(getContext().getResources().getDisplayMetrics().density * dp);
    }

    public interface OnHandwritingListener {
        void onWritingFinished(List<List<PointF>> points);
    }

}
