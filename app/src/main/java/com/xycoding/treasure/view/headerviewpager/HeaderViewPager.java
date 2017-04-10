package com.xycoding.treasure.view.headerviewpager;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * Created by xuyang on 2017/3/31.
 */
public class HeaderViewPager extends LinearLayout {

    private static final int INVALID_POINTER = -1;

    private final Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mHeaderHeight;
    private final int mTouchSlop;
    private final int mMinFlingVelocity;
    private final int mMaxFlingVelocity;
    private int mActivePointerId = INVALID_POINTER;
    private int mInitMotionY;
    private int mLastTouchY;
    private int mLastScrollerY;
    private ScrollableContainer mScrollableContainer;
    private boolean mFlingUp = false;
    private boolean mFlingChild = false;

    public HeaderViewPager(Context context) {
        this(context, null);
    }

    public HeaderViewPager(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderViewPager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (mScroller.computeScrollOffset()) {
            //强制child view不拦截事件
            super.requestDisallowInterceptTouchEvent(false);
        } else {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureVelocityTracker(ev);
        int pointerIndex;
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mInitMotionY = mLastTouchY = Math.round(ev.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final int y = Math.round(ev.getY(pointerIndex));
                final int dy = y - mInitMotionY;
                if (Math.abs(dy) > mTouchSlop) {
                    mLastTouchY = dy > 0 ? mInitMotionY + mTouchSlop : mInitMotionY - mTouchSlop;
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                final int actionIndex = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = ev.getPointerId(actionIndex);
                mInitMotionY = mLastTouchY = Math.round(ev.getY(actionIndex));
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                //未拦截事件时：手指滑动微小距离，需fling header
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                float vy = mVelocityTracker.getYVelocity(pointerIndex);
                if (Math.abs(vy) >= mMinFlingVelocity) {
                    fling(vy);
                }
                clearParams();
                break;
            case MotionEvent.ACTION_CANCEL:
                clearParams();
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        ensureVelocityTracker(ev);
        int pointerIndex;
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mLastTouchY = Math.round(ev.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final int y = Math.round(ev.getY(pointerIndex));
                scroll(Math.round(mLastTouchY - y));
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                final int actionIndex = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = ev.getPointerId(actionIndex);
                mLastTouchY = Math.round(ev.getY(actionIndex));
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                fling(mVelocityTracker.getYVelocity(pointerIndex));
                clearParams();
                break;
            case MotionEvent.ACTION_CANCEL:
                clearParams();
                break;
        }
        return true;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        if (ev.getPointerId(pointerIndex) == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
            mInitMotionY = mLastTouchY = Math.round(ev.getY(newPointerIndex));
        }
    }

    private void ensureVelocityTracker(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
    }

    private void clearParams() {
        mActivePointerId = INVALID_POINTER;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            float currVelocity = mScroller.getCurrVelocity();
            if (mFlingUp) {
                //向上fling时，若header已完全隐藏，则向上fling底部内容
                if (isHeaderCollapseCompletely() && !mFlingChild) {
                    //保证fling一次
                    mFlingChild = true;
                    int remainDistance = mScroller.getFinalY() - mScroller.getCurrY();
                    int remainDuration = mScroller.getDuration() - mScroller.timePassed();
                    flingContent(Math.round(currVelocity), remainDistance, remainDuration);
                } else {
                    final int deltaY = mScroller.getCurrY() - mLastScrollerY;
                    scrollTo(0, getScrollY() + deltaY);
                }
            } else {
                boolean isScrollContainerTop = isScrollContainerTop();
                //向下fling时，若底部内容未滑动到顶部时，则向下fling底部内容
                if (!isScrollContainerTop && !mFlingChild) {
                    //保证fling一次
                    mFlingChild = true;
                    int remainDistance = mScroller.getFinalY() - mScroller.getCurrY();
                    int remainDuration = mScroller.getDuration() - mScroller.timePassed();
                    flingContent(-Math.round(currVelocity), remainDistance, remainDuration);
                }
                //向下fling时，若header未完全展开时，则滑动header
                if ((!isHeaderCollapseCompletely() && !isHeaderExpandCompletely())
                        || (isScrollContainerTop && !isHeaderExpandCompletely())) {
                    final int deltaY = mScroller.getCurrY() - mLastScrollerY;
                    scrollTo(0, getScrollY() + deltaY);
                }
            }
            mLastScrollerY = mScroller.getCurrY();
            invalidate();
        }
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

    public void scrollToTop() {
//        int distance = getScrollY();
//        if (mScrollableContainer != null && mScrollableContainer.getScrollableView() != null) {
//            if (mScrollableContainer.getScrollableView() instanceof RecyclerView) {
//                distance += ((RecyclerView) mScrollableContainer.getScrollableView()).computeVerticalScrollOffset();
//            } else {
//                distance += mScrollableContainer.getScrollableView().getScrollY();
//            }
//        }
//        mIntercepted = false;
//        mScroller.startScroll(0, 0, 0, distance);
    }

    private void scroll(int dy) {
        if (dy == 0) {
            return;
        }
        boolean isScrollContainerTop = isScrollContainerTop();
        if (isScrollContainerTop || !isHeaderCollapseCompletely()) {
            //当底部内容滑动到顶部或header未完全隐藏时，滑动header
            scrollBy(0, dy);
        }
        if ((!isScrollContainerTop && isHeaderExpandCompletely()) || isHeaderCollapseCompletely()) {
            //当底部内容未滑动到顶部且header完全展开或header完全隐藏时，滑动底部内容
            if (mScrollableContainer != null && mScrollableContainer.getScrollableView() != null) {
                mScrollableContainer.getScrollableView().scrollBy(0, dy);
            }
        }
    }

    private void fling(float vy) {
        if (vy == 0) {
            return;
        }
        mScroller.abortAnimation();
        //上滑速度小于0，下滑速度大于0
        mFlingUp = vy < 0;
        mFlingChild = false;
        mLastScrollerY = getScrollY();
        mScroller.fling(0, getScrollY(), 0, -Math.round(vy), 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        invalidate();
    }

    /**
     * fling底部内容
     *
     * @param vy
     * @param distance
     * @param duration
     */
    private void flingContent(int vy, int distance, int duration) {
        if (mScrollableContainer != null && mScrollableContainer.getScrollableView() != null) {
            if (mScrollableContainer.getScrollableView() instanceof AbsListView) {
                AbsListView absListView = (AbsListView) mScrollableContainer.getScrollableView();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    absListView.fling(vy);
                } else {
                    absListView.smoothScrollBy(distance, duration);
                }
            } else if (mScrollableContainer.getScrollableView() instanceof ScrollView) {
                ((ScrollView) mScrollableContainer.getScrollableView()).fling(vy);
            } else if (mScrollableContainer.getScrollableView() instanceof RecyclerView) {
                ((RecyclerView) mScrollableContainer.getScrollableView()).fling(0, vy);
            } else if (mScrollableContainer.getScrollableView() instanceof WebView) {
                ((WebView) mScrollableContainer.getScrollableView()).flingScroll(0, vy);
            }
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
     * header是否完全展开
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
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

    public void setCurrentScrollableContainer(@NonNull ScrollableContainer scrollableContainer) {
        mScrollableContainer = scrollableContainer;
    }

}
