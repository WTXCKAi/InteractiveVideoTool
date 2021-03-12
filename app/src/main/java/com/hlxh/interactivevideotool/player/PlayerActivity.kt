package com.hlxh.interactivevideotool.player

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.find
import com.hlxh.interactivevideotool.findStart
import com.hlxh.interactivevideotool.logic.repo.Repo
import com.hlxh.interactivevideotool.logic.repo.ScriptDataSource
import com.hlxh.interactivevideotool.model.Episode
import com.hlxh.interactivevideotool.model.QUESTION_SELECT
import com.hlxh.interactivevideotool.model.ScriptDetail
import com.hlxh.interactivevideotool.parseUrl
import com.hlxh.interactivevideotool.util.requestPermission
import kotlinx.android.synthetic.main.activity_player.*


@SuppressLint("CheckResult")
class PlayerActivity : AppCompatActivity() {

    private var mPlaybackPosition: Long = 0
    private var mCurrentWindow: Int = 0
    private var mPlayWhenReady: Boolean = true
    private val mViewModel by lazy { ViewModelProvider(this).get(PlayerViewModel::class.java) }
    private lateinit var mPlayerView: PlayerView
    private var mPlayer: SimpleExoPlayer? = null
    private lateinit var mPlayerControlView: PlayerControlView


    //加载媒体数据的dataSource
    private lateinit var mDataSourceFactory: DataSource.Factory

    private var mEpisodeList: List<Episode> = ArrayList()
    private var mScriptDetail: ScriptDetail? = null
    private var currentEpisode: Episode? = null

    private val mPlayerHistory by lazy {
        supportFragmentManager.findFragmentByTag("history_fragment") as PlayHistory
    }

    private val mOptionsPanel: OptionsFragment by lazy {
        supportFragmentManager.findFragmentByTag("options_fragment") as OptionsFragment
    }

    //是否正在沉浸式播放
    private var isSteep = false

    private val eventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

            if (playbackState == Player.STATE_ENDED) {
                //当前视频播放结束
                val isFinished = currentEpisode?.interact == null
                if (isFinished) {
                    //剧本播放完毕
                    Toast.makeText(
                        this@PlayerActivity,
                        "Congratulation!! got an end.",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                //显示问题和选项
                mPlayerControlView.hide()
                mPlayerHistory.hide()
                val interactType = currentEpisode?.interact?.type

                if (interactType == QUESTION_SELECT) {
                    //选择模式的互动
                    currentEpisode!!.interact?.let { it ->
                        mOptionsPanel.showQuestion(it) {
                            play(it.nextEpisodeId)
                        }
                    }
                }
                else {
                    //问答模式的互动
                }


            } else {
                //视频正在播放，不显示问题选项
                mOptionsPanel.hide()
                if (!isSteep) {
                    mPlayerHistory.show()
                    // playerControlView.show()
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initializePlayer()

        //取出videoId，到Repo中找数据源
        val scriptId = intent.getStringExtra("scriptId")
        Repo.scripts.value!!.find { it.id == scriptId }.let {
            if (it != null) {
                mScriptDetail = it
                startVideo()
            } else {
                Toast.makeText(this, "start a play", Toast.LENGTH_SHORT).show()
                //finish()
            }
        }
        Log.d("playbug", "所选剧本id = $scriptId")
        //网络请求的方式暂时弃用
        //取出videoId，发起网络请求
//        mViewModel.loadScriptDetail(scriptId!!)
//
//        //监听，获取详细视频段数据源
//        mViewModel.scriptDetailLiveData.observe(this) {
//            //mEpisodeList = it.episodeList as ArrayList<Episode>
//
//            if (it != null) {
//                mScriptDetail = it
//                Log.d("play", "收到片段的uri = ${it.episodeList[0].url}")
//                Log.d("interact", "interact = ${it.episodeList[0].interact}")
//                startVideo()
//            } else {
//                Toast.makeText(this, "start a play", Toast.LENGTH_SHORT).show()
//               // finish()
//            }
//        }

        //隐藏状态栏，做沉浸式view（也就是常见的全屏）
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        //跳转播放指定history
        mPlayerHistory.selectCallBack { episode ->
            episode.play(true)
        }

        content.setOnClickListener {
            //全屏播放的时候，点击屏幕就展示播放历史选项条
            isSteep = !isSteep
            if (isSteep) {
                mPlayerHistory.hide()
                mPlayerControlView.hide()
            } else {
                mPlayerHistory.show()
                mPlayerControlView.show()
            }
        }

    }

    private fun startVideo() {
        mScriptDetail?.let {
            val episode = it.findStart()
            if (episode != null) {
                Log.d("playbug", "episode typ = ${episode.type}")
            }
            episode?.play()
        }
    }

    private fun Episode.play(fromHistory: Boolean = false) {
        currentEpisode = this
        //要播放的视频资源
//        val videoSource: MediaSource = ProgressiveMediaSource.Factory(mDataSourceFactory)
//            .createMediaSource(parseUrl(url))
        //mPlayer.prepare(videoSource)

        // Prepare the player with the source.
        val mediaItem: MediaItem = MediaItem.fromUri(parseUrl(url))
        if (mPlayer != null) {
            mPlayer!!.setMediaItem(mediaItem)
            mPlayer!!.prepare()
        }

        mPlayerControlView.show()
        mPlayerHistory.setCurrentEpisode(this, fromHistory)
    }

    private fun play(id: String) {
        val episode = mScriptDetail?.find(id)
        episode?.play()
    }

    private fun initializePlayer() {
        mPlayerView = player_view

        if (mPlayer == null) {
            val trackSelector = DefaultTrackSelector(this)
            trackSelector.setParameters(
                trackSelector.buildUponParameters().setMaxVideoSizeSd()
            )
            mPlayer = SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build()
            mPlayerView.player = mPlayer

            mDataSourceFactory = DefaultDataSourceFactory(
                this,
                Util.getUserAgent(this, "InteractiveVideoTool")
            )
        }

        mPlayer!!.addListener(eventListener)

        //播放控制控件
        mPlayerControlView = findViewById(R.id.exo_controller)
        mPlayerControlView.player = mPlayer
        // 始终显示进度条
        mPlayerControlView.showTimeoutMs = -1

//        //恢复上次播放状态
        mPlayer!!.playWhenReady = mPlayWhenReady
        mPlayer!!.seekTo(mCurrentWindow, mPlaybackPosition)
//        mPlayer.prepare()
    }
    
    private fun releasePlayer() {
        //释放播放器资源
        if (mPlayer != null) {
            //保存现场
            mPlayWhenReady = mPlayer!!.playWhenReady //暂停还是播放状态
            mPlaybackPosition = mPlayer!!.currentPosition //当前播放位置
            mCurrentWindow = mPlayer!!.currentWindowIndex //当前窗口索引
            mPlayer!!.release()
            mPlayer = null
        }
    }


    override fun onStop() {
        super.onStop()
        Log.d("playbug", "on stop")
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.removeListener(eventListener)
        Log.d("playbug", "on destroy")

        releasePlayer()
    }

    companion object {
        fun start(context: Context, scriptId: String) {
            context.startActivity(
                Intent(context, PlayerActivity::class.java).apply {
                    putExtra("scriptId", scriptId)
                }
            )
        }
    }
}
