package com.hlxh.interactivevideotool.ui.mine

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hlxh.interactivevideotool.model.ScriptAbstract

abstract class MineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    open fun bindModel(scriptAbstract: ScriptAbstract) { }

}
