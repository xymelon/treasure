package com.xycoding.treasure.view.headerviewpager;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by xuyang on 2017/3/31.
 */
public class HeaderViewPager1 extends LinearLayout {

    private static final int INVALID_POINTER = -1;

    private final Scroller mScroller;
    private HeaderScrollHelper.ScrollableContainer mScrollableContainer;
    private int mHeaderHeight;
    private final int mTouchSlop;
    private final int mMinFlingVelocity;
    private final int mMaxFlingVelocity;
    private int mActivePointerId = INVALID_POINTER;
    private float mInitMotionX, mInitMotionDownX, mInitMotionPointerDownX;
    private float mInitMotionY, mInitMotionDownY, mInitMotionPointerDownY;
    private float mLastTouchY;
    private boolean mVerticalScroll = false;

    public HeaderViewPager1(Context context) {
        this(context, null);
    }

    public HeaderViewPager1(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderViewPager1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);

        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();

        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量需加上header高度
        View header = getChildAt(0);
        if (header != null) {
            measureChildWithMargins(header, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0);
            mHeaderHeight = header.getMeasuredHeight();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + mHeaderHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int pointerIndex;
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mInitMotionX = mInitMotionDownX = ev.getX(mActivePointerId);
                mInitMotionY = mLastTouchY = mInitMotionDownY = ev.getY(mActivePointerId);
                mScroller.abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float focusX = ev.getX(pointerIndex);
                final float focusY = ev.getY(pointerIndex);
                //判断是否竖直滑动
                final float touchDistanceX = Math.abs(focusX - mInitMotionX);
                final float touchDistanceY = Math.abs(focusY - mInitMotionY);
                if (touchDistanceX > mTouchSlop && touchDistanceX > touchDistanceY) {
                    mVerticalScroll = false;
                } else if (touchDistanceY > mTouchSlop && touchDistanceY > touchDistanceX) {
                    mVerticalScroll = true;
                }
                if (mVerticalScroll) {
                    //scroll container已到顶部或header未完全隐藏时触发
                    if (isScrollContainerTop() || !isHeaderCollapseCompletely()) {
                        scrollBy(0, Math.round(mLastTouchY - focusY));
                    }
                }
                mLastTouchY = focusY;
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:
                pointerIndex = MotionEventCompat.getActionIndex(ev);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitMotionPointerDownX = ev.getX(pointerIndex);
                mInitMotionPointerDownY = ev.getY(pointerIndex);
                mLastTouchY = ev.getY(pointerIndex);
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                final float prePointerId = mActivePointerId;
                onSecondaryPointerUp(ev);
                if (prePointerId != mActivePointerId) {
                    //切换手指，改变位置
                    mInitMotionX = mInitMotionX == mInitMotionDownX ? mInitMotionPointerDownX : mInitMotionDownX;
                    mInitMotionY = mInitMotionY == mInitMotionDownY ? mInitMotionPointerDownY : mInitMotionDownY;
                    mLastTouchY = ev.getY(ev.findPointerIndex(mActivePointerId));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER;
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (y > mHeaderHeight) {
            y = mHeaderHeight;
        } else if (y < 0) {
            y = 0;
        }
        super.scrollTo(x, y);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    /**
     * header是否完全隐藏
     *
     * @return
     */
    private boolean isHeaderCollapseCompletely() {
        return getScrollY() == mHeaderHeight;
    }

    /**
     * header是否完全可见
     *
     * @return
     */
    private boolean isHeaderExpandCompletely() {
        return getScrollY() == 0;
    }

    /**
     * scroll container是否已滑动到顶部
     *
     * @return
     */
    private boolean isScrollContainerTop() {
        return mScrollableContainer == null
                || mScrollableContainer.getScrollableView() == null
                || !canViewScrollUp(mScrollableContainer.getScrollableView());
    }

    /**
     * 判断当前view是否往上滑动
     *
     * @param view
     * @return
     */
    private boolean canViewScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 ||
                        absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(view, -1) || view.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(view, -1);
        }
    }

    public void setCurrentScrollableContainer(@NonNull HeaderScrollHelper.ScrollableContainer scrollableContainer) {
        mScrollableContainer = scrollableContainer;
    }

}
