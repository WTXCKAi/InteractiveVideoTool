package com.hlxh.interactivevideotool.ui.edit

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hlxh.interactivevideotool.util.getScreenHeight
import com.hlxh.interactivevideotool.util.getStatusBarHeight

/**
 * @author sunjingkai
 * @since 2020/7/2
 * @lastModified by sunjingkai on 2020/7/2
 */
open class FixedHeightBottomSheetDialogFragment: BottomSheetDialogFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.also {
            addGlobaLayoutListener(view)
        }
    }

    private fun addGlobaLayoutListener(view: View?) {
        view?.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                setPeekHeight(v, getScreenHeight(requireContext()) * 1 + getStatusBarHeight(requireContext()))
                v.removeOnLayoutChangeListener(this)
            }
        })
    }

    fun setPeekHeight(view: View, peekHeight: Int) {
        val behavior = getBottomSheetBehaviour(view) ?: return
        behavior.peekHeight = peekHeight
    }

    private fun getBottomSheetBehaviour(view: View): BottomSheetBehavior<*>? {
        val layoutParams =
            (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int
                ) {
                    //禁止拖拽，
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        //设置为收缩状态
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    }
                }

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float
                ) {
                }
            })
            return behavior
        }
        return null
    }
}