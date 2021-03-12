package com.hlxh.interactivevideotool.ui.homepage

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.logic.repo.Repo
import kotlinx.android.synthetic.main.fragment_hacker_scene.*


class HackerSceneFrag : Fragment() {

    private val mViewModel by lazy { ViewModelProvider(this).get(HomepageViewModel::class.java) }

    private val mAdapter by lazy { ContentAdapter(mViewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("hackerSceneFrag"," onCreateView")
        Repo.loadHackerScript()
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.HackerStyle)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.fragment_hacker_scene, container, false)
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("hackerSceneFrag"," onViewCreated")

        Repo.hackerScripts.observe(viewLifecycleOwner) {
            mAdapter.setData(it)
            mAdapter.notifyDataSetChanged()
            swipe_refresh.isRefreshing = false
        }

        content_recyclerView.layoutManager = LinearLayoutManager(activity)
        content_recyclerView.adapter = mAdapter

        swipe_refresh.setColorSchemeColors(R.color.design_default_color_primary)
        swipe_refresh.setOnRefreshListener {
            Repo.loadHackerScript()
        }
    }
}