package com.hlxh.interactivevideotool.ui.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.logic.repo.Repo
import kotlinx.android.synthetic.main.fragment_all_scene.swipe_refresh
import kotlinx.android.synthetic.main.fragment_mine.*

class MineFragment : Fragment() {
    private val mViewModel by lazy { ViewModelProvider(this).get(MineViewModel::class.java) }
    private val mAdapter by lazy { MineAdapter("user1") }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //mViewModel.loadUserVideo("victim")
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Repo.scripts.observe(viewLifecycleOwner) {
            mAdapter.setData(it)
            mAdapter.notifyDataSetChanged()

            swipe_refresh.isRefreshing = false  //数据返回后就隐藏刷新进度条
        }

        mine_recyclerView.layoutManager = GridLayoutManager(activity, 2)
        mine_recyclerView.adapter = mAdapter

        //下拉刷新
        //swipe_refresh.setColorSchemeColors(R.color.design_default_color_primary)
        swipe_refresh.setOnRefreshListener {
            //发起网络请求获取新数据
            Repo.loadAllScripts()
        }



    }

}