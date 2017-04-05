package com.xycoding.treasure.view;

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
    private float mInitMotionY, mInitMotionDownY, mInitMotionPointerDownY;
    private float mLastTouchY;
    private int mLastScrollerY;
    private boolean mFlingUp = false;
    /**
     * header view pager 是否拦截事件
     */
    private boolean mIntercepted = false;
    private ScrollableContainer mScrollableContainer;

    /* 跟踪底部内容变量 begin */
    private VelocityTracker mTrackVelocityTracker;
    private int mTrackActivePointerId = INVALID_POINTER;
    private float mTrackInitMotionX, mTrackInitMotionDownX, mTrackInitMotionPointerDownX;
    private float mTrackInitMotionY, mTrackInitMotionDownY, mTrackInitMotionPointerDownY;
    private float mTrackLastTouchY;
    /**
     * 底部内容是否横向滑动
     */
    private boolean mTrackHorizontalIntercepted = false;
    /**
     * 底部内容是否竖向滑动
     */
    private boolean mTrackVerticalIntercepted = false;
    /* 跟踪底部内容变量 end */

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (MotionEventCompat.getActionMasked(ev) == MotionEvent.ACTION_DOWN) {
            mScroller.abortAnimation();
        }
        super.dispatchTouchEvent(ev);
        trackTouchEvent(ev);
        return true;
    }

    /**
     * 未拦截事件（底部内容消费事件）时，跟踪底部内容事件；
     * 当底部内容滚动或fling到顶部时，需判断header是否已完全展开，再进行header滚动。
     *
     * @param ev
     * @return
     */
    private void trackTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            clearTrackParams();
        }
        if (mTrackVelocityTracker == null) {
            mTrackVelocityTracker = VelocityTracker.obtain();
        }
        mTrackVelocityTracker.addMovement(ev);
        int pointerIndex;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTrackActivePointerId = ev.getPointerId(0);
                pointerIndex = ev.findPointerIndex(mTrackActivePointerId);
                if (pointerIndex < 0) {
                    return;
                }
                mTrackInitMotionX = mTrackInitMotionDownX = ev.getX(pointerIndex);
                mTrackInitMotionY = mTrackInitMotionDownY = mTrackLastTouchY = ev.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mTrackActivePointerId);
                if (pointerIndex < 0) {
                    return;
                }
                final float y = ev.getY(pointerIndex);
                final int yDiff = Math.round(mTrackLastTouchY - y);
                mTrackLastTouchY = y;
                if (mTrackVerticalIntercepted || mTrackHorizontalIntercepted) {
                    if (mTrackHorizontalIntercepted) {
                        //底部内容横向滑动，直接返回
                        return;
                    }
                    trackScroll(yDiff);
                    return;
                }
                final float x = ev.getX(pointerIndex);
                final float dx = Math.abs(x - mTrackInitMotionX);
                final float dy = Math.abs(y - mTrackInitMotionY);
                if (dx > mTouchSlop && dx > dy) {
                    mTrackHorizontalIntercepted = true;
                }
                if (dy > mTouchSlop && dy > dx) {
                    mTrackVerticalIntercepted = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                pointerIndex = ev.findPointerIndex(mTrackActivePointerId);
                if (pointerIndex < 0) {
                    return;
                }
                mTrackInitMotionX = mTrackInitMotionPointerDownX = ev.getX(pointerIndex);
                mTrackInitMotionY = mTrackInitMotionPointerDownY = ev.getY(pointerIndex);
                mTrackActivePointerId = ev.getPointerId(pointerIndex);
                mTrackLastTouchY = ev.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                final int prePointerId = mTrackActivePointerId;
                onTrackSecondaryPointerUp(ev);
                if (prePointerId != mActivePointerId) {
                    //切换手指，改变起始位置
                    mTrackInitMotionX = mTrackInitMotionX == mTrackInitMotionDownX ? mTrackInitMotionPointerDownX : mTrackInitMotionDownX;
                    mTrackInitMotionY = mTrackInitMotionY == mTrackInitMotionDownY ? mTrackInitMotionPointerDownY : mTrackInitMotionDownY;
                }
                mTrackLastTouchY = ev.getY(ev.findPointerIndex(mTrackActivePointerId));
                break;
            case MotionEvent.ACTION_UP:
                pointerIndex = ev.findPointerIndex(mTrackActivePointerId);
                if (pointerIndex < 0) {
                    return;
                }
                if (mTrackVerticalIntercepted) {
                    mTrackVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                    trackFling(mTrackVelocityTracker.getYVelocity(pointerIndex));
                }
                break;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //header完全隐藏且底部内容未在顶部时，不拦截事件
        if (isHeaderCollapseCompletely() && !isScrollContainerTop()) {
            mIntercepted = false;
            return false;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        int pointerIndex;
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                mIntercepted = false;
                mActivePointerId = ev.getPointerId(0);
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitMotionY = mInitMotionDownY = ev.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                final float dy = y - mInitMotionY;
                if (Math.abs(dy) > mTouchSlop) {
                    //header完全隐藏且向上滑动时，不拦截事件
                    if (isHeaderCollapseCompletely() && dy < 0) {
                        mIntercepted = false;
                        return false;
                    }
                    mLastTouchY = dy > 0 ? mInitMotionY + mTouchSlop : mInitMotionY - mTouchSlop;
                    mIntercepted = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitMotionY = mInitMotionPointerDownY = ev.getY(pointerIndex);
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                final int prePointerId = mActivePointerId;
                onSecondaryPointerUp(ev);
                if (prePointerId != mActivePointerId) {
                    //切换手指，改变起始位置
                    mInitMotionY = mInitMotionY == mInitMotionDownY ? mInitMotionPointerDownY : mInitMotionDownY;
                }
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
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        int pointerIndex;
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                mIntercepted = false;
                mActivePointerId = ev.getPointerId(0);
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mLastTouchY = ev.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mIntercepted = true;
                final float y = ev.getY(pointerIndex);
                scroll(Math.round(mLastTouchY - y));
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                mLastTouchY = ev.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                mLastTouchY = ev.getY(ev.findPointerIndex(mActivePointerId));
                break;
            case MotionEvent.ACTION_UP:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mIntercepted = true;
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

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            float currVelocity = mScroller.getCurrVelocity();
            if (mIntercepted) {
                //当前view拦截事件
                if (mFlingUp) {
                    //向上fling时，若header已完全隐藏，则向上fling底部内容
                    if (isHeaderCollapseCompletely()) {
                        int remainDistance = mScroller.getFinalY() - mScroller.getCurrY();
                        int remainDuration = mScroller.getDuration() - mScroller.timePassed();
                        flingContent(Math.round(currVelocity), remainDistance, remainDuration);
                    } else {
                        scrollTo(0, mScroller.getCurrY());
                    }
                } else {
                    //向下fling时，若header已完全展开且底部内容未滑动到顶部时，则向下fling底部内容
                    if (isHeaderExpandCompletely() && !isScrollContainerTop()) {
                        int remainDistance = mScroller.getFinalY() - mScroller.getCurrY();
                        int remainDuration = mScroller.getDuration() - mScroller.timePassed();
                        flingContent(-Math.round(currVelocity), remainDistance, remainDuration);
                    }
                    //向下fling时，若header未完全展开时，则滚动header
                    if (!isHeaderExpandCompletely()) {
                        scrollTo(0, mScroller.getCurrY());
                    }
                }
            } else {
                //底部内容消费事件且向下fling时，若底部内容已到顶部，则开始滚动header
                if (!mFlingUp && isScrollContainerTop()) {
                    final int deltaY = mScroller.getCurrY() - mLastScrollerY;
                    scrollTo(0, getScrollY() + deltaY);
                }
            }
            invalidate();
            mLastScrollerY = mScroller.getCurrY();
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

    private void onTrackSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mTrackActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mTrackActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private void clearParams() {
        mActivePointerId = INVALID_POINTER;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void clearTrackParams() {
        mTrackVerticalIntercepted = false;
        mTrackHorizontalIntercepted = false;
        mTrackActivePointerId = INVALID_POINTER;
        if (mTrackVelocityTracker != null) {
            mTrackVelocityTracker.recycle();
            mTrackVelocityTracker = null;
        }
    }

    private void scroll(int dy) {
        if (dy == 0) {
            return;
        }
        boolean isScrollContainerTop = isScrollContainerTop();
        if (isScrollContainerTop || !isHeaderCollapseCompletely()) {
            //当底部内容在顶部或header未完全隐藏时，滑动header
            scrollBy(0, dy);
        }
        if (!isScrollContainerTop || isHeaderCollapseCompletely()) {
            //当header完全隐藏时，滑动底部内容
            if (mScrollableContainer != null && mScrollableContainer.getScrollableView() != null) {
                mScrollableContainer.getScrollableView().scrollBy(0, dy);
            }
        }
    }

    private void trackScroll(int dy) {
        if (mIntercepted || dy == 0) {
            return;
        }
        boolean isScrollContainerTop = isScrollContainerTop();
        if (isScrollContainerTop || !isHeaderCollapseCompletely()) {
            //当底部内容在顶部或header未完全隐藏时，滚动header
            scrollBy(0, dy);
            //异常情况：向下滚动且开始滚动header时，若底部内容未滚动到顶部时，需手动指定滚动
            if (dy < 0 && !isScrollContainerTop) {
                if (mScrollableContainer != null && mScrollableContainer.getScrollableView() != null) {
                    mScrollableContainer.getScrollableView().scrollBy(0, dy);
                }
            }
        }
    }

    private void fling(float vy) {
        if (vy == 0) {
            return;
        }
        //上滑速度小于0，下滑速度大于0
        mFlingUp = vy < 0;
        mLastScrollerY = getScrollY();
        mScroller.fling(0, getScrollY(), 0, -Math.round(vy), 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        invalidate();
    }

    private void trackFling(float vy) {
        if (mIntercepted) {
            return;
        }
        fling(vy);
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

    public interface ScrollableContainer {
        View getScrollableView();
    }

}
