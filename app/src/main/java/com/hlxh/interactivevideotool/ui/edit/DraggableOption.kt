package com.hlxh.interactivevideotool.ui.edit

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

import kotlin.math.abs


/**
 * @author sunjingkai
 * @since 2020/7/1
 * @lastModified by sunjingkai on 2020/7/1
 */
@SuppressLint("ViewConstructor")
class DraggableTextView(
    context: Context
) : AppCompatTextView(context) {

    interface Listener {
        fun onDragEnd(x: Float, y: Float)
        fun onSingleTap()
    }

    private var mWidth = 0
    private var mHeight = 0
    private var mMaxWidth = 0
    private var mMaxHeight = 0
    private var downX = 0f
    private var downY = 0f
    private var lastXOnDownEvent = 0f
    private var lastYOnDownEvent = 0f

    private var isDrag = false

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun checkOptionOverlapped(other: DraggableTextView): Boolean {
        val targetL = other.x
        val targetT = other.y
        val targetR = targetL + other.width
        val targetB = targetT + other.height

        val r = x + width
        val b = y + height
        if (x > targetL && x < targetR) {
            if (y > targetT && y < targetB) {
                return true
            } else if (b > targetT && b < targetB) {
                return true
            }
        } else if (r > targetL && r < targetR) {
            if (y > targetT && y < targetB) {
                return true
            } else if (b > targetT && b < targetB) {
                return true
            }
        }
        return false
    }

    fun revertLastDrag() {
        x = lastXOnDownEvent
        y = lastYOnDownEvent
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
        mMaxWidth = getScreenWidth(context)
        mMaxHeight = getScreenHeight(context)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrag = false
                downX = event.x
                downY = event.y
                lastXOnDownEvent = x
                lastYOnDownEvent = y
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x - downX
                val moveY = event.y - downY
                var l: Int
                var r: Int
                var t: Int
                var b: Int
                if (abs(moveX) > 3 || abs(moveY) > 3) {
                    l = (x + moveX).toInt()
                    r = l + width
                    t = (y + moveY).toInt()
                    b = t + height
                    if (l < 0) {
                        l = 0
                    } else if (r > mMaxWidth) {
                        r = mMaxWidth
                        l = r - width
                    }
                    if (t < 0) {
                        t = 0
                        b = t + height
                    } else if (b > mMaxHeight) {
                        b = mMaxHeight
                        t = b - height
                    }
                    x = l.toFloat()
                    y = t.toFloat()
                    isDrag = true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isDrag) {
                    listener?.onSingleTap()
                } else {
                    listener?.onDragEnd(x, y)
                }
                isDrag = false
            }
            MotionEvent.ACTION_CANCEL -> {
            }
        }
        return true
    }
}