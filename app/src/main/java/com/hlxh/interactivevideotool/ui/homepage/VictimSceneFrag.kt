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
import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.logic.repo.Repo
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
//        mViewModel.loadScriptAbstract("victim")//在这进行首次网络请求
        Repo.loadVictimScript()
        return inflater.inflate(R.layout.fragment_victim_scene, container, false)
    }
    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AllSceneFrag"," onViewCreated")

        //订阅looperLiveData
        Repo.victimScripts.observe(viewLifecycleOwner) {
            //T!意味着 “T" 或 "T?”
            //Log.d("crash", "Victim ******** 收到list长度= ${(it as ArrayList).size}")
            mAdapter.setData(it)
            mAdapter.notifyDataSetChanged()
            swipe_refresh.isRefreshing = false  //数据返回后就隐藏刷新进度条
        }

        content_recyclerView.layoutManager = LinearLayoutManager(activity)
        content_recyclerView.adapter = mAdapter

        //下拉刷新
        swipe_refresh.setColorSchemeColors(R.color.design_default_color_primary)
        swipe_refresh.setOnRefreshListener {
            //发起网络请求获取新数据
            Repo.loadVictimScript()
        }



    }

}