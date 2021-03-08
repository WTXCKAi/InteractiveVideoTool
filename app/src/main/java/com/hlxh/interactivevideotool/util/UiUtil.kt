package com.hlxh.interactivevideotool.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorRes

fun getScreenWidth(context: Context?): Int {
    if (context == null) {
        return 0
    }
    val dm = context.resources.displayMetrics
    return dm?.widthPixels ?: 0
}

fun getScreenHeight(context: Context?): Int {
    if (context == null) {
        return 0
    }
    val dm = context.resources.displayMetrics
    return dm?.heightPixels ?: 0
}

fun getStatusBarHeight(context: Context): Int {
    val resources = context.resources
    val resourceId =
        resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

fun getNavigationBarHeight(context: Context): Int {
    val resources = context.resources
    val rid =
        resources.getIdentifier("config_showNavigationBar", "bool", "android")
    return if (rid != 0) {
        val resourceId =
            resources.getIdentifier("navigation_bar_height", "dimen", "android")
        resources.getDimensionPixelSize(resourceId)
    } else 0
}

fun getColor(context:Context,@ColorRes color: Int): Int {
    return context.resources.getColor(color)
}

fun Context.dip2Px(dipValue: Int): Int {
    val scale = resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}

fun Context.sp2px(sp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        resources.displayMetrics
    )
}