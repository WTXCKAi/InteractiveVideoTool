package com.hlxh.interactivevideotool.ui.mine

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlxh.interactivevideotool.logic.repo.Repo
import com.hlxh.interactivevideotool.model.ScriptResponse
import kotlinx.coroutines.launch

class MineViewModel : ViewModel() {

    val videoAbstractLiveData = MutableLiveData<ScriptResponse>()


    fun loadUserVideo(type: String) {
        Log.d("load", "load_type = $type")
        viewModelScope.launch {
            videoAbstractLiveData.value = Repo.loadUserScript(type)
        }
    }

}