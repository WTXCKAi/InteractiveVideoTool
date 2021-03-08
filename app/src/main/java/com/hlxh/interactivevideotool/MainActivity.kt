package com.hlxh.interactivevideotool

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.hlxh.interactivevideotool.myWidget.LooperPager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //private val mLooperPagerItems =  ArrayList<LooperPagerItem>()
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setTheme(android.R.style.ThemeOverlay_Material_Dark)

        initBottomNavView()


//        val searchView = searchView
//        searchView.setOnClickSearch { string -> println("received: $string") }
//
//        searchView.setOnClickBack(bCallBack { finish() })
    }


//    fun changeColor(dayNight: Boolean) {
//        findViewById<View>(R.id.xxx).setBackgroundResource(
//            if(dayNight) R.color.white else R.color.black)
//    }
    private fun initBottomNavView() {
        navView = nav_view
        val navController = findNavController(R.id.navigation_host_fragment)


        navView.setupWithNavController(navController)
//        navView.setItemOnTouchListener(0, object: View.OnTouchListener{
//            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                changeColor(true)
//                return true
//            }
//        })

    }


}
