package com.xycoding.treasure.view

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout

/**
 * Created by xymelon on 2019/4/26.
 */
class MaskView(context: Context) : FrameLayout(context) {

    private lateinit var anchorRectF: RectF
    private lateinit var anchorPath: Path
    private lateinit var guideBitmap: Bitmap
    private var maskColor: Int? = null
    private var guidePadding: Float = 0f
    private var alignBottomAnchor: Boolean = true
    private var wrapperClickListener: OnClickListener? = null

    init {
        setWillNotDraw(false)
        super.setOnClickListener {
            hide()
            wrapperClickListener?.onClick(it)
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        wrapperClickListener = listener
    }

    override fun onDraw(canvas: Canvas) {
        //draw anchor Path
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutPath(anchorPath)
        } else {
            canvas.clipPath(anchorPath, Region.Op.DIFFERENCE)
        }
        //draw mask layer color
        if (maskColor != null) {
            canvas.drawColor(maskColor!!)
        }
        //draw bitmap
        val left: Float
        val top: Float
        if (alignBottomAnchor) {
            left = anchorRectF.right - guideBitmap.width
            top = anchorRectF.bottom + guidePadding
        } else {
            left = anchorRectF.right - guideBitmap.width
            top = anchorRectF.top - guideBitmap.height - guidePadding
        }
        canvas.drawBitmap(guideBitmap, left, top, null)
    }

    private fun hide() {
        if (parent is ViewGroup) {
            val alphaAnimation = AlphaAnimation(1f, 0f).apply {
                duration = 300
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        (parent as ViewGroup).removeView(this@MaskView)
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }
                })
            }
            startAnimation(alphaAnimation)
        }
    }

    companion object {
        fun show(
            anchor: View,
            @DrawableRes guideResId: Int,
            hideListener: () -> Unit = {},
            @ColorInt maskColor: Int = Color.parseColor("#66000000"),
            alignBottomAnchor: Boolean = true,
            anchorRoundRadius: Int = 0,
            guidePadding: Int = 8
        ) {
            anchor.rootView.post {
                val guideBitmap = BitmapFactory.decodeResource(anchor.context.resources, guideResId)
                //高亮区域
                val rectF = getRectOnScreen(anchor)
                val path = Path()
                path.addRoundRect(
                    rectF,
                    anchorRoundRadius.px,
                    anchorRoundRadius.px,
                    Path.Direction.CW
                )
                //蒙层
                val maskView = MaskView(anchor.context).apply {
                    this.anchorRectF = rectF
                    this.anchorPath = path
                    this.guideBitmap = guideBitmap
                    this.guidePadding = guidePadding.px
                    this.alignBottomAnchor = alignBottomAnchor
                    this.maskColor = maskColor
                    setOnClickListener {
                        hideListener()
                    }
                }
                if (anchor.rootView is ViewGroup) {
                    (anchor.rootView as ViewGroup).addView(
                        maskView, LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )
                }
            }
        }

        private val Int.px: Float
            get() = this * Resources.getSystem().displayMetrics.density

        private fun getRectOnScreen(view: View): RectF {
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            val rectF = RectF()
            rectF.left = location[0].toFloat()
            rectF.top = location[1].toFloat()
            rectF.right = rectF.left + view.width
            rectF.bottom = rectF.top + view.height
            return rectF
        }
    }

}