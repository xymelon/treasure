package com.xycoding.treasure.activity.layout

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.text.PrecomputedTextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Layout
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder
import com.xycoding.treasure.R
import com.xycoding.treasure.kotlin.dp
import com.xycoding.treasure.view.LayoutTextView
import kotlinx.android.synthetic.main.activity_layout_textview.*

/**
 * Created by xymelon on 2018/10/31.
 */
class LayoutTextViewActivity : AppCompatActivity() {

    private val longText = "Android KitKat offered a new feature called “Scenes & Transitions,” with the intention of making animations easier and more automatic for developers. Instead of thinking about how to create animations to help guide the user between different phases of an application flow, a developer could simply use transitions and have animations run automatically whenever the UI changed. These transitions could also be customized to offer new or different animation effects for these UI changes. The problem was that these APIs, being a part of the Android platform itself, were only available on KitKat and later releases. Developers wanting animated experiences for all of their users weren’t terribly interested in transitions because they’d still have to do things the old way for the older releases they needed to support. The intention with transitions was always to provide a back-ported solution for the Support Library… but it was a non-trivial task and thus didn’t happen. Until now Thanks to the efforts of Yuichi Araki in the Google Developer Relations team, we’ve finally finished that back-port and the capabilities of KitKat transitions are available all the way back to the Ice Cream Sandwich release. That means that you are now able to change your UI’s layout and use the transitions system to animate those changes automatically on devices back to API level 14."
    private lateinit var layout: Layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_textview)
        val width = 300F.dp(this)
        val textSize = 12F.dp(this)
        layout = TextLayoutBuilder()
                .setText(longText)
                .setWidth(width)
                .setTextSize(textSize)
                .setTextColor(Color.BLACK)
                .build()!!

        var startTime = System.nanoTime()
        layoutContent.addView(TextView(this).apply {
            layoutParams = ViewGroup.MarginLayoutParams(this@LayoutTextViewActivity.layout.width, this@LayoutTextViewActivity.layout.height)
            text = this@LayoutTextViewActivity.longText
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.RED)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        })
        tvTextview.text = "TextView: " + (System.nanoTime() - startTime)

        startTime = System.nanoTime()
        layoutContent.addView(LayoutTextView(this).apply {
            val layoutParams = ViewGroup.MarginLayoutParams(this@LayoutTextViewActivity.layout.width, this@LayoutTextViewActivity.layout.height)
            layoutParams.topMargin = 20
            setLayoutParams(layoutParams)
            layout = this@LayoutTextViewActivity.layout
            setBackgroundColor(Color.BLUE)
        })
        tvLayout.text = "LayoutView: " + (System.nanoTime() - startTime)

        val precomputedText = PrecomputedTextCompat.create(longText, tvPrecomputedContent.textMetricsParamsCompat)
        startTime = System.nanoTime()
        tvPrecomputedContent.text = precomputedText
        if (tvPrecomputedContent.layoutParams is ViewGroup.MarginLayoutParams) {
            (tvPrecomputedContent.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = 20
        }
        tvPrecomputed.text = "PreComputedTextView: " + (System.nanoTime() - startTime)

        btnRecyclerView.setOnClickListener {
            startActivity(Intent(it.context, LayoutRecyclerViewActivity::class.java))
        }
    }

}