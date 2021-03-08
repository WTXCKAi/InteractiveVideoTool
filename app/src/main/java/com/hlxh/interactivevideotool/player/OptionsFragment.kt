package com.hlxh.interactivevideotool.player

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.model.Option
import com.hlxh.interactivevideotool.model.Question
import com.hlxh.interactivevideotool.util.dip2Px
import com.hlxh.interactivevideotool.util.sp2px

class OptionsFragment : Fragment() {

    private lateinit var mView: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FrameLayout(requireContext()).also {
            mView = it
            mView.setOnClickListener { }
        }
    }

    fun showQuestion(question: Question, callBack: (Option) -> Unit) {

        mView.removeAllViews()
        //有一个x或者y不为0，就自定义布局
        val selfPosition = question.optionList.find { it.x >= 0.0f || it.y >= 0.0f } != null

        val container: ViewGroup = if (selfPosition) mView else LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM
                bottomMargin = context.dip2Px(60)
                leftMargin = context.dip2Px(30)
                rightMargin = context.dip2Px(30)
            }

            gravity = Gravity.CENTER_HORIZONTAL
            mView.addView(this)
        }

        if (!selfPosition && !TextUtils.isEmpty(question.questionText)) {
            container.addView(TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(context.dip2Px(10), 0, context.dip2Px(10), 0)
                setTextColor(Color.WHITE)
                background = context.getDrawable(R.drawable.play_history_round_background)
                textSize = context.sp2px(10.0f)
                text = question.questionText
            })
        }

        question.optionTextList.forEachIndexed { index, optionText ->
            val option = question.optionList[index] //取option
            //创建option按钮
            container.addView(Button(context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    if (selfPosition) ViewGroup.LayoutParams.WRAP_CONTENT else ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = context.dip2Px(5)
                }

                background = context.getDrawable(R.drawable.play_history_round_background)

                setOnClickListener {
                    mView.removeAllViews()
                    callBack(option)
                }
                text = optionText
                setTextColor(Color.WHITE)
                textSize = 20.0f
                if (selfPosition) {
                    translationX = option.x * mView.width
                    translationY = option.y * mView.height
                }
            })
        }
        view?.visibility = View.VISIBLE

    }

    fun hide() {
        view?.visibility = View.GONE
    }
}

