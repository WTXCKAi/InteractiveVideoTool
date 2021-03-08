package com.hlxh.interactivevideotool.player

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import java.util.*


abstract class SimpleRecycleAdapter<T>(context: Context, private var mData: List<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val inflate: LayoutInflater = LayoutInflater.from(context)
    protected val mContext: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflate.inflate(getLayoutResId(viewType), parent, false)
        view.setOnClickListener(clickListener)
        view.setOnLongClickListener(longClickListener)
        val simpleViewHolder = SimpleViewHolder(view)
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                doOnViewAttachedToWindow(simpleViewHolder)
            }

            override fun onViewDetachedFromWindow(v: View) {
                doOnViewDetachedFromWindow(simpleViewHolder)
            }
        })
        return simpleViewHolder
    }

    fun doOnViewAttachedToWindow(simpleViewHolder: SimpleViewHolder) {
        val position = simpleViewHolder.adapterPosition
        if (position < 0) {
            return
        }
        getItem(position)?.let { o ->
            simpleViewHolder.setItem(o)
            simpleViewHolder.onViewAttachedToWindow()
            convert(simpleViewHolder, o, position)
        }
    }

    fun doOnViewDetachedFromWindow(simpleViewHolder: SimpleViewHolder) {
        simpleViewHolder.onViewDetachedFromWindow()
        simpleViewHolder.unbind()
    }

    abstract fun getLayoutResId(viewType: Int): Int

    fun getItem(position: Int): T? {
        return if (0 <= position && position < mData.size) {
            mData[position]
        } else null
    }

    abstract fun convert(viewHolder: SimpleViewHolder, t: T, position: Int)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SimpleViewHolder && holder.itemView != null && isViewAttachToWindow(holder.itemView)) {
            doOnViewDetachedFromWindow(holder)
            doOnViewAttachedToWindow(holder)
        }
    }

    fun setData(data: List<T>) {
        mData = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }

    private val clickListener = View.OnClickListener { v ->
        if (v.tag != null && v.tag is SimpleViewHolder) {
            val viewHolder = v.tag as SimpleViewHolder
            getItem(viewHolder.position)?.let {
                onItemClick(viewHolder, it, viewHolder.position)
            }
        }
    }
    private val longClickListener = OnLongClickListener { v ->
        if (v.tag != null && v.tag is SimpleViewHolder) {
            val viewHolder = v.tag as SimpleViewHolder
            getItem(viewHolder.position)?.let {
                onItemLongClick(viewHolder, it, viewHolder.position)
            }
        }
        false
    }

    @Suppress("EmptyFunctionBlock")
    open fun onItemClick(v: SimpleViewHolder, t: T, position: Int) {
    }

    @Suppress("EmptyFunctionBlock")
    open fun onItemLongClick(v: SimpleViewHolder, t: T, position: Int) {
    }

    companion object {
        private fun isViewAttachToWindow(view: View): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.isAttachedToWindow
            } else {
                val handler = view.handler
                handler != null
            }
        }
    }

}

class SimpleViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
    private val mViews: SparseArray<View> = SparseArray()

    fun <V : View> getView(viewId: Int): V {
        var view = mViews[viewId]
        if (view == null) {
            view = itemView.findViewById(viewId)
            mViews.put(viewId, view)
        }
        return view as V
    }

    fun setText(viewId: Int, text: String?): SimpleViewHolder {
        val tv = getView<TextView>(viewId)!!
        tv.text = text
        return this
    }

    fun setBackgroundResource(viewId: Int, @DrawableRes resid: Int): SimpleViewHolder {
        val tv = getView<View>(viewId)
        tv.setBackgroundResource(resid)
        return this
    }

    fun setTextColor(viewId: Int, @ColorInt color: Int): SimpleViewHolder {
        val tv = getView<TextView>(viewId)
        tv.setTextColor(color)
        return this
    }

    fun setText(viewId: Int, text: CharSequence?): SimpleViewHolder {
        val tv = getView<TextView>(viewId)
        tv.text = text
        return this
    }

    fun setImageResource(viewId: Int, drawableId: Int): SimpleViewHolder {
        val view = getView<ImageView>(viewId)
        view.setImageResource(drawableId)
        return this
    }

    fun setOnClickListener(viewId: Int,
                           listener: View.OnClickListener?): SimpleViewHolder {
        val view = getView<View>(viewId)
        view.setOnClickListener(listener)
        return this
    }

    fun setImageBitmap(viewId: Int, bm: Bitmap?): SimpleViewHolder {
        val view = getView<ImageView>(viewId)
        view.setImageBitmap(bm)
        return this
    }

    fun hide(viewId: Int): SimpleViewHolder {
        val view = getView<View>(viewId)
        view.visibility = View.GONE
        return this
    }

    fun invisible(viewId: Int): SimpleViewHolder {
        val view = getView<View>(viewId)
        view.visibility = View.INVISIBLE
        return this
    }

    fun show(viewId: Int): SimpleViewHolder {
        val view = getView<View>(viewId)
        view.visibility = View.VISIBLE
        return this
    }

    private val extra: MutableMap<String, Any?> = HashMap()

    fun put(key: String, o: Any?) {
        extra[key] = o
    }

    @Suppress("EmptyCatchBlock")
    fun getInt(key: String): Int {
        val value = extra[key] ?: return 0
        if (value is String) {
            try {
                return value.toInt()
            } catch (e: NumberFormatException) {
            }
            return 0
        }
        return try {
            value as Int
        } catch (e: ClassCastException) {
            0
        }
    }

    @Suppress("EmptyCatchBlock")
    fun getLong(key: String): Long {
        val value = extra[key] ?: return 0
        if (value is String) {
            try {
                return value.toLong()
            } catch (e: NumberFormatException) {
            }
            return 0
        }
        return try {
            value as Long
        } catch (e: ClassCastException) {
            0
        }
    }

    fun <T> getItem(): T {
        return extra["__________"] as T
    }

    fun <T> setItem(item: T) {
        extra["__________"] = item
    }

    @Suppress("EmptyFunctionBlock")
    open fun onViewAttachedToWindow() {
    }

    @Suppress("EmptyFunctionBlock")
    open fun onViewDetachedFromWindow() {
    }

    @Suppress("EmptyFunctionBlock")
    open fun unbind() {
    }

    init {
        itemView.tag = this
    }
}