package com.xycoding.treasure.activity.layout

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.text.PrecomputedTextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xycoding.treasure.R
import com.xycoding.treasure.kotlin.dp
import com.xycoding.treasure.view.recyclerview.LinearSpaceItemDecoration
import kotlinx.android.synthetic.main.activity_layout_recycler_view.*
import kotlinx.android.synthetic.main.layout_textview.view.*
import java.util.concurrent.Executors

/**
 * Created by xymelon on 2018/11/1.
 */
class LayoutRecyclerViewActivity : AppCompatActivity() {

    private val longText = "%s %s. Android KitKat offered a new feature called “Scenes & Transitions,” with the intention of making animations easier and more automatic for developers. Instead of thinking about how to create animations to help guide the user between different phases of an application flow, a developer could simply use transitions and have animations run automatically whenever the UI changed. These transitions could also be customized to offer new or different animation effects for these UI changes. The problem was that these APIs, being a part of the Android platform itself, were only available on KitKat and later releases. Developers wanting animated experiences for all of their users weren’t terribly interested in transitions because they’d still have to do things the old way for the older releases they needed to support. The intention with transitions was always to provide a back-ported solution for the Support Library… but it was a non-trivial task and thus didn’t happen. Until now Thanks to the efforts of Yuichi Araki in the Google Developer Relations team, we’ve finally finished that back-port and the capabilities of KitKat transitions are available all the way back to the Ice Cream Sandwich release. That means that you are now able to change your UI’s layout and use the transitions system to animate those changes automatically on devices back to API level 14."
    private lateinit var dataList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_recycler_view)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            change(checkedId)
        }
        dataList = arrayListOf()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(LinearSpaceItemDecoration(16.dp(this)))
        change(radioGroup.checkedRadioButtonId)
    }

    fun change(@IdRes checkedId: Int) {
        when (checkedId) {
            R.id.rbTextView -> {
                dataList.clear()
                for (i in 1..10) {
                    dataList.add(longText.format("TextView", i))
                }
                recyclerView.adapter = LayoutAdapter(dataList)
            }
            R.id.rbPrecomputedText -> {
                dataList.clear()
                for (i in 1..10) {
                    dataList.add(longText.format("PrecomputedTextView", i))
                }
                recyclerView.adapter = PrecomputedLayoutAdapter(dataList)
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    open class LayoutAdapter(val dataList: ArrayList<String>) : RecyclerView.Adapter<ViewHolder>() {

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

    class PrecomputedLayoutAdapter(dataList: ArrayList<String>) : LayoutAdapter(dataList) {

        private val executor = Executors.newSingleThreadExecutor()

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.view.textViewItem.setTextFuture(PrecomputedTextCompat.getTextFuture(
                    dataList[position],
                    TextViewCompat.getTextMetricsParams(holder.view.textViewItem),
                    executor))
        }

    }

}