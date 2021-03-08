package com.hlxh.interactivevideotool.ui.homepage

import android.util.Log
import androidx.lifecycle.*
import com.hlxh.interactivevideotool.logic.repo.Repo
import com.hlxh.interactivevideotool.model.ScriptAbstract

import com.hlxh.interactivevideotool.model.ScriptAbstractResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomepageViewModel : ViewModel() {
    private val searchLiveData = MutableLiveData<String>()  //搜索框查询

    private val filterLabel = MutableLiveData<String>()     //标签查询

    val scriptAbstractLiveData = MutableLiveData<ScriptAbstractResponse>()

    val allScriptListLiveData = MutableLiveData< List<ScriptAbstract> >()


    fun loadScriptAbstract(type: String) {
        Log.d("load", "load_type = $type")
        viewModelScope.launch {
            scriptAbstractLiveData.value = Repo.loadScriptAbstract(type)
        }
    }

     fun loadScriptAbstractAll() {
         viewModelScope.launch {
             val victimResponse = async {
                 Repo.loadScriptAbstract("victim")  //VideoAbstractResponse
             }
             val hackerResponse = async {
                 Repo.loadScriptAbstract("hacker")
             }
             val victimScriptList = victimResponse.await()
             val hackerScriptList = hackerResponse.await()

             val tmpList = victimScriptList.scriptAbstractList as ArrayList<ScriptAbstract>
             tmpList.addAll(hackerScriptList.scriptAbstractList)
             tmpList.sortBy { it.id }

             allScriptListLiveData.value = tmpList
         }
    }
}
