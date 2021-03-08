package com.hlxh.interactivevideotool.myWidget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * A custom ViewPager which can not swipe
 */
class NoSwipeViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        //return super.onTouchEvent(ev)
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        //return super.onInterceptTouchEvent(ev)
        return false
    }
}