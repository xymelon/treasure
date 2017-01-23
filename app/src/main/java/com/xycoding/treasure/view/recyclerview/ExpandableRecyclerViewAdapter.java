package com.xycoding.treasure.view.recyclerview;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by xuyang on 2017/1/23.
 */
public abstract class ExpandableRecyclerViewAdapter<CHILD, HEAD extends ExpandableHead> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int ITEM_TYPE_HEADER = -1024;

    private List<CHILD> mChildItems;
    private List<HEAD> mHeadItems;

    public ExpandableRecyclerViewAdapter(@NonNull List<CHILD> childItems, @NonNull List<HEAD> headItems) {
        mChildItems = childItems;
        mHeadItems = headItems;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return onCreateHeadViewHolder(parent);
        }
        return onCreateChildViewHolder(parent, viewType);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @CallSuper
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public final int getItemCount() {
        return mChildItems.size() + mHeadItems.size();
    }

    public abstract RecyclerView.ViewHolder onCreateHeadViewHolder(ViewGroup parent);

    public abstract RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindHeadViewHolder(RecyclerView.ViewHolder holder, int position);

    public abstract void onBindChildViewHolder(RecyclerView.ViewHolder holder, int position);

    private int childPositionToExpandablePosition(int position) {
        int offset = 0, headSize = mHeadItems.size();
        for (int i = 0; i < headSize; i++) {
            if (mHeadItems.get(i).getPosition() > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

//    private int expandablePositionToChildPosition(int sectionedPosition) {
//        if (isSectionHeaderPosition(sectionedPosition)) {
//            return RecyclerView.NO_POSITION;
//        }
//        int offset = 0;
//        for (int i = 0; i < mSections.size(); i++) {
//            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
//                break;
//            }
//            --offset;
//        }
//        return sectionedPosition + offset;
//    }

}
