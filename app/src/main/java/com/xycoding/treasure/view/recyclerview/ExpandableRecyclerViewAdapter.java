package com.xycoding.treasure.view.recyclerview;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by xuyang on 2017/1/23.
 */
public abstract class ExpandableRecyclerViewAdapter<HEAD, CHILD, HOLDER extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<HOLDER> {

    private final static int ITEM_TYPE_HEADER = -1024;

    private List<? extends ExpandableGroup<HEAD, CHILD>> mGroups;
    private OnGroupHeaderClickListener mGroupHeaderClickListener;

    public ExpandableRecyclerViewAdapter(@NonNull List<? extends ExpandableGroup<HEAD, CHILD>> groups) {
        mGroups = groups;
    }

    @Override
    public final HOLDER onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return onCreateHeadViewHolder(parent);
        }
        return onCreateChildViewHolder(parent, viewType);
    }

    @Override
    public final void onBindViewHolder(HOLDER holder, int position) {
        int currentPosition = -1;
        for (ExpandableGroup<HEAD, CHILD> group : mGroups) {
            currentPosition++;
            if (currentPosition == position) {
                onBindHeadViewHolder(holder, group.getHead(), group.isExpanded());
                onGroupHeaderClick(holder, group);
                return;
            }
            if (group.isExpanded()) {
                for (CHILD child : group.getChildren()) {
                    currentPosition++;
                    if (currentPosition == position) {
                        onBindChildViewHolder(holder, child);
                        return;
                    }
                }
            }
        }
    }

    @CallSuper
    @Override
    public int getItemViewType(int position) {
        if (isGroupHeader(position)) {
            return ITEM_TYPE_HEADER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public final int getItemCount() {
        int count = 0;
        for (ExpandableGroup group : mGroups) {
            count++;
            if (group.isExpanded()) {
                count += group.getChildren().size();
            }
        }
        return count;
    }

    /**
     * @param flatPosition the flat position (raw index within the list of visible items in the RecyclerView)
     * @return
     */
    public boolean isGroupHeader(int flatPosition) {
        int currentPosition = -1;
        for (ExpandableGroup group : mGroups) {
            currentPosition++;
            if (currentPosition == flatPosition) {
                return true;
            }
            if (group.isExpanded()) {
                currentPosition += group.getChildren().size();
            }
            if (currentPosition >= flatPosition) {
                return false;
            }
        }
        return false;
    }

    private void onGroupHeaderClick(final HOLDER holder, final ExpandableGroup<HEAD, CHILD> group) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.expanded = !group.isExpanded();
                onBindHeadViewHolder(holder, group.head, group.expanded);
                if (group.expanded) {
                    notifyItemRangeInserted(holder.getAdapterPosition() + 1, group.children.size());
                } else {
                    notifyItemRangeRemoved(holder.getAdapterPosition() + 1, group.children.size());
                }
                if (mGroupHeaderClickListener != null) {
                    mGroupHeaderClickListener.onGroupHeaderClick(group.expanded);
                }
            }
        });
    }

    public void setGroupHeaderClickListener(OnGroupHeaderClickListener listener) {
        this.mGroupHeaderClickListener = listener;
    }

    public abstract HOLDER onCreateHeadViewHolder(ViewGroup parent);

    public abstract HOLDER onCreateChildViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindHeadViewHolder(HOLDER holder, HEAD head, boolean expanded);

    public abstract void onBindChildViewHolder(HOLDER holder, CHILD child);

    public interface OnGroupHeaderClickListener {
        void onGroupHeaderClick(boolean expanded);
    }

    /**
     * Created by xuyang on 2017/1/23.
     */
    public static class ExpandableGroup<HEAD, CHILD> {

        private HEAD head;
        private boolean expanded;
        private List<CHILD> children;

        public ExpandableGroup(@NonNull HEAD head, @NonNull List<CHILD> children) {
            this.head = head;
            this.expanded = false;
            this.children = children;
        }

        public ExpandableGroup(@NonNull HEAD head, @NonNull List<CHILD> children, boolean expanded) {
            this.head = head;
            this.children = children;
            this.expanded = expanded;
        }

        public HEAD getHead() {
            return head;
        }

        public List<CHILD> getChildren() {
            return children;
        }

        public boolean isExpanded() {
            return expanded;
        }

    }

}
