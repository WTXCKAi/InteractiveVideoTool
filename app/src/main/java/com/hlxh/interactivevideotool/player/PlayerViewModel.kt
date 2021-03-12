package com.hlxh.interactivevideotool.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlxh.interactivevideotool.logic.repo.Repo
import com.hlxh.interactivevideotool.model.ScriptDetail
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {

    val scriptDetailLiveData = MutableLiveData<ScriptDetail>()

    fun loadScriptDetail(id: String) {
        viewModelScope.launch{
            scriptDetailLiveData.value = Repo.loadScriptDetailFromServer(id)
        }
    }

}