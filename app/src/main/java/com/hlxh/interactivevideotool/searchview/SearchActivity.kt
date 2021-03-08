package com.hlxh.interactivevideotool.searchview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hlxh.interactivevideotool.R
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    private lateinit var mSearchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        mSearchView = search_view

        //点击搜索按钮后的操作
        mSearchView.setOnClickSearch {
            Log.d("search", "I receive $it")
        }

        //点击返回按键后的操作
        mSearchView.setOnClickBack {
            finish()
        }
    }
    companion object {
        fun start(context: Context) {
            context.startActivity(
                Intent(context, SearchActivity::class.java)
            )
        }
    }
}