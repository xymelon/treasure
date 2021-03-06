package com.xycoding.treasure.view.headerviewpager;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.xycoding.treasure.R;

import java.lang.reflect.Method;

/**
 * Created by xuyang on 2017/3/31.
 */
public class HeaderViewPager_ScrollBarBitmap extends LinearLayout {

    private static final int INVALID_POINTER = -1;
    //快速回到顶部最大时长
    private static final int FAST_RETURN_TOP_TIME = 1000;
    //fling时，剩余距离大于此指才能显示scroll bar
    private static final int SCROLL_BAR_MAX_ALPHA = 255;
    //停止滑动时，scroll bar显示时长
    private static final int SCROLL_BAR_SHOW_DURATION = 2000;
    private static final int SCROLL_BAR_MIN_DISTANCE = 10;
    private static final int SCROLL_BAR_FADE_DURATION = 100;
    private static final int SCROLL_BAR_FADE_STEP = 16;
    private static final int SCROLL_BAR_FADE_ALPHA_STEP = SCROLL_BAR_MAX_ALPHA / (SCROLL_BAR_FADE_DURATION / SCROLL_BAR_FADE_STEP);
    private final Paint mScrollBarAlphaPaint = new Paint();
    private int mScrollBarAlpha = SCROLL_BAR_MAX_ALPHA;
    private Runnable mScrollBarHideRunnable;
    private Bitmap mScrollBarBitmap;
    private RectF mScrollBarBitmapRectF = new RectF();
    private RectF mScrollBarEventRectF = new RectF();
    private final int mScrollBarMinHotArea;
    private int mScrollBarBitmapMarginTop, mScrollbarBitmapMarginBottom;
    private boolean mScrollBarFadingOut = false;
    private boolean mScrollBarConsumeEvent = false;
    private boolean mShowScrollBar = false;
    //标志scroll bar是否向上滚动
    private boolean mScrollBarUp = false;
    private OnScrollBarClickListener mOnScrollBarClickListener;
    private OnFastBackVisibleListener mFastBackVisibleListener;

    private Rect mHeaderViewPagerRect = new Rect();
    private final Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    //向上滑动偏移量
    private int mTopOffset;
    //向上滑动最大距离（等于header高度减去偏移量）
    private int mMaxScrollY;
    private final int mTouchSlop;
    private final int mMinFlingVelocity;
    private final int mMaxFlingVelocity;
    private int mActivePointerId = INVALID_POINTER;
    private int mInitMotionY;
    private int mLastTouchY;
    private int mLastScrollerY;
    //标志是否向上fling
    private boolean mFlingUp = false;
    //标志是否fling内容
    private boolean mFlingContent = false;
    //标志是否fling到顶部
    private boolean mFlingToTop = false;
    private ScrollableContainer mScrollableContainer;
    private ViewPager mViewPager;
    private OnScrollHeaderListener mOnScrollHeaderListener;

    private EdgeEffectCompat mEdgeEffectTop;
    private EdgeEffectCompat mEdgeEffectBottom;
    private boolean mEdgeEffectTopActive;
    private boolean mEdgeEffectBottomActive;

    public HeaderViewPager_ScrollBarBitmap(Context context) {
        this(context, null);
    }

