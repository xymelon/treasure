package com.xycoding.treasure.docker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.json.JSONObject;

/**
 * Created by xuyang on 2017/4/25.
 */
public interface IDictDocker<VH extends RecyclerView.ViewHolder> {

    VH onCreateViewHolder(@NonNull ViewGroup parent);

    void onBindViewHolder(@NonNull VH holder, @Nullable JSONObject data, int position);

    int viewType();

}
