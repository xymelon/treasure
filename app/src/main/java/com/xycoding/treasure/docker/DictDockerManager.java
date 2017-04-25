package com.xycoding.treasure.docker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuyang on 2017/4/25.
 */
public class DictDockerManager {

    public static final int VIEW_TYPE_UNKNOWN = 0;

    /**
     * 英汉/汉英词典
     */
    public static final int VIEW_TYPE_EC = 100;

    /**
     * 英英词典
     */
    public static final int VIEW_TYPE_EE = 101;

    private static final SparseArray<IDictDocker> sDictDockers = new SparseArray<>();
    private static final Map<String, Integer> sDictViewTypes = new HashMap<>();

    static {
        registerDocker(new ECDictDocker());
        registerDocker(new EEDictDocker());
    }

    /**
     * Register item docker, each docker should have unique view type.
     *
     * @param docker
     */
    private static void registerDocker(IDictDocker docker) {
        sDictDockers.put(docker.viewType(), docker);
        switch (docker.viewType()) {
            case VIEW_TYPE_EC:
                sDictViewTypes.put("ec", VIEW_TYPE_EC);
                sDictViewTypes.put("ce", VIEW_TYPE_EC);
                break;
        }
    }

    /**
     * Create ViewHolder by view type, return null if the corresponding docker is not registered.
     *
     * @param parent
     * @param viewType
     * @return
     */
    public static RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType) {
        IDictDocker docker = sDictDockers.get(viewType);
        if (docker != null) {
            return docker.onCreateViewHolder(parent);
        }
        //create dummy view holder prevents crash.
        return new RecyclerView.ViewHolder(new View(parent.getContext())) {
        };
    }

    /**
     * Bind data to ViewHolder.
     *
     * @param holder
     * @param data
     * @param position
     */
    public static void bindViewHolder(@NonNull RecyclerView.ViewHolder holder, @Nullable JSONObject data, int position) {
        IDictDocker docker = sDictDockers.get(holder.getItemViewType());
        if (docker != null) {
            docker.onBindViewHolder(holder, data, position);
        }
    }

    /**
     * Get view type by dict id.
     *
     * @param dictId
     * @return
     */
    public static int getItemViewType(String dictId) {
        if (sDictViewTypes.containsKey(dictId)) {
            return sDictViewTypes.get(dictId);
        }
        return VIEW_TYPE_UNKNOWN;
    }

}
