package com.hlxh.interactivevideotool.myWidget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * A custom ViewPager which can change items automatically
 */
class AutoChangeViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    private var DELAY_TIME: Long = 2500

    private val mOnTouchListener = OnTouchListener { view, event ->
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> stopLooper()
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                startLooper()
                //view.performClick()
            }
        }
        true
    }

    private val mTask = object: Runnable {
        override fun run() {
            val curItem = currentItem
            currentItem = 1 + curItem
            postDelayed(this, DELAY_TIME)
        }
    }
    init {
        setOnTouchListener { _, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("click", " Action_down")
                    stopLooper()
                }
                MotionEvent.ACTION_UP -> {
                    Log.d("click", " Action_up")

                    startLooper()
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.d("click", " Action_move")
                    stopLooper()
                }
            }
            //viewPager的onTouch方法要返回false，如果返回true，表示当前方法已经消化掉触摸事件了
            //则该触摸事件就到此为止，不再被进一步处理，后果就是这个viewPager无法左右滑动了
            false
        }

        setOnClickListener { Log.d("click", "autoLoop click") }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("viewPage", "onAttachedToWindow")
        startLooper()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d("viewPage", "onDetachedFromWindow")

        stopLooper()
    }

    private fun startLooper() {
        postDelayed(mTask, DELAY_TIME)
    }

    private fun stopLooper() {
        removeCallbacks(mTask)
    }
    public fun setDelayTime(delayTime: Long) {
        DELAY_TIME = delayTime
    }

}