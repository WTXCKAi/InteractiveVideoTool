package com.hlxh.interactivevideotool.logic.network

object InteractiveVideoNetwork {

    //创建LoadVideoService接口的动态代理对象
    private val loadScriptService = ServiceCreator.create<LoadScriptService>()

    fun loadScript(type: String) = loadScriptService.loadScript(type)  //这里返回的是Call类型的数据

    fun loadScriptDetail(id: String) = loadScriptService.loadScriptDetail("1")

    fun loadUserScript(user: String) = loadScriptService.loadScript(user)


    //suspend fun loadVideos() = loadVideoService.loadVideos("").await()

//    private suspend fun <T> Call<T>.await(): T {
//        return suspendCoroutine { continuation ->
//            enqueue(object : Callback<T> {
//                override fun onResponse(call: Call<T>, response: Response<T>) {
//                    val body = response.body()
//                    if (body != null) continuation.resume(body)
//                    else continuation.resumeWithException(
//                        RuntimeException("response body is null")
//                    )
//                }
//
//                override fun onFailure(call: Call<T>, t: Throwable) {
//                    continuation.resumeWithException(t)
//                }
//            })
//        }
//    }
}