package com.xycoding.treasure.activity.layout

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.text.PrecomputedTextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.text.Layout
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder
import com.xycoding.richtext.RichText
import com.xycoding.richtext.typeface.ClickSpan
import com.xycoding.richtext.typeface.FontTypefaceSpan
import com.xycoding.richtext.typeface.IStyleSpan
import com.xycoding.richtext.typeface.LinkClickSpan
import com.xycoding.treasure.R
import com.xycoding.treasure.kotlin.dp
import com.xycoding.treasure.kotlin.sp
import com.xycoding.treasure.utils.DeviceUtils
import com.xycoding.treasure.view.LayoutTextView
import kotlinx.android.synthetic.main.activity_layout_textview.*

/**
 * Created by xymelon on 2018/10/31.
 */
class LayoutTextViewActivity : AppCompatActivity() {

    private lateinit var layout: Layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_textview)
        val width = DeviceUtils.screenWidthPixels() - (32.dp(this))
        val textSize = 12F.sp(this)

        val longText = richSpannable(this, tagString)

        //子线程计算并创建StaticLayout
        layout = TextLayoutBuilder()
            .setText(longText)
            .setWidth(width)
            .setTextSize(textSize)
            .setTextColor(Color.BLACK)
            .build()!!

        addTextViewLabel("TextView vs 自定义Layout")

        //新增TextView
        var startTime = System.nanoTime()
        layoutContent.addView(AppCompatTextView(this).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                this@LayoutTextViewActivity.layout.width,
                this@LayoutTextViewActivity.layout.height
            )
            text = longText
            setTextColor(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        })
        var time = System.nanoTime() - startTime
        addTextViewLabel("普通TextView", time)

        //新增自定义TextView
        startTime = System.nanoTime()
        layoutContent.addView(LayoutTextView(this).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                this@LayoutTextViewActivity.layout.width,
                this@LayoutTextViewActivity.layout.height
            )
            layout = this@LayoutTextViewActivity.layout
        })
        time = System.nanoTime() - startTime
        addTextViewLabel("自定义LayoutTextView", time)


        addTextViewLabel("TextView setText vs PrecomputedText")

        val textView = AppCompatTextView(this).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setTextColor(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }
        startTime = System.nanoTime()
        layoutContent.addView(textView.apply {
            text = longText
        })
        time = System.nanoTime() - startTime
        addTextViewLabel("普通TextView setText", time)


        val precomputedTextView = AppCompatTextView(this).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setTextColor(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }
        val precomputedText =
            PrecomputedTextCompat.create(longText, precomputedTextView.textMetricsParamsCompat)
        startTime = System.nanoTime()
        layoutContent.addView(precomputedTextView.apply {
            text = precomputedText
        })
        time = System.nanoTime() - startTime
        addTextViewLabel("普通TextView precomputedText", time)

        btnRecyclerView.setOnClickListener {
            startActivity(Intent(it.context, LayoutRecyclerViewActivity::class.java))
        }
    }

    private fun addTextViewLabel(label: String, time: Long? = null) {
        layoutContent.addView(TextView(this).apply {
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.topMargin = 20
            layoutParams.bottomMargin = 50
            setLayoutParams(layoutParams)

            setTextColor(Color.BLACK)
            if (time == null) {
                text = label
                textSize = 18F
            } else {
                text = "$label: $time"
                setBackgroundColor(Color.GREEN)
            }
        })
    }

    companion object {

        const val tagString =
            "The <a href='https://en.wikipedia.org/wiki/Rich_Text_Format'>Rich Text Format</a> " +
                    "is a <c>proprietary</c> <f>document</f> file format with published <bi>specification</bi> " +
                    "developed by <t>Microsoft Corporation</t> from 1987 until 2008 for <s>cross-platform</s> document interchange " +
                    "with Microsoft products."

        fun richSpannable(context: Context, tagString: String): Spanned {
            val foregroundTextColor = ContextCompat.getColor(context, R.color.T1)
            val linkTextColor = ContextCompat.getColor(context, R.color.colorPrimary)
            val normalTextColor = ContextCompat.getColor(context, R.color.R1)
            val pressedTextColor = ContextCompat.getColor(context, R.color.W1)
            val pressedBackgroundColor = ContextCompat.getColor(context, R.color.B1)
            val georgiaTypeface =
                Typeface.createFromAsset(context.assets, "fonts/DroidSansJapanese.ttf")

            val richText = RichText.Builder()
                .addBlockTypeSpan(
                    ClickSpan(
                        normalTextColor,
                        pressedTextColor,
                        pressedBackgroundColor,
                        ClickSpan.OnClickListener { textView, text, rawX, rawY ->
                            Toast.makeText(
                                context,
                                text,
                                Toast.LENGTH_SHORT
                            ).show()
                        }), "c"
                )
                .addBlockTypeSpan(
                    IStyleSpan<CharacterStyle> { ForegroundColorSpan(foregroundTextColor) },
                    "f", "t"
                )
                .addBlockTypeSpan(
                    IStyleSpan<CharacterStyle> { StyleSpan(Typeface.BOLD_ITALIC) },
                    "bi"
                )
                .addBlockTypeSpan(IStyleSpan<CharacterStyle> {
                    TextAppearanceSpan(
                        context,
                        R.style.SpinnerItemAppearance
                    )
                }, "s")
                .addBlockTypeSpan(FontTypefaceSpan(georgiaTypeface), "t")
                .addLinkTypeSpan(
                    LinkClickSpan(
                        linkTextColor,
                        pressedTextColor,
                        pressedBackgroundColor,
                        LinkClickSpan.OnLinkClickListener { textView, url ->
                            Toast.makeText(
                                context,
                                url,
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                )
                .build()
            return richText.parse(tagString)
        }
    }

}