package com.hlxh.interactivevideotool.logic.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.hlxh.interactivevideotool.model.ScriptDetail
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * @author wenchengye on 02/07/2020.
 */
object ScriptDataSource {

    val scripts: MutableLiveData<List<ScriptDetail>> = MutableLiveData()

    fun loadData(context: Context) {

        if (scripts.value.isNullOrEmpty()) {

            val local = loadFromAssets(context)
            //val server = loadFromServer()

            val d = Single.zip<List<ScriptDetail>, List<ScriptDetail>, List<ScriptDetail>>(
                local,
                local,
                BiFunction { a, b ->
                    mutableListOf<ScriptDetail>().apply {
                        this.addAll(a)
                        this.addAll(b)
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    scripts.value = it
                }, {
                    it.printStackTrace()
                })
        }
    }

    private fun loadFromAssets(context: Context): Single<List<ScriptDetail>> {
        return Single.fromCallable {
            val localScripts: MutableList<ScriptDetail> = mutableListOf()
            val dirs = context.assets.list("")
            val gson = GsonBuilder().create()
            dirs?.forEach {
                var stream: InputStream? = null
                try {
                    val scriptFile = "${it}/script.json"
                    stream = context.assets.open(scriptFile)
                    val script =
                        gson.fromJson(BufferedReader(InputStreamReader(stream)), ScriptDetail::class.java)
                    localScripts.add(script)
                } catch (e: Exception) {
                    stream?.close()
                }

            }
            localScripts
        }
    }

//    private fun loadFromServer(): Single<List<ScriptDetail>> {
//        return ScriptServerRepo.getScripts().flatMap {
//
//            if (it.isNotEmpty()) {
//                var ob = ScriptServerRepo.getScript(it.first().script).map { script -> listOf(script) }
//
//                it.forEachIndexed { index, manifest ->
//                    if (index != 0) {
//                        ob = Observable.zip(ob, ScriptServerRepo.getScript(manifest.script), BiFunction { a, b ->
//                            mutableListOf<ScriptDetail>().apply {
//                                this.addAll(a)
//                                this.add(b)
//                            }
//                        })
//                    }
//                }
//
//                ob
//            } else {
//                Observable.empty()
//            }
//
//        }.firstOrError()
//    }

}