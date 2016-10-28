package com.xycoding.treasure.view.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by xuyang on 2016/4/22.
 */
public class LinearSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int spaceInPixels;
    private boolean showTopSpace;

    public LinearSpaceItemDecoration(int spaceInPixels) {
        this(spaceInPixels, true);
    }

    public LinearSpaceItemDecoration(int spaceInPixels, boolean showTopSpace) {
        this.spaceInPixels = spaceInPixels;
        this.showTopSpace = showTopSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        if (position > 0) {
            outRect.top = spaceInPixels;
        } else if (showTopSpace) {
            outRect.top = spaceInPixels;
        }
    }
}
