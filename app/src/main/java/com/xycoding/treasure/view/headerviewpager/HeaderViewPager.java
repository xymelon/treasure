package com.xycoding.treasure.view.headerviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.xycoding.treasure.R;
import com.xycoding.treasure.utils.DeviceUtils;

/**
 * Created by xuyang on 2017/3/31.
 */
public class HeaderViewPager extends LinearLayout {

    private static final int INVALID_POINTER = -1;
    private static final int FAST_RETURN_TOP_TIME = 2000;

    private final Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    /**
     * 向上滑动偏移量
     */
    private int mTopOffset;
    /**
     * 向上滑动最大距离（等于header高度减去偏移量）
     */
    private int mMaxScrollY;
    private final int mTouchSlop;
    private final int mMinFlingVelocity;
    private final int mMaxFlingVelocity;
    private int mActivePointerId = INVALID_POINTER;
    private int mInitMotionY;
    private int mLastTouchY;
    private int mLastScrollerY;
    private boolean mFlingUp = false;
    private boolean mFlingChild = false;
    private boolean mFlingToTop = false;
    private ScrollableContainer mScrollableContainer;
    private OnScrollHeaderListener mOnScrollHeaderListener;

    private EdgeEffectCompat mEdgeEffectTop;
    private EdgeEffectCompat mEdgeEffectBottom;
    private boolean mEdgeEffectTopActive;
    private boolean mEdgeEffectBottomActive;

    public HeaderViewPager(Context context) {
        this(context, null);
    }

