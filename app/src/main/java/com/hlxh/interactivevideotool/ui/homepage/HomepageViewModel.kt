package com.hlxh.interactivevideotool.ui.homepage

import android.util.Log
import androidx.lifecycle.*
import com.hlxh.interactivevideotool.logic.repo.Repo

import com.hlxh.interactivevideotool.model.ScriptDetail
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomepageViewModel : ViewModel() {
    private val searchLiveData = MutableLiveData<String>()  //搜索框查询

    private val filterLabel = MutableLiveData<String>()     //标签查询

    val scriptLiveData = MutableLiveData< List<ScriptDetail>>()

    val allScriptListLiveData = MutableLiveData< List<ScriptDetail> >()

    val scripts: MutableLiveData<List<ScriptDetail>> = MutableLiveData()

    fun loadTest(type: String) {
        scripts.value = Repo.loadFromAssets(type)
    }

    fun loadScriptAbstract(type: String) {
        Log.d("load", "load_type = $type")
        viewModelScope.launch {

            scriptLiveData.value = Repo.loadScriptFromServer(type).scriptList
        }
    }

    fun loadScriptAll() {
         viewModelScope.launch {
             val victimResponse = async {
                 Repo.loadScriptFromServer("victim")  //VideoAbstractResponse
             }
        //             val hackerResponse = async {
        //                 Repo.loadScriptFromServer("hacker")
        //             }
             val victimScriptList = victimResponse.await()
        //             val hackerScriptList = hackerResponse.await()

             val tmpList = victimScriptList.scriptList as ArrayList<ScriptDetail>
        //             tmpList.addAll(hackerScriptList.scriptList)
             tmpList.sortBy { it.id }

             allScriptListLiveData.value = tmpList
             Repo.scripts.value = tmpList
         }
    }
}
