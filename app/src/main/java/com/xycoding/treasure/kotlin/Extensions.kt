package com.xycoding.treasure.kotlin

import android.content.Context
import android.util.TypedValue

/**
 * Created by xymelon on 2018/11/1.
 */
fun Float.dp(context: Context): Int {
    return Math.round(
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics
        )
    )
}

fun Int.dp(context: Context): Int {
    return Math.round(
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
        )
    )
}

fun Float.sp(context: Context): Int {
    return Math.round(
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics
        )
    )
}
