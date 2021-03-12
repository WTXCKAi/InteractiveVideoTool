package com.hlxh.interactivevideotool.myWidget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.model.ScriptAbstract
import com.hlxh.interactivevideotool.model.ScriptDetail
import com.hlxh.interactivevideotool.parseUrl
import com.hlxh.interactivevideotool.player.PlayerActivity
import kotlinx.android.synthetic.main.looper_pager_layout.view.*

class LooperPager(context: Context?, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private lateinit var mViewPager: AutoChangeViewPager
    private lateinit var mLooperPagerTitle: TextView
    private lateinit var mLooperPagerContent: TextView
    private var mPagerItems: List<ScriptDetail> = ArrayList()

    init {
        //加载控件布局
        LayoutInflater.from(context).inflate(R.layout.looper_pager_layout, this, true)

        initView()

        //切换时title和content会改变，所以需要一个页面滑动的监听
        initEvent()
    }

    private fun initEvent() {
        //页面滑动事件监听
        mViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //页面滚动
            }

            override fun onPageSelected(position: Int) {
                //页面被选中（滚动后停在哪个页上，说明哪个页被选中）时回调
                //停下来后，刷新标题和内容

                val realPosition = position % mPagerItems.size
                mLooperPagerTitle.text = this@LooperPager.mPagerItems[realPosition].title
                mLooperPagerContent.text = this@LooperPager.mPagerItems[realPosition].summary

            }

            override fun onPageScrollStateChanged(state: Int) {
                //页面状态改变：停止滚动状态、拖拽状态、选中状态
            }
        })

    }

    fun setData(pagerItems: List<ScriptDetail>) {
        mPagerItems = pagerItems
        mViewPager.adapter = InnerAdapter()

        //设置title和content的初始值
        mLooperPagerTitle.text = mPagerItems[0].title
        mLooperPagerContent.text = mPagerItems[0].summary


        val startPosition = mPagerItems.size * 200
        mViewPager.setCurrentItem(startPosition, false)

    }
    private fun initView() {
        //初始化LooperPager，获取里边的控件
        mViewPager = viewPager
        mLooperPagerTitle = looperPagerTitle
        mLooperPagerContent = looperPagerContent

    }

    inner class InnerAdapter: PagerAdapter() {

        override fun getCount(): Int = Int.MAX_VALUE

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            //引入item布局（其实只有一个图片控件）
            //为什么要用false
            val itemView = LayoutInflater.from(container.context).inflate(R.layout.looper_pager_item, container, false)

            //获取控件
            val cover: ImageView = itemView.findViewById(R.id.cover)

            //获取当前项的pagerItem实例
            val realPosition = position % mPagerItems.size

            val pagerItem = mPagerItems[realPosition]
            Log.d("loadfrom", "after parseUrl = ${parseUrl(pagerItem.coverImageUrl)}")

            Glide.with(this@LooperPager)
                .load(parseUrl(pagerItem.coverImageUrl))
                .into(cover)
            cover.scaleType = ImageView.ScaleType.FIT_XY

            itemView.setOnClickListener(View.OnClickListener {
                val videoId = pagerItem.id
                Log.d("click", "------- click video $videoId -----")
                PlayerActivity.start(context, videoId)
            })

            //把数据添加到容器中
            container.addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            //当子项View被滑出范围内时会被移除
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            //用来判断页面的view和上面instantiateItem回传的object是否一样
            return view == `object`
        }

    }
}
