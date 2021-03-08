package com.hlxh.interactivevideotool.player

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.model.Episode
import com.hlxh.interactivevideotool.parseUrl
import com.hlxh.interactivevideotool.util.dip2Px
import kotlinx.android.synthetic.main.player_history_layout.*


class PlayHistory : Fragment() {

    private var mHistoryList: MutableList<Episode> = mutableListOf()
    private lateinit var mCurrentEpisode: Episode

    private val mRecyclerView: RecyclerView by lazy { play_history }

    private val mCurrentContext by lazy { requireContext() }
    private var selectEpisodeCallback: ((Episode) -> Unit)? = null
    private var nowShowDetail: Episode? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_history_layout, container, false)
    }

    fun setCurrentEpisode(episode: Episode, fromHistory: Boolean = false) {

        if (::mCurrentEpisode.isInitialized && !fromHistory) {
            val currentIndex = mHistoryList.indexOf(mCurrentEpisode)
            //不是最后一个
            if (currentIndex < mHistoryList.size - 1) {
                //后面的不是准备下一个的，删除历史
                if (mHistoryList[currentIndex + 1] != episode) {
                    mHistoryList = mHistoryList.subList(0, currentIndex + 1)
                }
            }
        }

        if (mHistoryList.find { it == episode } == null) {
            mHistoryList.add(episode)
        }

        mCurrentEpisode = episode
        mRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        mRecyclerView.adapter = object : SimpleRecycleAdapter<Episode>(mCurrentContext, mHistoryList) {
            override fun getLayoutResId(viewType: Int): Int {
                return R.layout.player_history_item
            }

            override fun convert(
                viewHolder: SimpleViewHolder,
                episode: Episode,
                position: Int
            ) {
                if (episode == mCurrentEpisode) {
                    viewHolder.setImageResource(R.id.image, R.drawable.history_current)
                } else {
                    viewHolder.setImageResource(R.id.image, R.drawable.history_normal)
                }

            }

            override fun onItemClick(v: SimpleViewHolder, episode: Episode, position: Int) {
                if (nowShowDetail == episode) {
                    detail_info.visibility = View.GONE
                    nowShowDetail = null
                } else {
                    nowShowDetail = episode
                    detail_info.visibility = View.VISIBLE

                    val url = episode.url

                    val uri = if (url.startsWith("http://") || url.startsWith("/")) {
                        parseUrl(url)
                    } else {
                        Uri.parse(
                            "file:///android_asset/${url.substring(
                                0,
                                url.lastIndexOf("/")
                            )}/cover.png"
                        )
                    }
                    Glide.with(detail_background)
                        .load(uri)
                        .into(detail_background)
                    if (TextUtils.isEmpty(episode.title)) {
                        detail_title.visibility = View.GONE
                    } else {
                        detail_title.text = episode.title
                        detail_title.visibility = View.VISIBLE
                    }
                    detail_info.setOnClickListener {
                        selectEpisodeCallback?.invoke(episode)
                    }
                    //???
                    detail_info.post {
                        detail_info.y =
                            mRecyclerView.y + v.itemView.y - detail_info.height / 2 + mCurrentContext.dip2Px(20)
                    }
                }
            }

        }

    }


    fun show() {
        detail_info.visibility = View.GONE
        nowShowDetail = null
        view?.visibility = View.VISIBLE
    }

    fun hide() {
        view?.visibility = View.GONE
    }

    fun selectCallBack(callback: (Episode) -> Unit) {
        selectEpisodeCallback = callback
    }

}
