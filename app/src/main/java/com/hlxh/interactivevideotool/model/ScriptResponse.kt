package com.hlxh.interactivevideotool.model
import com.google.gson.annotations.SerializedName

/**
 * 互动视频成品
 * 已发布的互动视频作品的数据结构
 */

//video_type 视频场景视角
const val TYPE_VICTIM = 0
const val TYPE_HACKER = 1

//episode_type
const val TYPE_START = "start"
const val TYPE_NORMAL = "normal"
const val TYPE_FINISHED = "ending"

const val OPTION_TYPE_UNDEFINED = "undefined"
const val OPTION_TYPE_LINK = "link"
const val OPTION_TYPE_EPISODE = "episode"

//interact_type
const val QUESTION_SELECT = 0
const val QUESTION_ANSWER = 1



data class ScriptResponse(val status: String, val scriptList: List<ScriptDetail>)

data class ScriptAbstract(
    @SerializedName("script_id") var id: String = "",
    @SerializedName("script_type") var type: Int = TYPE_VICTIM,
    @SerializedName("script_coverImage_url") var coverImageUrl: String = "",
    var title: String = "",
    var label: String = "",
    var summary: String = "",
    var date: String = "",
    var clickRate: Int = 0,
    val user: String = ""
)

data class ScriptDetail(
    @SerializedName("script_id") var id: String = "",
    @SerializedName("script_type") var type: Int = TYPE_VICTIM,
    @SerializedName("script_coverImage_url") var coverImageUrl: String = "",
    var title: String = "",
    var label: String = "",
    var summary: String = "",
    var date: String = "",
    var clickRate: Int = 0,
    var user: String = "",
    var episodeList: MutableList<Episode> = arrayListOf()
)

data class Episode(
    @SerializedName("episode_id") val id: String = "",
    @SerializedName("script_id") val scriptId: String = "",
    @SerializedName("episode_url") var url: String = "",
    @SerializedName("episode_title") var title: String = "",
    @SerializedName("episode_type") var type: String = TYPE_NORMAL,
    @SerializedName("episode_cover") var coverImage: String = "",
    @SerializedName("interact_method") var interact: Question? = null
)

data class Question(
    @SerializedName("question_type") var type: Int = QUESTION_SELECT,
    var questionText: String = "",
    var answer: String = "",
    var optionList: MutableList<Option> = arrayListOf(),
    var optionTextList: MutableList<String> = arrayListOf(),
    var tips: Tips = Tips()
)

data class Option(
    //选择题的选项
    @SerializedName("episode_id") val nextEpisodeId: String = "",
    @SerializedName("option_type") val type: String = OPTION_TYPE_UNDEFINED,
    @SerializedName("option_style") val style: String = "", //样式，估计来不及做了
//    @SerializedName("option_content") val optionText: String = "",
    @SerializedName("option_x") val x: Float = -1f,
    @SerializedName("option_y") val y:Float = -1f
)

data class Tips(
    //问答题的提示
    @SerializedName("question_id") val id: String = "",
    @SerializedName("tips_image_url") val url: String = "",
    @SerializedName("tips_content") val content: String = "",
    @SerializedName("tips_x") val x: Float = -1f,
    @SerializedName("tips_y") val y: Float = -1f
)