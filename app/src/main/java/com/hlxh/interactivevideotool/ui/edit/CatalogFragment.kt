package com.hlxh.interactivevideotool.ui.edit

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.model.Episode
import com.hlxh.interactivevideotool.util.dip2Px
import com.hlxh.interactivevideotool.util.sp2px
import kotlinx.android.synthetic.main.fragment_catalog.*


/**
 * @author sunjingkai
 * @since 2020/7/1
 * @lastModified by sunjingkai on 2020/7/1
 */
class CatalogFragment : FixedHeightBottomSheetDialogFragment() {

    interface OnConfirmListener {
        fun onConfirm(node: Node)
    }

    private var listener: OnConfirmListener? = null

    private var rootNode: Node? = null
    private var currentNode: Node? = null

    fun setOnConfirmListener(listener: OnConfirmListener) {
        this.listener = listener
    }

    private val list = mutableListOf<CatalogItem>()

    private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder> =
        object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder = ViewHolder(parent)

            override fun getItemCount(): Int = list.size

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val (node, episode, level, index) = list[position]
                if (episode == null) return
                (holder.itemView as TextView).apply {
                    val highLight = node === currentNode
                    setPadding(context.dip2Px(3), 0, context.dip2Px(3), 0)
                    text = "$level.$index -- ${episode.title}"
                    setBackgroundResource(if (highLight) R.drawable.shape_catalog_highlight else 0)
                    if (highLight) typeface = Typeface.DEFAULT_BOLD
                    textSize = context.sp2px(8f)
                    if (node.isLinkedNode) {
                        setTextColor(context.resources.getColor(R.color.current_indicator))
                    } else {
                        setTextColor(Color.WHITE)
                    }
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginStart = level * context.dip2Px(10)
                    }
                    setOnClickListener {
                        showAlert(node)
                    }
                }
            }
        }

    fun setNode(rootNode: Node?, currentNode: Node) {
        this.rootNode = rootNode
        this.currentNode = currentNode
        list.clear()
        rootNode?.also { list.addAll(it.mapToEpisodeList()) }
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(TextView(parent.context)) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rootView.adapter = adapter
    }

    private fun Node.mapToEpisodeList(
        level: Int = 0, index: Int = 0, result: MutableList<CatalogItem> = mutableListOf()
    ): List<CatalogItem> {
        result.add(CatalogItem(this, episode, level, index))
        if (left == null && right == null) return result

        left?.also { it.mapToEpisodeList(level + 1, 1, result) }
        right?.also { it.mapToEpisodeList(level + 1, 2, result) }
        return result
    }

    private fun Node.findEpisodeById(level: Int): Episode {
//        ScriptRepository.getEpisode(id)
        return Episode(title = "Ep$level")
    }

    private fun showAlert(node: Node) {
        AlertDialog.Builder(requireContext())
            .setMessage("确定选用？")
            .setPositiveButton("确定") { dialog, which ->
                listener?.onConfirm(node)
                dialog.dismiss()
            }
            .setNegativeButton("取消") { dialog, which -> dialog.dismiss() }
            .show()
    }


}

data class CatalogItem(
    val node: Node, val episode: Episode?, val level: Int, val index: Int
)