package com.hlxh.interactivevideotool.ui.homepage

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hlxh.interactivevideotool.model.ScriptAbstract
import com.hlxh.interactivevideotool.model.ScriptDetail


abstract class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bindModel(scriptAbstract: ScriptDetail) {

    }

    open fun bindModel(looperData: List<ScriptDetail>) {

    }
}