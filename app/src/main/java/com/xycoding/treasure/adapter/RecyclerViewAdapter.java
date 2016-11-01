package com.xycoding.treasure.adapter;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.NinePatchDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.LayoutRecyclerViewItemBinding;
import com.xycoding.treasure.view.recyclerview.OnItemTouchListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by xuyang on 2016/10/31.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> implements OnItemTouchListener {

    private List<String> mData;
    private OnStartDragListener mDragListener;

    public RecyclerViewAdapter(@NonNull List<String> data) {
        mData = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        holder.binding.tvLabel.setText(mData.get(position));
        holder.binding.ivSort.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN && mDragListener != null) {
                    mDragListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onItemSelected(RecyclerView.ViewHolder holder) {
        holder.itemView.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder holder) {
        holder.itemView.setPadding(0, 0, 0, 0);
        holder.itemView.setBackgroundResource(0);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemRemoved(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void setDragListener(OnStartDragListener dragListener) {
        mDragListener = dragListener;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private LayoutRecyclerViewItemBinding binding;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    /**
     * Listener for manual initiation of a drag.
     */
    public interface OnStartDragListener {

        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);

    }

}
