package com.hlxh.interactivevideotool.ui.homepage

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.model.Episode
import com.hlxh.interactivevideotool.model.Question
import com.hlxh.interactivevideotool.model.ScriptDetail
import kotlinx.android.synthetic.main.fragment_all_scene.*

/**
 * 暂时这么写着，三个fragment的类是可以复用的，后期再优化
 */
class VictimSceneFrag : Fragment(){

    private val mViewModel by lazy { ViewModelProvider(this).get(HomepageViewModel::class.java) }

    private val mAdapter by lazy { ContentAdapter(mViewModel) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel.loadScriptAbstract("victim")//在这进行首次网络请求
        return inflater.inflate(R.layout.fragment_victim_scene, container, false)
    }
    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AllSceneFrag"," onViewCreated")

        //订阅looperLiveData
        mViewModel.scriptAbstractLiveData.observe(viewLifecycleOwner) {
            //T!意味着 “T" 或 "T?”
            Log.d("crash", "VIctim ******** 收到list长度= $it.videoAbstractList")
            mAdapter.setData(it.scriptAbstractList)
            mAdapter.notifyDataSetChanged()
            swipe_refresh.isRefreshing = false  //数据返回后就隐藏刷新进度条
        }

        content_recyclerView.layoutManager = LinearLayoutManager(activity)
        content_recyclerView.adapter = mAdapter

        //下拉刷新
        swipe_refresh.setColorSchemeColors(R.color.design_default_color_primary)
        swipe_refresh.setOnRefreshListener {
            //发起网络请求获取新数据
            mViewModel.loadScriptAbstract("victim")
        }

        //,,,,,,生成json数据
//        val episode = Episode("id1", "v1", "http://vjs.zencdn.net/v/oceans.mp4", "test", "start")
//
//        val scriptDetail = ScriptDetail("v1", episodeList = arrayListOf(episode))
//
//        //主要就是这句
//        val jsonre = Gson().toJson(scriptDetail)
//
//        Log.d("tojson", jsonre)

        //生成一个完整script的数据，测试用

    }

}