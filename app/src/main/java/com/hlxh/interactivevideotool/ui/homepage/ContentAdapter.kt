package com.hlxh.interactivevideotool.ui.homepage

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
import com.hlxh.interactivevideotool.model.*
import com.hlxh.interactivevideotool.myWidget.LooperPager
import com.hlxh.interactivevideotool.player.PlayerActivity
import kotlinx.android.synthetic.main.frag_homepage_recycler_item_videoinfo.view.*
import java.util.ArrayList

class ContentAdapter(private val viewModel: HomepageViewModel) : RecyclerView.Adapter<ContentViewHolder>() {

    private val HEADERVIEW_TYPE: Int = 1
    private val FOOTERVIEW_TYPE: Int = 2
    private val TAG = "crash"
    private var mVideoAbstractList = ArrayList<ScriptAbstract>()   //总的视频摘要列表
    private var mLooperData: List<ScriptAbstract> = ArrayList<ScriptAbstract>()          //用在Looper中的数据，是总数据的子集



    inner class LooperPagerViewHolder(view: View) : ContentViewHolder(view) {
        //1。轮播图
        private val looperPager: LooperPager = view.findViewById(R.id.looperPager)

        override fun bindModel(looperData: List<ScriptAbstract>) {
            Log.d(TAG, "looperData.size = ${looperData.size}")
            //if (looperData.isNotEmpty()) mLooperPager.setData(looperData)
            looperPager.setData(looperData)
        }
    }

    inner class VideoItemViewHolder(view: View) : ContentViewHolder(view) {
        //2。视频列表
        private var mCover: ImageView = view.findViewById(R.id.cover)
        private var mTitle: TextView = view.title
        private var mSummary: TextView = view.summary
        private var mDate: TextView = view.date

        //设置视频列表这个viewHolder的item的点击事件
        init {
            itemView.setOnClickListener {
                Log.d("click", "_____ contentViewHolder.adapterPosition = $adapterPosition")

                val position = adapterPosition - 1
                val scriptId = mVideoAbstractList[position].id
                //启动新Activity，用于播放视频，传递video
                Log.d("click", "____ YOU CLICK VIDEO $scriptId")
                PlayerActivity.start(it.context, scriptId)
            }
        }

        override fun bindModel(scriptAbstract: ScriptAbstract) {
            Glide.with(InteractiveVideoApplication.context)
                .load(scriptAbstract.coverImageUrl)
                .into(mCover)

            mTitle.text = scriptAbstract.title
            mSummary.text = scriptAbstract.summary
            mDate.text = scriptAbstract.date
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {

        lateinit var contentViewHolder: ContentViewHolder
        val layoutInflater = LayoutInflater.from(parent.context)

        //根据不同的viewType来返回不同的布局
        when(viewType) {
            HEADERVIEW_TYPE -> {
                val looperPagerView: View = layoutInflater.inflate(R.layout.frag_homepage_recycler_item_looperpager, parent, false)
                contentViewHolder = LooperPagerViewHolder(looperPagerView)
            }
            FOOTERVIEW_TYPE -> {
                val videoInfoView: View = layoutInflater.inflate(R.layout.frag_homepage_recycler_item_videoinfo, parent, false)
                contentViewHolder = VideoItemViewHolder(videoInfoView)
            }
        }
        return contentViewHolder
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        //根据Position去调用不同的holder类设置数据源
        if (position == 0) {
            Log.d(TAG, "onBindViewHolder.bindModel")
            Log.d(TAG, "looper, mLooperData.size = ${mLooperData.size}")
            holder.bindModel(mLooperData)
        }
        else {
            Log.d(TAG, "videoList, mVideoAbstractLIst.size = ${mVideoAbstractList.size}")
            holder.bindModel(mVideoAbstractList[position-1])
        }
    }

    override fun getItemCount(): Int {
        //该方法描述的是这个recyclerview有多少个item
        //只有当ItemCount不为0时，recyclerView才会调用onCreateViewHolder和onBindViewHolder
        //本项目中，recyclerview有两种item，第一种是looperPager，数量为1个；第二种是视频列表，数量为 mVideoAbstractList.size
        //所以总item数量 = 1 + mVideoAbstractList.size
        //looperPager这个item的position恒为0，视频列表
        //当该方法当返回值不为0时，就会调用onBindViewHolder
        return if (mVideoAbstractList.isEmpty()) 0 else mVideoAbstractList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HEADERVIEW_TYPE
        else FOOTERVIEW_TYPE
    }

    fun setData(scriptAbstractList: List<ScriptAbstract>) {
        //服务端返回数据时才会调用
        Log.d(TAG, "setData :::: videoAbstractList.size = ${scriptAbstractList.size}")

        mVideoAbstractList = scriptAbstractList as ArrayList<ScriptAbstract>
        Log.d(TAG, "this is setData")
        //筛选出播放量最高的3个
        mLooperData = mVideoAbstractList.sortedByDescending { it.clickRate }.take(3) as ArrayList<ScriptAbstract>
        Log.d(TAG, "mLooperData.size = ${mLooperData.size}")

    }
}