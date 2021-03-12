package com.hlxh.interactivevideotool.ui.mine

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hlxh.interactivevideotool.InteractiveVideoApplication
import com.hlxh.interactivevideotool.R
//import com.hlxh.interactivevideotool.ui.edit.EditActivity
import com.hlxh.interactivevideotool.model.ScriptAbstract
import com.hlxh.interactivevideotool.model.ScriptDetail
import com.hlxh.interactivevideotool.parseUrl
import com.hlxh.interactivevideotool.player.PlayerActivity
import com.hlxh.interactivevideotool.ui.edit.EditActivity
import kotlinx.android.synthetic.main.frag_mine_recycler_item_draft.view.*
import kotlinx.android.synthetic.main.frag_mine_recycler_item_video.view.*

class MineAdapter(val user: String) : RecyclerView.Adapter<MineViewHolder>() {

    private val USER_VIEW: Int = 0
    private val DRAFT_VIEW: Int = 1
    private val VIDEO_VIEW: Int = 2

    private var mScriptList: List<ScriptDetail> = mutableListOf()


    inner class DraftViewHolder(view: View) : MineViewHolder(view) {
        private val draft: ImageView = view.draft
        init {
            itemView.setOnClickListener {

            }
        }
    }

    inner class VideoItemViewHolder(view: View) : MineViewHolder(view) {
        private val cover: ImageView = view.cover
        private val title: TextView  = view.title

        override fun bindModel(script: ScriptDetail) {
            Glide.with(InteractiveVideoApplication.context)
                .load(parseUrl(script.coverImageUrl))
                .into(cover)

            title.text = script.title
        }
        init {
            //长按进入剧情树
            itemView.setOnLongClickListener {
                val position = adapterPosition - 1
                val scriptId = mScriptList[position].id
                //启动剧情树Activity
                EditActivity.start(it.context, scriptId)
                true
            }

            //单击播放
            itemView.setOnClickListener {
                Log.d("click", "_____ MinViewHolder.adapterPosition = $adapterPosition")
                PlayerActivity.start(it.context, mScriptList[adapterPosition-1].id)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MineViewHolder {
        lateinit var viewHolder: MineViewHolder
        val layoutInflater = LayoutInflater.from(parent.context)

        when(viewType) {
//            USER_VIEW -> {
//                val userView: View = layoutInflater.inflate(R.layout.frag_min_recycler_item_header, parent, false)
//                viewHolder = UserViewHolder(userView)
//            }
            DRAFT_VIEW -> {
                //非高优需求
                val draftView:View = layoutInflater.inflate(R.layout.frag_mine_recycler_item_draft, parent, false)
                viewHolder = DraftViewHolder(draftView)
            }
            VIDEO_VIEW -> {
                val videoView: View = layoutInflater.inflate(R.layout.frag_mine_recycler_item_video, parent, false)
                viewHolder = VideoItemViewHolder(videoView)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MineViewHolder, position: Int) {
        when (holder) {
            //is UserViewHolder -> { }
            is DraftViewHolder -> { }
            else -> {
                holder.bindModel(mScriptList[position-1])
            }
        }

    }

    override fun getItemCount(): Int {
//        return if (mVideoList.isEmpty()) 0 else mVideoList.size + 2
        return if (mScriptList.isEmpty()) 0 else mScriptList.size + 1

    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
//            0 -> USER_VIEW
//            1 -> DRAFT_VIEW
//            else -> VIDEO_VIEW
            0 -> DRAFT_VIEW
            else -> VIDEO_VIEW
        }
    }

    fun setData(scriptList: List<ScriptDetail>) {
        mScriptList = scriptList
    }
}