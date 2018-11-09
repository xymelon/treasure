package com.xycoding.treasure.activity.layout

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.text.PrecomputedTextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder
import com.xycoding.treasure.R
import com.xycoding.treasure.kotlin.dp
import com.xycoding.treasure.kotlin.sp
import com.xycoding.treasure.utils.DeviceUtils
import com.xycoding.treasure.view.LayoutTextView
import com.xycoding.treasure.view.recyclerview.LinearSpaceItemDecoration
import kotlinx.android.synthetic.main.activity_layout_recycler_view.*
import kotlinx.android.synthetic.main.layout_textview.view.*

/**
 * Created by xymelon on 2018/11/1.
 */
class LayoutRecyclerViewActivity : AppCompatActivity() {

    private val longText = "%s %s. " + LayoutTextViewActivity.tagString

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_recycler_view)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            change(checkedId)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(LinearSpaceItemDecoration(16.dp(this)))
        change(radioGroup.checkedRadioButtonId)
    }

    private fun change(@IdRes checkedId: Int) {
        val dataList = ArrayList<CharSequence>()
        when (checkedId) {
            R.id.rbTextView -> {
                for (i in 1..size) {
                    dataList.add(
                        LayoutTextViewActivity.richSpannable(
                            this,
                            longText.format("TextView", i)
                        )
                    )
                }
                recyclerView.adapter = TextViewAdapter(dataList)
            }
            R.id.rbLayoutTextView -> {
                val width = DeviceUtils.screenWidthPixels() - 2 * 16F.dp(this)
                val textSize = 16F.sp(this)
                val layoutDataList = ArrayList<Layout>()
                var layout: Layout
                for (i in 1..size) {
                    layout = TextLayoutBuilder()
                        .setText(
                            LayoutTextViewActivity.richSpannable(
                                this,
                                longText.format("LayoutTextView", i)
                            )
                        )
                        .setWidth(width)
                        .setTextSize(textSize)
                        .build()!!
                    layoutDataList.add(layout)
                }
                recyclerView.adapter = LayoutTextViewAdapter(layoutDataList)
            }
            R.id.rbPrecomputedText -> {
                for (i in 1..size) {
                    dataList.add(
                        LayoutTextViewActivity.richSpannable(
                            this,
                            longText.format("PrecomputedTextView", i)
                        )
                    )
                }
                recyclerView.adapter = PrecomputedLayoutAdapter(dataList)
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    open class TextViewAdapter(val dataList: ArrayList<CharSequence>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ViewHolder(layoutInflater.inflate(R.layout.layout_textview, parent, false))
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.view.textViewItem.text = dataList[position]
        }

    }

    open class LayoutTextViewAdapter(private val dataList: ArrayList<Layout>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutTextView(parent.context))
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (holder.view is LayoutTextView) {
                val layoutParams = ViewGroup.MarginLayoutParams(
                    dataList[position].width,
                    dataList[position].height
                )
                holder.view.layout = dataList[position]
                holder.view.layoutParams = layoutParams
            }
        }

    }

    class PrecomputedLayoutAdapter(dataList: ArrayList<CharSequence>) : TextViewAdapter(dataList) {

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.view.textViewItem.setTextFuture(
                PrecomputedTextCompat.getTextFuture(
                    dataList[position],
                    TextViewCompat.getTextMetricsParams(holder.view.textViewItem),
                    null
                )
            )
        }

    }

    companion object {
        const val size = 100
    }

}