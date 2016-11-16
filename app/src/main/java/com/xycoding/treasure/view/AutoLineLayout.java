package com.xycoding.treasure.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import com.xycoding.treasure.R;

/**
 * Created by xuyang on 2016/11/16.
 */
public class AutoLineLayout extends ViewGroup {

    private int mHorizontalSpacing;
    private int mVerticalSpacing;
    private SparseIntArray mChildRowWidth = new SparseIntArray();
    private SparseIntArray mChildRowHeight = new SparseIntArray();

    public AutoLineLayout(Context context) {
        this(context, null);
    }

    public AutoLineLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.AutoLineLayout);
            mHorizontalSpacing = styledAttributes.getDimensionPixelOffset(R.styleable.AutoLineLayout_horizontalSpacing, 0);
            mVerticalSpacing = styledAttributes.getDimensionPixelOffset(R.styleable.AutoLineLayout_verticalSpacing, 0);
            styledAttributes.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int maxInternalWidth = MeasureSpec.getSize(widthMeasureSpec) - getHorizontalPadding();
        final int maxInternalHeight = MeasureSpec.getSize(heightMeasureSpec) - getVerticalPadding();

        mChildRowHeight.clear();
        mChildRowWidth.clear();
        int currentRowWidth = 0, currentRowHeight = 0, rowIndex = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            int childWidthSpec = createChildMeasureSpec(child.getLayoutParams().width, maxInternalWidth, widthMode);
            int childHeightSpec = createChildMeasureSpec(child.getLayoutParams().height, maxInternalHeight, heightMode);
            child.measure(childWidthSpec, childHeightSpec);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (wouldExceedParentWidth(widthMode, maxInternalWidth, currentRowWidth, childWidth)) {
                // new line
                currentRowWidth = 0;
                currentRowHeight = 0;
                rowIndex++;
            }
            currentRowWidth = currentRowWidth == 0 ? childWidth : currentRowWidth + mHorizontalSpacing + childWidth;
            currentRowHeight = Math.max(currentRowHeight, childHeight);
            mChildRowWidth.put(rowIndex, currentRowWidth);
            mChildRowHeight.put(rowIndex, currentRowHeight);
        }
        int longestRowWidth = 0, totalRowHeight = 0;
        for (int i = 0; i < mChildRowHeight.size(); i++) {
            totalRowHeight += mChildRowHeight.get(i);
            if (i < mChildRowHeight.size() - 1) {
                totalRowHeight += mVerticalSpacing;
            }
            longestRowWidth = Math.max(longestRowWidth, mChildRowWidth.get(i));
        }
        setMeasuredDimension(
                widthMode == MeasureSpec.EXACTLY ? MeasureSpec.getSize(widthMeasureSpec) : longestRowWidth + getHorizontalPadding(),
                heightMode == MeasureSpec.EXACTLY ? MeasureSpec.getSize(heightMeasureSpec) : totalRowHeight + getVerticalPadding());
    }

    @Override
    protected void onLayout(boolean changed, int leftPosition, int topPosition, int rightPosition, int bottomPosition) {
        final int parentWidth = getMeasuredWidth() - getPaddingRight();
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();

        int rowIndex = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();
            if (childLeft + childWidth > parentWidth) {
                //new line
                childLeft = getPaddingLeft();
                childTop += mChildRowHeight.get(rowIndex) + mVerticalSpacing;
                rowIndex++;
            }
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            childLeft += childWidth + mHorizontalSpacing;
        }
    }

    private boolean wouldExceedParentWidth(int widthMode, int maxInternalWidth, int currentRowWidth, int childWidth) {
        currentRowWidth = currentRowWidth == 0 ? childWidth : currentRowWidth + mHorizontalSpacing + childWidth;
        return widthMode != MeasureSpec.UNSPECIFIED && currentRowWidth > maxInternalWidth;
    }

    private int createChildMeasureSpec(int childLayoutParam, int max, int parentMode) {
        int spec;
        if (childLayoutParam == LayoutParams.MATCH_PARENT) {
            spec = MeasureSpec.makeMeasureSpec(max, MeasureSpec.EXACTLY);
        } else if (childLayoutParam == LayoutParams.WRAP_CONTENT) {
            spec = MeasureSpec.makeMeasureSpec(
                    max, parentMode == MeasureSpec.UNSPECIFIED ? MeasureSpec.UNSPECIFIED : MeasureSpec.AT_MOST);
        } else {
            spec = MeasureSpec.makeMeasureSpec(childLayoutParam, MeasureSpec.EXACTLY);
        }
        return spec;
    }

    protected int getVerticalPadding() {
        return getPaddingTop() + getPaddingBottom();
    }

    protected int getHorizontalPadding() {
        return getPaddingLeft() + getPaddingRight();
    }

}
