package com.xycoding.treasure.docker;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xycoding.treasure.R;
import com.xycoding.treasure.adapter.RecyclerViewDictHorizontalAdapter;
import com.xycoding.treasure.databinding.LayoutDictHorizontalBinding;

import org.json.JSONObject;

/**
 * Created by xuyang on 2017/4/25.
 */
public class ECDictDocker implements IDictDocker<ECDictDocker.ECViewHolder> {

    @Override
    public ECViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return new ECViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dict_horizontal, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ECViewHolder holder, @Nullable JSONObject data, int position) {
        RecyclerView recyclerView = holder.binding.recyclerViewHorizontal;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new RecyclerViewDictHorizontalAdapter());
    }

    @Override
    public int viewType() {
        return DictDockerManager.VIEW_TYPE_EC;
    }

    static class ECViewHolder extends RecyclerView.ViewHolder {

        private LayoutDictHorizontalBinding binding;

        ECViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

}
