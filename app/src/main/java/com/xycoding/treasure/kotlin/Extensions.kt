package com.xycoding.treasure.kotlin

import android.content.Context
import android.util.TypedValue

/**
 * Created by xymelon on 2018/11/1.
 */
public inline fun Float.dp(context: Context): Int {
    return Math.round(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics))
}

public inline fun Int.dp(context: Context): Int {
    return Math.round(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics))
}