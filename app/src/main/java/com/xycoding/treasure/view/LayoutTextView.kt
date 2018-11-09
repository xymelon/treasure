package com.xycoding.treasure.view

import android.content.Context
import android.graphics.Canvas
import android.text.Layout
import android.util.AttributeSet
import android.view.View

/**
 * Created by xymelon on 2018/10/31.
 */
class LayoutTextView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var layout: Layout? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        layout?.draw(canvas)
    }

}