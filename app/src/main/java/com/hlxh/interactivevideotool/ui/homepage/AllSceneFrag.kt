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
import com.hlxh.interactivevideotool.model.*

import kotlinx.android.synthetic.main.fragment_all_scene.*

class AllSceneFrag : Fragment(){
    val CRASH = "crash"
    private val mViewModel by lazy { ViewModelProvider(this).get(HomepageViewModel::class.java) }

    private val mAdapter by lazy { ContentAdapter(mViewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(CRASH, "AllFrag on CreateVIew")

        //mViewModel.loadScriptAbstractAll()
        //mViewModel.loadTest("victim")
        Repo.loadAllScripts()
        return inflater.inflate(R.layout.fragment_all_scene, container, false)
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AllSceneFrag"," onViewCreated")

        //订阅looperLiveData
        Repo.scripts.observe(viewLifecycleOwner) {
            //T!意味着 “T" 或 "T?”
//            Log.d(CRASH, "********* it = $it")
//            Log.d(CRASH, "*********    mViewModel.allVideoList = ${mViewModel.allVideoList.size}")
            Log.d("loadfromassets", "live data changed")
            for (s in it) {
                Log.d("loadfromassets", "cover = ${s.coverImageUrl}")

            }
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
            Repo.loadAllScripts()
        }
//        //,,,,,,生成json数据
////        val episode = Episode("id1", "v1", "http://vjs.zencdn.net/v/oceans.mp4", "test", "start")
////
////        val scriptDetail = ScriptDetail("v1", episodeList = arrayListOf(episode))
////
////        //主要就是这句
////        val jsonre = Gson().toJson(scriptDetail)
////
////        Log.d("tojson", jsonre)
//
//        //生成一个完整script的数据，测试用
//
//主要就是这句

        val optionLink1 = Option("episode1", OPTION_TYPE_LINK)
        var optionTo2 = Option("episode2", OPTION_TYPE_EPISODE)
        var optionTo3 = Option("episode3", OPTION_TYPE_EPISODE)
        var optionLink3 = Option("episode3", OPTION_TYPE_LINK, x = 0.25f, y = 0.8f)
        var optionTo4 = Option("episode4", OPTION_TYPE_EPISODE, x = 0.6f, y = 0.8f)
        var episode1 = Episode(
            "episode1", "s1", "victim/test/test1.mp4", "the first episode", TYPE_START,
            interact = Question(
                QUESTION_SELECT, "往哪里走？",
                optionList = arrayListOf(optionTo2, optionTo3),
                optionTextList =  arrayListOf("go to 2", "go to 3"))
            )
        var episode2 = Episode(
            "episode2", "s1", "victim/test/test2.mp4", "the second episode", TYPE_NORMAL,
            interact = Question(
                QUESTION_SELECT, "你的选择是？",
                optionList =  arrayListOf(optionLink3, optionTo4),
                optionTextList =  arrayListOf("go to 3", "去 4 吧")
            )
        )
        var episode3 = Episode(
            "episode3", "s1", "victim/test/test3.mp4", "I'm No.3", TYPE_NORMAL,
            interact = Question(
                QUESTION_SELECT, "where to go?",
                optionList =  arrayListOf(optionLink3, optionLink1),
                optionTextList =  arrayListOf("去3", "去4")
            )
        )
        var episode4 = Episode(
            "episode4", "s1", "victim/test/test4.mp4", "我是4", TYPE_FINISHED,
            interact = null
        )


        val videoList = ArrayList<ScriptDetail>()
        videoList.add(
            ScriptDetail("id1", TYPE_HACKER,
                "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg",
                "第一个剧本", "title 1", "内容 1", "2021-2-1", 5,
                episodeList = arrayListOf(episode1, episode2))
        )
        videoList.add(
            ScriptDetail("id2", TYPE_HACKER,
                "https://t7.baidu.com/it/u=963301259,1982396977&fm=193&f=GIF",
                "第二个剧本", "title 2", "内容 2", "2021-2-1", 1,
                episodeList = arrayListOf(episode1, episode2))
        )
        videoList.add(
            ScriptDetail("s3", TYPE_VICTIM,
                "https://t7.baidu.com/it/u=1575628574,1150213623&fm=193&f=GIF",
                "第三个", "title 3", "内容 3", "2021-2-1", 0,
                episodeList = arrayListOf(episode1, episode2)))
        videoList.add(
            ScriptDetail("s4", TYPE_VICTIM,
                "https://www.cnitom.com/uploadfile/2020/0109/20200109025125725.jpg",
                "第四个", "title 4", "内容 4", "2021-2-1", 7,
                episodeList = arrayListOf(episode1, episode2)))
        videoList.add(
            ScriptDetail("s5", TYPE_VICTIM,
                "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg",
                "第五个", "title 5", "内容 5", "2021-2-1", 15,
                episodeList = arrayListOf(episode1)))

//        Repo.writeToFile(videoList[0])
//        Repo.writeToFile(videoList[1])
//        Repo.writeToFile(videoList[2])
//        Repo.writeToFile(videoList[3])
//        val jsonre = Gson().toJson(videoList[0])
//
//        Log.d("tojson", jsonre)
//


    }


}