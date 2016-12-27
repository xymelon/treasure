package com.xycoding.treasure.view.recyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Adds bottom dividers to a RecyclerView with a GridLayoutManager.
 */
public class GridBottomDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mNumColumns;

    /**
     * Constructor. Takes in {@link Drawable} objects to be used as
     * horizontal and vertical dividers.
     *
     * @param divider    A divider {@code Drawable} to be drawn.
     * @param numColumns The number of columns in the grid of the RecyclerView
     */
    public GridBottomDividerItemDecoration(Drawable divider, int numColumns) {
        mDivider = divider;
        mNumColumns = numColumns;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        drawVerticalDividers(canvas, parent);
    }

    /**
     * Determines the size and location of offsets between items in the parent
     * RecyclerView.
     *
     * @param outRect The {@link Rect} of offsets to be added around the child view
     * @param view    The child view to be decorated with an offset
     * @param parent  The RecyclerView onto which dividers are being added
     * @param state   The current RecyclerView.State of the RecyclerView
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        boolean childIsInFirstRow = (parent.getChildAdapterPosition(view)) < mNumColumns;
        if (!childIsInFirstRow) {
            outRect.top = mDivider.getIntrinsicHeight();
        }
    }

    /**
     * Adds vertical dividers to a RecyclerView with a GridLayoutManager or its
     * subclass.
     *
     * @param canvas The {@link Canvas} onto which dividers will be drawn
     * @param parent The RecyclerView onto which dividers are being added
     */
    private void drawVerticalDividers(Canvas canvas, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }
}