    public HeaderViewPager_ScrollBarBitmap(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderViewPager_ScrollBarBitmap(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setWillNotDraw(false);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderViewPager);
        mTopOffset = typedArray.getDimensionPixelSize(R.styleable.HeaderViewPager_hvp_topOffset, 0);
        mScrollBarBitmap = drawableToBitmap(typedArray.getDrawable(R.styleable.HeaderViewPager_hvp_scrollbar));
        mScrollBarBitmapMarginTop = typedArray.getDimensionPixelSize(R.styleable.HeaderViewPager_hvp_scrollbar_marginTop, 0);
        mScrollbarBitmapMarginBottom = typedArray.getDimensionPixelSize(R.styleable.HeaderViewPager_hvp_scrollbar_marginBottom, 0);
        typedArray.recycle();

        //scroll bar最小点击热区
        mScrollBarMinHotArea = dp2px(context, 36);

        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();

        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (onlyOneDirectChildVisible()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
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
        if (onlyOneDirectChildVisible()) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
            return;
        }
        if (mScroller.computeScrollOffset()) {
            //强制child view不拦截事件
            super.requestDisallowInterceptTouchEvent(false);
        } else {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (onlyOneDirectChildVisible()) {
            return super.onInterceptTouchEvent(ev);
        }

        ensureVelocityTracker(ev);
        int pointerIndex;
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                resetEdgeEffects();
                mActivePointerId = ev.getPointerId(0);
                mInitMotionY = mLastTouchY = Math.round(ev.getY());
                if (isScrollBarConsumeEvent(ev)) {
                    //scroll bar消费事件
                    return true;
                }
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
        if (onlyOneDirectChildVisible()) {
            return super.onTouchEvent(ev);
        }

        ensureVelocityTracker(ev);
        int pointerIndex;
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                resetEdgeEffects();
                mActivePointerId = ev.getPointerId(0);
                mLastTouchY = Math.round(ev.getY());
                isScrollBarConsumeEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final int y = Math.round(ev.getY(pointerIndex));
                final int deltaY = mLastTouchY - y;
                if (!mScrollBarConsumeEvent) {
                    scroll(deltaY);
                    edgeEffectPull(ev.getX(pointerIndex), deltaY);
                }
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
                if (mScrollBarConsumeEvent) {
                    //双重验证，判定up事件是否在scroll bar热区
                    if (isScrollBarConsumeEvent(ev)) {
                        dispatchScrollBarClickEvent();
                    }
                } else {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                    fling(mVelocityTracker.getYVelocity(pointerIndex));
                }
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

    private boolean isScrollBarConsumeEvent(MotionEvent ev) {
        if (isScrollBarShow()) {
            mScrollBarEventRectF.set(mScrollBarBitmapRectF);
            //扩展scroll bar点击热区
            if (mScrollBarEventRectF.width() < mScrollBarMinHotArea) {
                float expand = (mScrollBarMinHotArea - mScrollBarEventRectF.width()) / 2;
                mScrollBarEventRectF.left -= expand;
                mScrollBarEventRectF.right += expand;
            }
            if (mScrollBarEventRectF.height() < mScrollBarMinHotArea) {
                float expand = (mScrollBarMinHotArea - mScrollBarEventRectF.height()) / 2;
                mScrollBarEventRectF.top -= expand;
                mScrollBarEventRectF.bottom += expand;
            }
            mScrollBarConsumeEvent = mScrollBarEventRectF.contains(ev.getX(), ev.getY());
            if (mScrollBarConsumeEvent) {
                showScrollBar();
            }
        } else {
            mScrollBarConsumeEvent = false;
        }
        return mScrollBarConsumeEvent;
    }

    private void dispatchScrollBarClickEvent() {
        if (mOnScrollBarClickListener != null) {
            //计算当前scroll bar全局中心点
            int[] location = new int[2];
            getLocationOnScreen(location);
            mOnScrollBarClickListener.onClick(
                    Math.round(location[1] + mScrollBarBitmapRectF.centerY()),
                    location[1] + mScrollBarBitmapMarginTop,
                    location[1] + computeVerticalScrollExtent() + mTopOffset - mScrollbarBitmapMarginBottom);
        }
    }

    private void dispatchFastBackVisibleEvent() {
        if (mFastBackVisibleListener != null) {
            mFastBackVisibleListener.onVisible(mScrollBarUp && isHeaderCollapseCompletely());
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
        fadeOutScrollBar();
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
                final View view = getCurrentScrollableView();
                if (isScrollContainerTop() || view instanceof WebView) {
                    scrollTo(0, getScrollY() + deltaY);
                    if (view instanceof WebView) {
                        view.scrollTo(0, 0);
                    }
                } else {
                    scrollContent(deltaY);
                }
            } else {
                final int distance = mScroller.getFinalY() - mScroller.getCurrY();
                if (!mShowScrollBar) {
                    //fling时，剩余距离大于指定阈值时，显示scroll bar
                    if (Math.abs(distance) >= SCROLL_BAR_MIN_DISTANCE) {
                        showScrollBar();
                    }
                }
                final int duration = mScroller.getDuration() - mScroller.timePassed();
                final float currVelocity = mScroller.getCurrVelocity();
                if (mFlingUp) {
                    //向上fling时，若header已完全隐藏，则向上fling底部内容
                    if (isHeaderCollapseCompletely() && !mFlingContent) {
                        //保证fling一次
                        mFlingContent = true;
                        flingContent(Math.round(currVelocity), distance, duration);
                    } else {
                        final int deltaY = mScroller.getCurrY() - mLastScrollerY;
                        scrollTo(0, getScrollY() + deltaY);
                    }
                } else {
                    boolean isScrollContainerTop = isScrollContainerTop();
                    //向下fling时，若底部内容未滑动到顶部时，则向下fling底部内容
                    if (!isScrollContainerTop && !mFlingContent) {
                        //保证fling一次
                        mFlingContent = true;
                        flingContent(-Math.round(currVelocity), distance, duration);
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
                        mScroller.abortAnimation();
                        mEdgeEffectBottomActive = true;
                        mEdgeEffectBottom.onAbsorb((int) mScroller.getCurrVelocity());
                    }
                } else {
                    //向下fling时，若已滑动到顶部，触发顶部边界动效
                    if (isScrollTop() && !mEdgeEffectTopActive) {
                        mScroller.abortAnimation();
                        mEdgeEffectTopActive = true;
                        mEdgeEffectTop.onAbsorb((int) mScroller.getCurrVelocity());
                        dispatchFastBackVisibleEvent();
                    }
                }
            }
        } else {
            fadeOutScrollBar();
        }
    }

    private boolean isScrollBarShow() {
        return mShowScrollBar || mScrollBarFadingOut;
    }

    private void showScrollBar() {
        if (getCurrentScrollableView() instanceof RecyclerView) {
            removeCallbacks(mScrollBarHideRunnable);
            mShowScrollBar = true;
            invalidate(
                    Math.round(mScrollBarBitmapRectF.left),
                    Math.round(mScrollBarBitmapRectF.top),
                    Math.round(mScrollBarBitmapRectF.right),
                    Math.round(mScrollBarBitmapRectF.bottom));
        }
    }

    private void fadeOutScrollBar() {
        if (!mShowScrollBar) {
            return;
        }
        removeCallbacks(mScrollBarHideRunnable);
        if (mScrollBarHideRunnable == null) {
            mScrollBarHideRunnable = new Runnable() {
                @Override
                public void run() {
                    hideScrollBar();
                }
            };
        }
        postDelayed(mScrollBarHideRunnable, SCROLL_BAR_SHOW_DURATION);
    }

    private void hideScrollBar() {
        mShowScrollBar = false;
        invalidate(
                Math.round(mScrollBarBitmapRectF.left),
                Math.round(mScrollBarBitmapRectF.top),
                Math.round(mScrollBarBitmapRectF.right),
                Math.round(mScrollBarBitmapRectF.bottom));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (onlyOneDirectChildVisible()) {
            return;
        }

        if (mEdgeEffectTop != null) {
            final int width = getWidth() - getPaddingLeft() - getPaddingRight();
            final int height = getHeight();
            if (!mEdgeEffectTop.isFinished()) {
                //绘制顶部边界动效
                final int restoreCount = canvas.save();
                canvas.translate(getPaddingLeft(), getPaddingTop());
                mEdgeEffectTop.setSize(width, height);
                if (mEdgeEffectTop.draw(canvas)) {
                    invalidate();
                }
                canvas.restoreToCount(restoreCount);
            }
            if (!mEdgeEffectBottom.isFinished()) {
                //绘制底部边界动效
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

        if (mScrollBarBitmap != null) {
            if (mShowScrollBar) {
                mScrollBarFadingOut = false;
                mScrollBarAlpha = SCROLL_BAR_MAX_ALPHA;
                mScrollBarAlphaPaint.setAlpha(mScrollBarAlpha);
                //绘制scroll bar
                final int scrollRange = computeVerticalScrollRange();
                final int scrollExtent = computeVerticalScrollExtent();
                final int scrollbarRange = scrollExtent + mTopOffset
                        - mScrollBarBitmapMarginTop - mScrollbarBitmapMarginBottom - mScrollBarBitmap.getHeight();
                float currentPercent = computeVerticalScrollOffset() * 1.f / (scrollRange - scrollExtent);
                currentPercent = currentPercent > 1.f ? 1.f : currentPercent;
                float scrollBarTop = mScrollBarBitmapMarginTop + currentPercent * scrollbarRange;
                if (!mScrollBarBitmapRectF.isEmpty()) {
                    float dy = scrollBarTop - mScrollBarBitmapRectF.top;
                    //若当前用户向上或向下滚动，但计算出的新位置相反，则保持scroll bar静止
                    if (mScrollBarUp) {
                        scrollBarTop = dy < 0 ? scrollBarTop : mScrollBarBitmapRectF.top;
                    } else {
                        scrollBarTop = dy > 0 ? scrollBarTop : mScrollBarBitmapRectF.top;
                    }
                }
                mScrollBarBitmapRectF.set(
                        getPaddingLeft() + getWidth() - mScrollBarBitmap.getWidth(),
                        scrollBarTop,
                        getPaddingLeft() + getWidth() + getPaddingRight(),
                        scrollBarTop + mScrollBarBitmap.getHeight());
                drawScrollBar(canvas);
            } else {
                //fade隐藏scroll bar
                mScrollBarAlpha -= SCROLL_BAR_FADE_ALPHA_STEP;
                if (mScrollBarAlpha >= 0 && !mScrollBarBitmapRectF.isEmpty()) {
                    mScrollBarFadingOut = true;
                    mScrollBarAlphaPaint.setAlpha(mScrollBarAlpha);
                    drawScrollBar(canvas);
                    postInvalidateDelayed(
                            SCROLL_BAR_FADE_STEP,
                            Math.round(mScrollBarBitmapRectF.left),
                            Math.round(mScrollBarBitmapRectF.top),
                            Math.round(mScrollBarBitmapRectF.right),
                            Math.round(mScrollBarBitmapRectF.bottom));
                } else {
                    mScrollBarFadingOut = false;
                    mScrollBarBitmapRectF.setEmpty();
                }
            }
        }
    }

    private void drawScrollBar(Canvas canvas) {
        final int restoreCount = canvas.save();
        canvas.translate(0, getScrollY());
        canvas.drawBitmap(mScrollBarBitmap, null, mScrollBarBitmapRectF, mScrollBarAlphaPaint);
        canvas.restoreToCount(restoreCount);
    }

    @Override
    protected int computeVerticalScrollRange() {
        if (onlyOneDirectChildVisible()) {
            return super.computeVerticalScrollRange();
        }

        int range = 0;
        View view = getCurrentScrollableView();
        if (view != null) {
            int scrollExtent = compute(view, "computeVerticalScrollExtent");
            int scrollRange = compute(view, "computeVerticalScrollRange");
            if (scrollExtent == scrollRange) {
                //底部内容不能滚动，直接返回当前view高度
                return getHeight() - mTopOffset;
            }
            range = scrollRange;
        }
        range += mMaxScrollY + getContentHeaderGap();
        return range;
    }

    @Override
    protected int computeVerticalScrollOffset() {
        if (onlyOneDirectChildVisible()) {
            return super.computeVerticalScrollOffset();
        }

        int offset = getScrollY();
        View view = getCurrentScrollableView();
        if (view != null) {
            offset += compute(view, "computeVerticalScrollOffset");
        }
        return offset;
    }

    @Override
    protected int computeVerticalScrollExtent() {
        if (onlyOneDirectChildVisible()) {
            return super.computeVerticalScrollExtent();
        }

        if (mHeaderViewPagerRect.isEmpty()) {
            getGlobalVisibleRect(mHeaderViewPagerRect);
        }
        return mHeaderViewPagerRect.height() - mTopOffset;
    }

    private int compute(View view, String methodName) {
        try {
            Method method = view.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return (int) method.invoke(view);
        } catch (Exception e) {
            //do nothing.
        }
        return 0;
    }

    private boolean onlyOneDirectChildVisible() {
        int visibleCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == VISIBLE) {
                visibleCount++;
                if (visibleCount > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private void ensureGlows() {
        if (getCurrentScrollableView() != null) {
            //隐藏recycler view滑到顶部和底部时的效果
            getCurrentScrollableView().setOverScrollMode(OVER_SCROLL_NEVER);
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

    private void edgeEffectPull(float touchX, int deltaY) {
        if (canOverScroll()) {
            ensureGlows();
            if (isScrollTop() && deltaY < 0) {
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

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (onlyOneDirectChildVisible()) {
            super.scrollTo(x, y);
            return;
        }

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
        hideScrollBar();
        int distance = getCurrentScrollY();
        int duration = FAST_RETURN_TOP_TIME;
        final int screenHeight = getResources().getDisplayMetrics().heightPixels;
        if (distance <= screenHeight) {
            //滑动距离小于屏幕，时间减少
            duration = FAST_RETURN_TOP_TIME / 3;
        }
        //hacky: 极端情况计算的距离总要差点，手动增加半个屏幕高度
        distance += screenHeight / 2;
        mLastScrollerY = 0;
        mFlingToTop = true;
        mScroller.startScroll(0, 0, 0, distance, duration);
        invalidate();
        scrollToTopViewPagerChildren();
    }

    public void scrollToTopImmediately() {
        hideScrollBar();
        mLastScrollerY = 0;
        scrollTo(0, 0);

        View view = getCurrentScrollableView();
        if (view != null) {
            if (view instanceof RecyclerView) {
                ((RecyclerView) view).scrollToPosition(0);
            } else {
                view.scrollTo(0, 0);
            }
        }
        scrollToTopViewPagerChildren();
    }

    private void scrollToTopViewPagerChildren() {
        if (mViewPager != null) {
            final int count = mViewPager.getChildCount();
            for (int i = 0; i < count; i++) {
                if (i != mViewPager.getCurrentItem()) {
                    final View child = mViewPager.getChildAt(i);
                    if (child instanceof RecyclerView) {
                        ((RecyclerView) child).scrollToPosition(0);
                    } else {
                        child.scrollTo(0, 0);
                    }
                }
            }
        }
    }

    public void scrollToPosition(int position) {
        if (!isHeaderCollapseCompletely()) {
            //若header未完全隐藏，则smooth隐藏header
            mFlingUp = true;
            mFlingContent = true;
            mFlingToTop = false;
            mLastScrollerY = 0;
            mScroller.startScroll(0, 0, 0, mMaxScrollY - getScrollY(), 100);
            invalidate();
        }
        View view = getCurrentScrollableView();
        if (view instanceof RecyclerView) {
            ((RecyclerView) view).smoothScrollToPosition(position);
        }
    }

    private int getCurrentScrollY() {
        int distance = 0;
        View view = getCurrentScrollableView();
        if (view != null) {
            distance = compute(view, "computeVerticalScrollOffset");
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
        try {
            //计算底部内容和header之间距离
            int height = 0;
            final int count = getChildCount();
            final ViewParent parent = getCurrentScrollableView().getParent();
            for (int i = 1; i < count; i++) {
                final View child = getChildAt(i);
                if (isViewParent(child, parent)) {
                    break;
                } else {
                    height += child.getHeight();
                }
            }
            return height;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 判断child的parent是否为view
     *
     * @param view
     * @param child
     * @return
     */
    private boolean isViewParent(View view, ViewParent child) {
        if (child == this) {
            return false;
        }
        if (view == child) {
            return true;
        }
        if (child.getParent() != null) {
            return isViewParent(view, child.getParent());
        }
        return false;
    }

    private void scroll(int dy) {
        //dy为0或首屏展示内容完毕，则禁止滚动
        if (dy == 0 || !canVerticalScroll()) {
            return;
        }
        mScrollBarUp = dy < 0;
        //scroll bar正在渐出，则再次显示
        if (isScrollBarShow()) {
            showScrollBar();
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
        invalidate();
    }

    private void scrollContent(int dy) {
        if (getCurrentScrollableView() != null) {
            getCurrentScrollableView().scrollBy(0, dy);
        }
    }

    private void fling(float vy) {
        //vy为0或首屏展示内容完毕，则禁止滚动
        if (vy == 0 || !canVerticalScroll()) {
            return;
        }
        mScroller.abortAnimation();
        //上滑速度小于0，下滑速度大于0
        mFlingUp = vy < 0;
        mScrollBarUp = !mFlingUp;
        mFlingContent = false;
        mFlingToTop = false;
        mLastScrollerY = getScrollY();
        if ((mFlingUp && isScrollBottom()) || (!mFlingUp && isScrollTop())) {
            //上滑且滑到底部，或下滑且滑到顶部，不用fling
            return;
        }
        mScroller.fling(0, getScrollY(), 0, -Math.round(vy), 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        dispatchFastBackVisibleEvent();
        invalidate();
    }

    /**
     * fling底部内容
     *
     * @param vy
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void flingContent(int vy, int distance, int duration) {
        View view = getCurrentScrollableView();
        if (view instanceof RecyclerView) {
            ((RecyclerView) view).fling(0, vy);
        } else if (view instanceof WebView) {
            ((WebView) view).flingScroll(0, vy);
        } else if (view instanceof ScrollView) {
            ((ScrollView) view).fling(vy);
        } else if (view instanceof AbsListView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((AbsListView) view).fling(vy);
            } else {
                ((AbsListView) view).smoothScrollBy(distance, duration);
            }
        }
    }

    private boolean canVerticalScroll() {
        return realHeight() > computeVerticalScrollExtent();
    }

    private int realHeight() {
        int realHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View view = getChildAt(i);
            final ViewPager pager = findDirectViewPager(view);
            if (pager != null) {
                int maxHeight = 0;
                for (int j = 0; j < pager.getChildCount(); j++) {
                    maxHeight = Math.max(maxHeight, compute(pager.getChildAt(j), "computeVerticalScrollExtent"));
                }
                realHeight += maxHeight;
            } else {
                realHeight += view.getHeight();
            }
        }
        return realHeight;
    }

    public int leftVisibleHeight() {
        return computeVerticalScrollExtent() - realHeight();
    }

    @Nullable
    private ViewPager findDirectViewPager(View parent) {
        if (parent instanceof ViewPager) {
            return (ViewPager) parent;
        }
        if (parent instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) parent).getChildCount(); i++) {
                View child = ((ViewGroup) parent).getChildAt(i);
                if (child instanceof ViewPager) {
                    return (ViewPager) child;
                }
            }
        }
        return null;
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
        return getCurrentScrollableView() == null
                || !canViewScrollUp(getCurrentScrollableView());
    }

    /**
     * 是否已滑动到顶部
     *
     * @return
     */
    private boolean isScrollTop() {
        return isScrollContainerTop() && isHeaderExpandCompletely();
    }

    /**
     * 是否已滑动到底部
     *
     * @return
     */
    private boolean isScrollBottom() {
        return isHeaderCollapseCompletely()
                && !(getCurrentScrollableView() == null)
                && !canViewScrollDown(getCurrentScrollableView());
    }

    private boolean canViewScrollUp(View view) {
        return ViewCompat.canScrollVertically(view, -1);
    }

    private boolean canViewScrollDown(View view) {
        return ViewCompat.canScrollVertically(view, 1);
    }

    @Nullable
    private View getCurrentScrollableView() {
        if (mScrollableContainer != null) {
            return mScrollableContainer.getScrollableView();
        }
        if (mViewPager != null) {
            return findChildScrollableView(mViewPager.getChildAt(mViewPager.getCurrentItem()));
        }
        return null;
    }

    private View findChildScrollableView(View view) {
        if (view instanceof RecyclerView
                || view instanceof WebView
                || view instanceof ListView
                || view instanceof GridView) {
            return view;
        }
        if (view instanceof ViewGroup) {
            int count = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = findChildScrollableView(((ViewGroup) view).getChildAt(i));
                if (childView != null) {
                    return childView;
                }
            }
        }
        return null;
    }

    public void setCurrentScrollableContainer(@NonNull ScrollableContainer scrollableContainer) {
        mScrollableContainer = scrollableContainer;
    }

    public void setupViewPager(@NonNull ViewPager viewPager) {
        mViewPager = viewPager;
    }

    public void setOnScrollHeaderListener(@NonNull OnScrollHeaderListener listener) {
        mOnScrollHeaderListener = listener;
    }

    public void setOnScrollBarClickListener(@NonNull OnScrollBarClickListener listener) {
        mOnScrollBarClickListener = listener;
    }

    public void setOnFastBackVisibleListener(@NonNull OnFastBackVisibleListener listener) {
        mFastBackVisibleListener = listener;
    }

    @Nullable
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            // Single color bitmap will be created of 1x1 pixel
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static int dp2px(Context context, float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()) + 0.5f);
    }

    public interface ScrollableContainer {
        RecyclerView getScrollableView();
    }

    public interface OnScrollHeaderListener {
        void onScroll(int currentPosition, int maxPosition);
    }

    public interface OnScrollBarClickListener {
        void onClick(int centerYInScreen, int startYInScreen, int endYInScreen);
    }

    public interface OnFastBackVisibleListener {
        void onVisible(boolean visible);
    }

}
