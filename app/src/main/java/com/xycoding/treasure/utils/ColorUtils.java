package com.xycoding.treasure.utils;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

/**
 * Created by xuyang on 2016/10/28.
 */
public class ColorUtils {

    public static int gradientColor(@NonNull Context context, float offset, @ColorRes int startColorId, @ColorRes int endColorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return (int) new android.animation.ArgbEvaluator().evaluate(
                    offset,
                    ContextCompat.getColor(context, startColorId),
                    ContextCompat.getColor(context, endColorId));
        } else {
            return (int) new ArgbEvaluator().evaluate(
                    offset,
                    ContextCompat.getColor(context, startColorId),
                    ContextCompat.getColor(context, endColorId));
        }
    }

}