    public HeaderViewPager(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderViewPager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setWillNotDraw(false);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderViewPager);
        mTopOffset = typedArray.getDimensionPixelSize(typedArray.getIndex(R.styleable.HeaderViewPager_hvp_topOffset), 0);
        typedArray.recycle();

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
            mMaxScrollY = header.getMeasuredHeight() - mTopOffset;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + mMaxScrollY, MeasureSpec.EXACTLY);
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
                resetEdgeEffects();
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
                resetEdgeEffects();
                mActivePointerId = ev.getPointerId(0);
                mLastTouchY = Math.round(ev.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final int y = Math.round(ev.getY(pointerIndex));
                final int deltaY = mLastTouchY - y;
                scroll(deltaY);

                edgeEffectPull(ev.getX(pointerIndex), deltaY);
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

        if (mEdgeEffectTop != null) {
            mEdgeEffectTop.onRelease();
        }
        if (mEdgeEffectBottom != null) {
            mEdgeEffectBottom.onRelease();
        }
    }

    private void resetEdgeEffects() {
        mEdgeEffectTopActive = false;
        mEdgeEffectBottomActive = false;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mFlingToTop) {
                //快速滑到顶部
                final int deltaY = mLastScrollerY - mScroller.getCurrY();
                if (isScrollContainerTop()) {
                    scrollTo(0, getScrollY() + deltaY);
                } else {
                    scrollContent(deltaY);
                }
            } else {
                float currVelocity = mScroller.getCurrVelocity();
                if (mFlingUp) {
                    //向上fling时，若header已完全隐藏，则向上fling底部内容
                    if (isHeaderCollapseCompletely() && !mFlingChild) {
                        //保证fling一次
                        mFlingChild = true;
                        flingContent(Math.round(currVelocity));
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
                        flingContent(-Math.round(currVelocity));
                    }
                    //向下fling时，若header未完全展开时，则滑动header
                    if ((!isHeaderCollapseCompletely() && !isHeaderExpandCompletely())
                            || (isScrollContainerTop && !isHeaderExpandCompletely())) {
                        final int deltaY = mScroller.getCurrY() - mLastScrollerY;
                        scrollTo(0, getScrollY() + deltaY);
                    }
                }
            }
            mLastScrollerY = mScroller.getCurrY();
            invalidate();

            if (canOverScroll()) {
                ensureGlows();
                if (mFlingToTop || mFlingUp) {
                    //向上fling时，若已滑动到底部，触发底部边界动效
                    if (isScrollBottom() && !mEdgeEffectBottomActive) {
                        mEdgeEffectBottomActive = true;
                        mEdgeEffectBottom.onAbsorb((int) mScroller.getCurrVelocity());
                    }
                } else {
                    //向下fling时，若已滑动到顶部，触发顶部边界动效
                    if (isHeaderExpandCompletely() && !mEdgeEffectTopActive) {
                        mEdgeEffectTopActive = true;
                        mEdgeEffectTop.onAbsorb((int) mScroller.getCurrVelocity());
                    }
                }
            }
        }
    }

    @Override
    protected int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    protected int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (y > mMaxScrollY) {
            y = mMaxScrollY;
        } else if (y < 0) {
            y = 0;
        }
        if (mOnScrollHeaderListener != null && getScrollY() != y) {
            mOnScrollHeaderListener.onScroll(y, mMaxScrollY);
        }
        super.scrollTo(x, y);
    }

    public void scrollToTop() {
        int distance = getCurrentScrollY();
        int duration = FAST_RETURN_TOP_TIME;
        if (distance <= DeviceUtils.getScreenHeight(getContext())) {
            //滑动距离小于屏幕，时间减少
            duration = FAST_RETURN_TOP_TIME / 3;
        }
        mLastScrollerY = 0;
        mFlingToTop = true;
        mScroller.startScroll(0, 0, 0, distance, duration);
        invalidate();
    }

    private int getScrollRange() {
        int scrollRange = 0;
        scrollRange += mMaxScrollY; //加上header
        scrollRange += getContentHeaderGap(); //加上header与内容之间高度
        if (mScrollableContainer != null && mScrollableContainer.getScrollableView() != null) {
            //加上底部内容滚动范围
            scrollRange += mScrollableContainer.getScrollableView().computeVerticalScrollRange()
                    + mScrollableContainer.getScrollableView().getPaddingTop()
                    + mScrollableContainer.getScrollableView().getPaddingBottom();
        }
        scrollRange -= getHeight() - mMaxScrollY - getPaddingBottom() - getPaddingTop();
        return Math.max(0, scrollRange);
    }

    private int getCurrentScrollY() {
        int distance = 0;
        if (mScrollableContainer != null && mScrollableContainer.getScrollableView() != null) {
            distance = mScrollableContainer.getScrollableView().computeVerticalScrollOffset();
            //大于0说明底部内容已滚动
            if (distance > 0) {
                distance += getContentHeaderGap();
            }
        }
        //加上header滚动距离
        distance += getScrollY();
        return distance;
    }

    private int getContentHeaderGap() {
        //计算底部内容和header之间距离
        if (mScrollableContainer.getScrollableView().getParent() instanceof View && getChildAt(0) != null) {
            return ((View) mScrollableContainer.getScrollableView().getParent()).getTop() - getChildAt(0).getBottom();
        }
        return 0;
    }

    private void edgeEffectPull(float touchX, int deltaY) {
        if (canOverScroll()) {
            ensureGlows();
            if (isHeaderExpandCompletely() && deltaY < 0) {
                mEdgeEffectTopActive = true;
                //滑到顶部且向下滑时，触发顶部边界动效
                mEdgeEffectTop.onPull((float) deltaY / getHeight(), touchX / getWidth());
                if (!mEdgeEffectBottom.isFinished()) {
                    mEdgeEffectBottom.onRelease();
                }
            } else if (isScrollBottom() && deltaY > 0) {
                mEdgeEffectBottomActive = true;
                //滑到底部且向上滑时，触发底部边界动效
                mEdgeEffectBottom.onPull((float) deltaY / getHeight(), 1.f - touchX / getWidth());
                if (!mEdgeEffectTop.isFinished()) {
                    mEdgeEffectTop.onRelease();
                }
            }
            if (mEdgeEffectTop != null && mEdgeEffectBottom != null
                    && (!mEdgeEffectTop.isFinished() || !mEdgeEffectBottom.isFinished())) {
                invalidate();
            }
        }
    }

    private boolean canOverScroll() {
        final int overScrollMode = getOverScrollMode();
        return overScrollMode == View.OVER_SCROLL_ALWAYS || overScrollMode == View.OVER_SCROLL_IF_CONTENT_SCROLLS;
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
            scrollContent(dy);
        }
    }

    private void scrollContent(int dy) {
        if (mScrollableContainer != null && mScrollableContainer.getScrollableView() != null) {
            mScrollableContainer.getScrollableView().scrollBy(0, dy);
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
        mFlingToTop = false;
        mLastScrollerY = getScrollY();
        if ((mFlingUp && isScrollBottom())
                || (!mFlingUp && isHeaderExpandCompletely())) {
            //上滑且已滑到底部，或下滑且滑动顶部，不用fling
            return;
        }
        mScroller.fling(0, getScrollY(), 0, -Math.round(vy), 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        invalidate();
    }

    /**
     * fling底部内容
     *
     * @param vy
     */
    private void flingContent(int vy) {
        if (mScrollableContainer != null && mScrollableContainer.getScrollableView() != null) {
            mScrollableContainer.getScrollableView().fling(0, vy);
        }
    }

    /**
     * header是否完全隐藏
     *
     * @return
     */
    private boolean isHeaderCollapseCompletely() {
        return getScrollY() == mMaxScrollY;
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
     * 是否已滑动到底部
     *
     * @return
     */
    private boolean isScrollBottom() {
        return isHeaderCollapseCompletely()
                && !(mScrollableContainer == null || mScrollableContainer.getScrollableView() == null)
                && !canViewScrollDown(mScrollableContainer.getScrollableView());
    }


    private boolean canViewScrollUp(View view) {
        return ViewCompat.canScrollVertically(view, -1);
    }

    private boolean canViewScrollDown(View view) {
        return ViewCompat.canScrollVertically(view, 1);
    }

    public void setCurrentScrollableContainer(@NonNull ScrollableContainer scrollableContainer) {
        mScrollableContainer = scrollableContainer;
    }

    public void setOnScrollHeaderListener(@NonNull OnScrollHeaderListener listener) {
        mOnScrollHeaderListener = listener;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mEdgeEffectTop != null) {
            final int width = getWidth() - getPaddingLeft() - getPaddingRight();
            final int height = getHeight();
            if (!mEdgeEffectTop.isFinished()) {
                final int restoreCount = canvas.save();

                canvas.translate(getPaddingLeft(), getPaddingTop());
                mEdgeEffectTop.setSize(width, height);
                if (mEdgeEffectTop.draw(canvas)) {
                    invalidate();
                }
                canvas.restoreToCount(restoreCount);
            }
            if (!mEdgeEffectBottom.isFinished()) {
                final int restoreCount = canvas.save();

                canvas.translate(-width + getPaddingLeft(), height);
                canvas.rotate(180, width, 0);
                mEdgeEffectBottom.setSize(width, height);
                if (mEdgeEffectBottom.draw(canvas)) {
                    invalidate();
                }
                canvas.restoreToCount(restoreCount);
            }
        }
    }

    private void ensureGlows() {
        if (mScrollableContainer != null && mScrollableContainer.getScrollableView() != null) {
            //隐藏recycler view滑到顶部和底部时的效果
            mScrollableContainer.getScrollableView().setOverScrollMode(OVER_SCROLL_NEVER);
        }
        if (getOverScrollMode() != View.OVER_SCROLL_NEVER) {
            if (mEdgeEffectTop == null) {
                Context context = getContext();
                mEdgeEffectTop = new EdgeEffectCompat(context);
                mEdgeEffectBottom = new EdgeEffectCompat(context);
            }
        } else {
            mEdgeEffectTop = null;
            mEdgeEffectBottom = null;
        }
    }

    public interface ScrollableContainer {
        RecyclerView getScrollableView();
    }

    public interface OnScrollHeaderListener {
        void onScroll(int currentPosition, int maxPosition);
    }

}
