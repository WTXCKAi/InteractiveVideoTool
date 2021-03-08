package com.hlxh.interactivevideotool.ui.edit

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.fromAsset
import com.hlxh.interactivevideotool.model.Episode
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.layout_edit_configure.*
import java.io.File


/**
 * @author wenchengye on 01/07/2020.
 */
class EditConfigureFragment : FixedHeightBottomSheetDialogFragment() {

    companion object {
        const val KEY_EPISODE_ID = "episode_id"
        const val KEY_SCRIPT_ID = "script_id"

        const val REQUEST_CODE_SELECT_VIDEO = 1

        val optionIndicators = listOf(
           "A", "B", "C", "D"
        )

        var frame: Bitmap? = null
    }


    private val episodeId: String by lazy {
        arguments?.get(KEY_EPISODE_ID) as? String ?: ""
    }

    private val scriptId: String by lazy {
        arguments?.get(KEY_SCRIPT_ID) as? String ?: ""
    }

    private lateinit var incomeEpisode: Episode
    private lateinit var videoUrl: String

    private val optionList: MutableList<EditText> = mutableListOf()

    private var extractDisposable: Disposable? = null

    private lateinit var player: SimpleExoPlayer
    private lateinit var dataSourceFactory: DataSource.Factory

    private var onSave: ((episode: Episode) -> Unit)? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_edit_configure, container, false)
    }


    @SuppressLint("CheckResult")
    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        fetchEpisode(episodeId, scriptId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                incomeEpisode = it
                bindView()
                doObserve()
            }, {
                it.printStackTrace()
            })
    }

    private fun bindView() {

        updateVideoUrl(incomeEpisode.url)

        val title = incomeEpisode.title.trim()
        episode_title.setText(title)
        episode_title.setSelection(title.length)

        options_title.setText(incomeEpisode.interact?.questionText)

        incomeEpisode.interact?.optionTextList?.forEachIndexed { index, option ->

            val itemView =
                layoutInflater.inflate(R.layout.layout_option_item, options, false)

            val edit = itemView.findViewById<EditText>(R.id.content)
            val indicator =  itemView.findViewById<TextView>(R.id.indicator)

            indicator.text = optionIndicators[index]
            edit.setText(option)

            options.addView(itemView)
            optionList.add(edit)
        }

        updateOptionsTitleVisible()
        updateEditFrameBtnVisible()
    }

    private fun doObserve() {
        frame_preview.setOnClickListener {
            pickVideo()
        }

        edit_frame_btn.setOnClickListener {
            if (videoUrl.isNotEmpty() && optionList.isNotEmpty()) {
                editFrame()
            }
        }

        save_button.setOnClickListener {
            saveEpisode()
        }
    }

    private fun fetchEpisode(episodeId: String, scriptId: String): Observable<Episode> = Observable.fromCallable {
//        ScriptDataSource.scripts.value?.find { it.id == scriptId  }?.find(episodeId)
//            ?: Episode(id = UUID.randomUUID().toString())
        Episode()
    }

    private fun saveEpisode() {
        updateEpisode()

        onSave?.invoke(incomeEpisode)
    }

    private fun updateEpisode() {
        incomeEpisode.url = videoUrl
        incomeEpisode.title = episode_title.text.toString()
        incomeEpisode.interact?.questionText = options_title.text.toString()

        optionList.forEachIndexed { index, view ->
            incomeEpisode.interact?.optionTextList?.set(index, view.text.toString())
        }
    }


    private fun pickVideo() {
        startActivityForResult(
            Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI),
            REQUEST_CODE_SELECT_VIDEO
        )
    }

    private fun editFrame() {
        updateEpisode()
        EditOptionsActivity.start(requireContext(), episodeId, scriptId)
    }

    private fun updateVideoUrl(url: String) {
        videoUrl  = url
        if (videoUrl.isNotEmpty()) {
            val bm = extractFrame(url)
        } else {
            frame_preview.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    private fun updateEditFrameBtnVisible() {
        edit_frame_btn.visibility = if (videoUrl.isNotEmpty() && optionList.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun updateOptionsTitleVisible() {
        options_title.visibility = if (optionList.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECT_VIDEO) {

            data?.data?.toVideoPath(requireActivity())?.takeIf { it.isNotEmpty() }?.let { url ->
                updateVideoUrl(url)
                updateEditFrameBtnVisible()
            }
        }
    }

    private fun extractFrame(url: String) {
        extractDisposable?.dispose()
        extractDisposable = Single.create<Unit> { emitter ->
            initPlayer()

            player.addListener(object : Player.EventListener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        player.seekTo(player.duration)
                    } else if (playbackState == Player.STATE_ENDED) {
                        player.seekTo(player.duration)
                    }
                }

                override fun onSeekProcessed() {
                    emitter.onSuccess(Unit)
                }
            })

            player.prepare(ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(if (url.startsWith("/")) Uri.fromFile(File(url)) else fromAsset(url)))
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Thread.sleep(500L)
                frame = (player_view.videoSurfaceView as TextureView).bitmap
                frame_preview.setImageBitmap(frame)
            }, {
                it.printStackTrace()
            })

    }

    private fun initPlayer() {
        if (this::player.isInitialized) return

        player = SimpleExoPlayer.Builder(requireContext()).build()
        player_view.player = player

        dataSourceFactory =  DefaultDataSourceFactory(
            requireContext(),
            Util.getUserAgent(requireContext(), "yourApplicationName")
        )

        player.playWhenReady = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::player.isInitialized) {
            player.release()
        }

    }

    fun setOnSave(action: (episode: Episode) -> Unit) {
        onSave = action
    }
}

private fun Uri.toVideoPath(activity: Activity): String {
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    val cursor: Cursor =  activity.managedQuery(this, projection, null, null, null)
    val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
    cursor.moveToFirst()
    return cursor.getString(columnIndex)
}