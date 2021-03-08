package com.hlxh.interactivevideotool.ui.homepage

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.hlxh.interactivevideotool.MainActivity
import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.fromFile
import com.hlxh.interactivevideotool.myWidget.NoSwipeViewPager
import com.hlxh.interactivevideotool.searchview.SearchActivity
import com.hlxh.interactivevideotool.searchview.SearchView
import com.nex3z.flowlayout.FlowLayout
import kotlinx.android.synthetic.main.frag_homepage_recycler_item_videoinfo.view.*
import kotlinx.android.synthetic.main.fragment_homepage.*

class HomepageFragment : Fragment() {

    private lateinit var mSearchView: View
    private lateinit var mFilter: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_homepage, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //以下两种写法等价
        //val sectionsPagerAdapter = activity?.let { SectionsPagerAdapter(it, childFragmentManager) }
        val sectionsPagerAdapter = SectionsPagerAdapter(requireActivity(), childFragmentManager)
        val noSwipeViewPager: NoSwipeViewPager = content_viewPager
        noSwipeViewPager.currentItem
        noSwipeViewPager.adapter = sectionsPagerAdapter
        //noSwipeViewPager.offscreenPageLimit = 1
        val tabs: TabLayout = homepage_tabs
        tabs.setupWithViewPager(noSwipeViewPager)
        val tabPosition = tabs.selectedTabPosition
        Log.d("tab", "tabs selected position = $tabPosition")
        val mainActivity = activity as MainActivity

        Log.d("tab", "getActivity = $mainActivity")
        if (tabPosition == 2) {

        }
//        mSearchView = search_block
        mSearchView = edit_text
        mSearchView.setOnClickListener{
            SearchActivity.start(requireContext())
        }

        //flowlayout 标签
        val labelSet: FlowLayout = scene_label_set
        labelSet.removeAllViews()
        val labelSubSet: FlowLayout = scene_label_subSet
        labelSubSet.removeAllViews()

        //循环创建item，添加到FlowLayout中
        var labelList: Array<String> = resources.getStringArray(R.array.label_array)
        for (item in labelList) {
            val checkBox: CheckBox = inflate(requireContext(), R.layout.flowlayout_item, null) as CheckBox
            checkBox.text = item
            labelSet.addView(checkBox)

            //为subset生成数据
            val labelChecked: TextView = inflate(requireContext(), R.layout.flowlayout_item_checked, null) as TextView
            labelChecked.text = "❎ $item"

            labelChecked.setOnClickListener {
                labelSubSet.removeView(it)
                checkBox.isChecked = false
            }
            //设置标签checkBoxItem的点击事件
            checkBox.setOnCheckedChangeListener { checkItemView, isChecked ->

                when (isChecked) {
                    true -> {
                        scene_label_subSet.visibility = VISIBLE

                        Log.d("flow", "check ${checkItemView.text}")
                        Log.d("flow", "此时click的labelChecked： ${labelChecked.text}")
                        labelSubSet.addView(labelChecked)
                    }
                    else -> {
                        Log.d("flow", "cancel ${checkItemView.text}")
                        //遍历scene_label_subSet,删掉相应的view
                        Log.d("flow", "此时cancel的labelChecked： ${labelChecked.text}")
                        scene_label_subSet.removeView(labelChecked)
                        if (scene_label_subSet.childCount == 0) scene_label_subSet.visibility = GONE
                    }
                }
            }
        }


        //筛选
        var isClicked: Boolean = false
        mFilter = scene_label_filter
        mFilter.setOnClickListener {
            if (!isClicked) {
                mFilter.text = "筛选  ▲️"
                mFilter.setTextColor(resources.getColor(R.color.orange))
                scene_label_set.visibility = VISIBLE
                isClicked = true
            }
            else {
                mFilter.text = "筛选  ▼"
                mFilter.setTextColor(resources.getColor(R.color.default_text_color))
                scene_label_set.visibility = GONE
                isClicked = false
            }
        }



    }

}