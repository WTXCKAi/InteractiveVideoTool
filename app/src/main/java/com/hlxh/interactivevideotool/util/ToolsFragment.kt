package com.hlxh.interactivevideotool.util

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

class  ToolsFragment : Fragment() {

    private var activityResultCallBack: ((resultCode: Int, data: Intent?) -> Unit)? = null
    private var activityResultIntent: Intent? = null

    private var permissionsCallback: ((permissions: Array<out String>, grantResults: IntArray) -> Unit)? = null
    private var permissions: Array<out String>? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activityResultIntent != null) {
            startActivityForResult(activityResultIntent, 0)
        } else if (permissions != null) {
            requestPermissions(permissions!!, 0)
        } else {
            clear()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultCallBack?.invoke(resultCode, data)
        clear()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsCallback?.invoke(permissions, grantResults)
        clear()
    }

    private fun clear() {
        activity?.apply {
            supportFragmentManager.beginTransaction().remove(this@ToolsFragment).commitAllowingStateLoss()
        }
    }

    companion object {
        fun start(activity: FragmentActivity, intent: Intent, activityResultCallBack: (resultCode: Int, data: Intent?) -> Unit) {
            activity.supportFragmentManager.beginTransaction()
                .add(ToolsFragment().apply {
                    this.activityResultCallBack = activityResultCallBack
                    this.activityResultIntent = intent
                }, "tag").commitAllowingStateLoss()
        }


        fun start(activity: FragmentActivity, permissions: Array<out String>, permissionsCallback: (permissions: Array<out String>, grantResults: IntArray) -> Unit) {
            activity.supportFragmentManager.beginTransaction()
                .add(ToolsFragment().apply {
                    this.permissions = permissions
                    this.permissionsCallback = permissionsCallback
                }, "tag").commitAllowingStateLoss()

        }
    }
}

fun FragmentActivity.startActivityForResult(intent: Intent, callback: (resultCode: Int, data: Intent?) -> Unit) {
    ToolsFragment.start(this, intent, callback)
}

fun FragmentActivity.requestPermission(vararg permissions: String, callback: (permissions: Array<out String>, grantResults: IntArray) -> Unit) {
    ToolsFragment.start(this, permissions, callback)
}

//interface AutoDisposable : LifecycleOwner {
//    fun Disposable.disposeOnStop(): Disposable {
//
//        lifecycle.addObserver(object : LifecycleObserver {
//            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//            fun onDestory() {
//                if (!isDisposed) {
//                    dispose()
//                }
//            }
//        })
//        return this
//    }
//}
