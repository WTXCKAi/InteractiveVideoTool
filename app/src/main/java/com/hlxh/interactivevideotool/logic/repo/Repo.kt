package com.hlxh.interactivevideotool.logic.repo

import com.hlxh.interactivevideotool.logic.network.InteractiveVideoNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Repo {

    suspend fun loadScriptAbstract(type: String) = InteractiveVideoNetwork.loadScriptAbstract(type).enqueue()  //VideoAbstractResponse类型数据
    suspend fun loadScriptDetail(id: String) = InteractiveVideoNetwork.loadScriptDetail(id).enqueue()    //VideoDetail类型数据
    suspend fun loadUserScript(user: String) = InteractiveVideoNetwork.loadUserScript(user).enqueue()    //videoAbstractResponse类型

    private suspend fun <T> Call<T>.enqueue(): T {
        return suspendCoroutine { continuation ->
            enqueue(object: Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response boyd is null")
                    )
                }
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}