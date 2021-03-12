package com.hlxh.interactivevideotool.logic.repo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hlxh.interactivevideotool.InteractiveVideoApplication
import com.hlxh.interactivevideotool.logic.network.InteractiveVideoNetwork
import com.hlxh.interactivevideotool.model.ScriptDetail
import com.hlxh.interactivevideotool.model.TYPE_VICTIM
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Repo {

    val scripts: MutableLiveData<List<ScriptDetail>> = MutableLiveData()

    val victimScripts: MutableLiveData<List<ScriptDetail>> = MutableLiveData()

    val hackerScripts: MutableLiveData<List<ScriptDetail>> = MutableLiveData()

    suspend fun loadScriptFromServer(type: String) = InteractiveVideoNetwork.loadScript(type).enqueue()  //VideoResponse类型数据

    suspend fun loadScriptDetailFromServer(id: String) = InteractiveVideoNetwork.loadScriptDetail(id).enqueue()    //VideoDetail类型数据

    suspend fun loadUserScript(user: String) = InteractiveVideoNetwork.loadUserScript(user).enqueue()    //暂时用不着这个方法

    fun loadAllScripts() {
        val scriptList: MutableList<ScriptDetail> = mutableListOf()
        scriptList.addAll(loadVictimScript())
        scriptList.addAll(loadHackerScript())
        scripts.value = scriptList
    }

    fun loadFromAssets(type: String): List<ScriptDetail> {
        val localScripts: MutableList<ScriptDetail> = mutableListOf()
        val dirs = InteractiveVideoApplication.context.assets.list(type)  //assets/type下所有文件夹路径
        val gson = GsonBuilder().create()
        dirs?.forEach {
            var stream: InputStream? = null
            try {
                val scriptFileName = "$type/$it/script.json"
                stream = InteractiveVideoApplication.context.assets.open(scriptFileName)
                val script =
                    gson.fromJson(BufferedReader(InputStreamReader(stream)), ScriptDetail::class.java)
                localScripts.add(script)
            } catch (e: Exception) {
                stream?.close()
            }
        }
        return localScripts
    }
    fun loadFromFile(type: String): List<ScriptDetail> {
        val localScripts: MutableList<ScriptDetail> = mutableListOf()
        val fileNames = InteractiveVideoApplication.context.filesDir
            .list()
            .filter { it.startsWith("${type}_") }
        try {
            fileNames?.forEach {
                val input = InteractiveVideoApplication.context.openFileInput(it)
                val reader = BufferedReader(InputStreamReader(input))
                val script = Gson().fromJson(reader, ScriptDetail::class.java)

                localScripts.add(script)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return localScripts
    }
    fun writeToFile(script: ScriptDetail) {
        val type = if (script.type == TYPE_VICTIM) "victim" else "hacker"
        val fileName = "${type}_${script.id}.json"
        val gson = GsonBuilder().create()
        try {
            val output = InteractiveVideoApplication.context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(output))

            writer.use {
                it.write(gson.toJson(script))
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadVictimScript(): List<ScriptDetail> {
        val scriptList: MutableList<ScriptDetail> = mutableListOf()
        scriptList.addAll(loadFromAssets("victim"))
        scriptList.addAll(loadFromFile("victim"))
        victimScripts.value = scriptList
        return scriptList
    }

    fun loadHackerScript(): List<ScriptDetail> {
        val scriptList: MutableList<ScriptDetail> = mutableListOf()
        scriptList.addAll(loadFromAssets("hacker"))
        scriptList.addAll(loadFromFile("hacker"))
        hackerScripts.value = scriptList
        return scriptList
    }

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