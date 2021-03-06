package com.hlxh.interactivevideotool.logic.network

import com.hlxh.interactivevideotool.model.ScriptResponse
import com.hlxh.interactivevideotool.model.ScriptDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface LoadScriptService {
//    @GET("videoresponse")
//    fun loadVideos(@Query("query") query: String): Call<VideoResponse>

    @GET("script/{type}")
    fun loadScript(@Path("type") type: String): Call<ScriptResponse>

    @GET("scriptDetail/{id}")
    fun loadScriptDetail(@Path("id") id: String): Call<ScriptDetail>

}