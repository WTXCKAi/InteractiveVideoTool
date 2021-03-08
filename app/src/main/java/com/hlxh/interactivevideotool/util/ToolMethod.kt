package com.hlxh.interactivevideotool

import android.net.Uri
import com.hlxh.interactivevideotool.model.Episode
import com.hlxh.interactivevideotool.model.TYPE_FINISHED
import com.hlxh.interactivevideotool.model.TYPE_START
import com.hlxh.interactivevideotool.model.ScriptDetail

fun fromAsset(name: String): Uri {
    return Uri.parse("file:///android_asset/${name}")
}

fun fromFile(name: String): Uri {
    return Uri.parse("file://${name}")
}

fun parseUrl(url: String): Uri {

    return when {
        url.startsWith("http://") -> Uri.parse(url)

        url.startsWith("/") -> fromFile(url)

        else -> fromAsset(url)

    }
}

fun ScriptDetail.findStart(): Episode? {
    return episodeList.find { episode -> episode.type == TYPE_START }
}


fun ScriptDetail.find(id: String): Episode? {
    return episodeList.find { episode -> episode.id == id}
}

fun Episode.isFinished(): Boolean {
    return type == TYPE_FINISHED
}