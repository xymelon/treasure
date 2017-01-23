package com.xycoding.treasure.view.recyclerview;

/**
 * Created by xuyang on 2017/1/23.
 */
public interface ExpandableHead {

    /**
     * Group header position in original data.
     *
     * @return
     */
    int getPosition();

    /**
     * Expand state.
     *
     * @return
     */
    boolean isExpanded();

}
