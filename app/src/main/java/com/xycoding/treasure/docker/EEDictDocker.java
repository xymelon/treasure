package com.xycoding.treasure.docker;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.LayoutDictVerticalBinding;

import org.json.JSONObject;

/**
 * Created by xuyang on 2017/4/25.
 */
public class EEDictDocker implements IDictDocker<EEDictDocker.EEViewHolder> {

    @Override
    public EEViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return new EEViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dict_vertical, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final EEViewHolder holder, @Nullable JSONObject data, final int position) {
        holder.binding.tvTitle.setText("21世纪英汉词典" + (position + 1));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.itemView.getContext(), "item " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int viewType() {
        return DictDockerManager.VIEW_TYPE_EE;
    }

    static class EEViewHolder extends RecyclerView.ViewHolder {

        private LayoutDictVerticalBinding binding;

        EEViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

}
