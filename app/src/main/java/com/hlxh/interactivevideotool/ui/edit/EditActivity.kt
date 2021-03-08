package com.hlxh.interactivevideotool.ui.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.find
import com.hlxh.interactivevideotool.findStart
import com.hlxh.interactivevideotool.model.*

import kotlinx.android.synthetic.main.pin_layout.*

class EditActivity : AppCompatActivity(), DrawerLayout.DrawerListener {

    companion object {
        fun start(context: Context, scriptId: String? = null) {
            context.startActivity(
                Intent(context, EditActivity::class.java).apply {
                    scriptId?.let { putExtra("scriptId", it) }
                })
        }
    }
    private lateinit var script: ScriptDetail

    private val transitionLeftToRoot by lazy {
        motionLayout.getTransition(R.id.transition_left_to_root)
    }
    private val transitionRightToRoot by lazy {
        motionLayout.getTransition(R.id.transition_right_to_root)
    }
    private val transitionRootToLeft by lazy {
        motionLayout.getTransition(R.id.transition_root_to_left)
    }
    private val transitionRootToRight by lazy {
        motionLayout.getTransition(R.id.transition_root_to_right)
    }

    private val node_a by lazy {
        findViewById<ViewGroup>(R.id.node_a)
            .also { it.mask().setOnClickListener { } }
    }

    private val node_b by lazy {
        findViewById<ViewGroup>(R.id.node_b)
            .also { it.mask().setOnClickListener { } }
    }

    private val node_s by lazy {
        findViewById<ViewGroup>(R.id.node_s)
            .also { it.mask().setOnClickListener { } }
    }

    private val node_aa by lazy {
        findViewById<ViewGroup>(R.id.node_aa)
    }

    private val node_ab by lazy {
        findViewById<ViewGroup>(R.id.node_ab)
    }

    private val node_ba by lazy {
        findViewById<ViewGroup>(R.id.node_ba)
    }

    private val node_bb by lazy {
        findViewById<ViewGroup>(R.id.node_bb)
    }

    private val node_p1 by lazy {
        findViewById<ViewGroup>(R.id.node_p1)
    }

    private val node_p2 by lazy {
        findViewById<ViewGroup>(R.id.node_p2)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pin_layout)

        setUpMotion()
        setUpData()
        setUpDrawer()

    }

    private fun setUpData() {

        val videoId = intent.getStringExtra("videoId")

        if (!TextUtils.isEmpty(videoId)) {
//            ScriptDataSource.scripts.value!!.find { it.id == videoId }.let {
//                if (it != null) {
//                    video = it
//                } else {
//                    finish()
//                }
//            }

            if (script.episodeList.isEmpty()) {
                initView()
                return
            }
            val rootEpisode = script.findStart()!!
            root =
                Node(id = rootEpisode.id, episode = rootEpisode, type = OPTION_TYPE_EPISODE).also {
                    completeNode(it)
                    cur = it
                }

            updateView()
        }
    }

    private fun completeNode(node: Node) {

        val optionA = node.episode?.interact?.optionList?.getOrNull(0)
        val optionB = node.episode?.interact?.optionList?.getOrNull(1)


        if (optionA != null) {
            script.find(optionA.nextEpisodeId)?.also {
                node.left = Node(id = it.id, episode = it, parent = node, type = optionA.type)
                if (optionA.type == OPTION_TYPE_EPISODE) {
                    completeNode(node.left!!)
                }
            }
        }

        if (optionB != null) {
            script.find(optionB.nextEpisodeId)?.also {
                node.right = Node(id = it.id, episode = it, parent = node, type = optionB.type)
                if (optionB.type == OPTION_TYPE_EPISODE) {
                    completeNode(node.right!!)
                }
            }
        }

    }

    private val catalogDrawerFragment = CatalogFragment()

    private fun setUpDrawer() {
        val drawer = drawerLayout
        drawer.addDrawerListener(this)
        catalogDrawerFragment.apply {
            setOnConfirmListener(object : CatalogFragment.OnConfirmListener {
                override fun onConfirm(node: Node) {
                    locateToNode(node)
                    drawer.closeDrawer(GravityCompat.END)
                }
            })
            root?.run {
                setNode(root, cur)
            }
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.script_catalog, catalogDrawerFragment, "drawerCatalog")
            .commit()
    }


    private val catalogBottomSheetFragment = CatalogFragment().apply {
        setOnConfirmListener(object : CatalogFragment.OnConfirmListener {
            override fun onConfirm(node: Node) {
                addLinkedNode(node)
                this@apply.dismiss()
                bottomSheet.dismiss()
            }
        })
    }

    private fun addLinkedNode(node: Node) {
        node.episode?.also {
            createNode(it, OPTION_TYPE_LINK)
        }
    }

    private fun locateToNode(node: Node) {
        root?.run {
            findNodeById(node.id)?.also {
                cur = it
                updateView()
                catalogDrawerFragment.setNode(this, node)
            }
        }
    }

    private val bottomSheet: BottomSheetDialog by lazy {
        BottomSheetDialog(this).also {
            it.setContentView(R.layout.layout_edit_node)
        }
    }

    private fun showBottomSheet(node: Node?, scriptId: String = "") {
        val episodeId = node?.episode?.id
        val isCreation = node == null
        val isLinkNode = node?.type == OPTION_TYPE_LINK
        bottomSheet.findViewById<View>(R.id.btnEdit)
            ?.apply {
                visibility = if (isLinkNode) View.GONE else View.VISIBLE
                setOnClickListener {
                    val editConfigureFragment = EditConfigureFragment()
                    editConfigureFragment.arguments = Bundle().apply {
                        putString(EditConfigureFragment.KEY_EPISODE_ID, episodeId)
                        putString(EditConfigureFragment.KEY_SCRIPT_ID, scriptId)
                    }
                    editConfigureFragment.setOnSave {
                        if (episodeId == null) {
                            editConfigureFragment.dismiss()
                            createNode(it, OPTION_TYPE_EPISODE)
                        } else {
                            editConfigureFragment.dismiss()
                        }

                        updateView()
                    }
                    editConfigureFragment.show(supportFragmentManager, "editConfig")

                    bottomSheet.dismiss()
                }
            }
        bottomSheet.findViewById<View>(R.id.btnLink)
            ?.apply {
                visibility = if (!isCreation) View.GONE else View.VISIBLE
                setOnClickListener {
                    catalogBottomSheetFragment.setNode(root, cur)
                    catalogBottomSheetFragment.show(supportFragmentManager, "catalog")
                }
            }
        bottomSheet.findViewById<View>(R.id.btnDelete)
            ?.apply {
                visibility = if (isCreation) View.GONE else View.VISIBLE
                setOnClickListener {
                    showDeleteAlert(node)
                }
            }
        bottomSheet.show()
    }

    private fun showDeleteAlert(node: Node?) {
        if (node == null) return
        val message = if (node.isLinkedNode) "确定删除？" else "会同时删除链接到此节点的其他节点，确定删除？"
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("确定") { dialog, which ->
                handleDeleteNode(node)
                catalogDrawerFragment.setNode(root, cur)
                dialog.dismiss()
                bottomSheet.dismiss()
            }
            .setNegativeButton("取消") { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun handleDeleteNode(removed: Node) {
        if (removed.isLinkedNode) {
            removed.parent?.also { parent ->
                script.removeNodeFromParent(parent, removed)
                if (cur === removed) cur = parent
                updateView()
            }
        } else {
            if (root === removed) {
                root = null
                script.episodeList.clear()
                initView()
                return
            } else {
                removed.parent?.also { script.removeNodeFromParent(it, removed) }
                root?.also {
                    script.removeNodeLinkedToTarget(it, removed)
                }
                if (cur === removed) removed.parent?.also { cur = it }
            }
            updateView()
        }
    }

    private var root: Node? = null
    private lateinit var cur: Node

    private var arrBackup: Int = 0

    private val transitionListener by lazy {
        object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                //设置箭头的透明度
                if (arrVisible != 0) arrBackup = arrVisible
                arrVisible = 0
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, currentId: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
                when (currentId) {
                    R.id.left_to_root -> {
                        (node_a.getTag(R.id.card_text) as? Node)?.let { cur = it }
                        //root?.findNodeById(node_a.getValue())?.let { cur = it }
                    }
                    R.id.right_to_root -> {
                        (node_b.getTag(R.id.card_text) as? Node)?.let { cur = it }
                        //root?.findNodeById(node_b.getValue())?.let { cur = it }
                    }
                    R.id.root_to_left -> {
                        (node_p1.getTag(R.id.card_text) as? Node)?.let { cur = it }
                        //root?.findNodeById(node_p1.getValue())?.let { cur = it }
                    }
                    R.id.root_to_right -> {
                        (node_p2.getTag(R.id.card_text) as? Node)?.let { cur = it }
                        //root?.findNodeById(node_p2.getValue())?.let { cur = it }
                    }
                    else -> {
                        arrVisible = arrBackup
                        return
                    }
                }
                updateView()
                catalogDrawerFragment.setNode(root, cur)
            }

        }
    }

    private fun setUpMotion() {
        motionLayout.setTransitionListener(transitionListener)
        initView()
    }

    private fun createNode(episode: Episode, type: String) {
        script.episodeList.add(episode)
        when (clickedView) {
            node_s -> {
                if (root == null) {
                    episode.type = TYPE_START
                    root = Node(id = episode.id, episode = episode, type = type).also { cur = it }
                } else {
                    cur.id = episode.id
                }
            }
            node_a -> {
                cur.left?.also { it.id = episode.id } ?: run {
                    cur.episode?.interact?.optionTextList?.add("")
                    cur.episode?.interact?.optionList?.add(Option(nextEpisodeId = episode.id, type = type))
                    cur.left =
                        Node(id = episode.id, episode = episode, type = type).apply { parent = cur }
                }
            }
            node_b -> {
                cur.right?.also { it.id = episode.id } ?: run {
                    cur.episode?.interact?.optionTextList?.add("")
                    cur.episode?.interact?.optionList?.add(Option(nextEpisodeId = episode.id, type = type))
                    cur.right =
                        Node(id = episode.id, episode = episode, type = type).apply { parent = cur }
                }
            }
        }
        updateView()
    }

    private lateinit var clickedView: MaterialCardView

    private fun initView() {
        node_s.setValue("+")
        node_a.setValue("+")
        node_b.setValue("+")
        setTransitions(false)
        arrVisible = 0

        arrayOf(node_a, node_b, node_s).forEach { cardView ->
            cardView.findViewById<View>(R.id.clickable).setOnLongClickListener {
                if (root == null) {
                    if (cardView != node_s) return@setOnLongClickListener false
                }
                clickedView = cardView as MaterialCardView
                (cardView.getTag(R.id.card_text) as? Node?).also {
                    //show bottom sheet
                    if (this::script.isInitialized) {
                        showBottomSheet(it, script.id)
                    } else {
                        showBottomSheet(it)
                    }

                }
                true
            }
        }

    }

    private var arrVisible = 0
        set(value) {
            field = value
            //设置箭头图案对透明度
            right_arr.alpha = if (value and 0b01 > 0) 1F else 0.3F
            left_arr.alpha = if (value and 0b10 > 0) 1F else 0.3F
        }


    private fun updateView() {
        motionLayout.progress = 0F
        arrVisible = 0
        setTransitions(false)
        node_a.canTounch(false)
        node_b.canTounch(false)
        Log.e("wangp", "update cur: ${cur.id}")
        _update(cur, node_s, node_a, node_b)
        cur.left?.let {
            arrVisible = arrVisible or 0b10
            Log.e("wangp", "update cur left: ${it.id}")

            //link节点不允许出现子节点加号
            if (it.isLinkedNode) return@let

            _update(it, null, node_aa, node_ab)
//            transitionLeftToRoot.setEnable(true)

            motionLayout.definedTransitions.add(transitionLeftToRoot)
            node_a.canTounch(true)

        }
//            ?: transitionLeftToRoot.setEnable(false)
        cur.right?.let {
            arrVisible = arrVisible or 0b01
            Log.e("wangp", "update cur right: ${it.id}")

            //link节点不允许出现子节点加号
            if (it.isLinkedNode) return@let

            _update(it, null, node_ba, node_bb)
//            transitionRightToRoot.setEnable(true)
            motionLayout.definedTransitions.add(transitionRightToRoot)
            node_b.canTounch(true)

        }
//            ?: transitionRightToRoot.setEnable(false)
        if (cur.parent == null) {
            node_s.canTounch(false)
//            transitionRootToLeft.setEnable(false)
//            transitionRootToRight.setEnable(false)
        } else {
            node_s.canTounch(true)
            if (cur.isLeftChild) {
//            transitionRootToRight.setEnable(false)
//            transitionRootToLeft.setEnable(true)

                motionLayout.definedTransitions.add(transitionRootToLeft)


                Log.e("wangp", "update cur(left) parent: ${cur.parent}")
                _update(cur.parent!!, node_p1, node_s, node_p2)
            } else if (cur.isRightChild) {
//            transitionRootToLeft.setEnable(false)
//            transitionRootToRight.setEnable(true)
                motionLayout.definedTransitions.add(transitionRootToRight)


                Log.e("wangp", "update cur(right) parent: ${cur.parent}")
                _update(cur.parent!!, node_p2, node_p1, node_s)
            }
        }


    }

    private fun setTransitions(enable: Boolean) {
    //??
        if (!enable) {
            Log.e("wangp", "clear")
            arrayOf(
                transitionLeftToRoot,
                transitionRightToRoot,
                transitionRootToLeft,
                transitionRootToRight
            ).filter {
                it in motionLayout.definedTransitions }.forEach {
                motionLayout.definedTransitions.remove(it)
            }
            return
        }

    }

    override fun onDrawerStateChanged(newState: Int) {
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerOpened(drawerView: View) {
    }

}

private fun _update(node: Node, parentView: ViewGroup?, leftView: ViewGroup, rightView: ViewGroup) {
    with(node) {
        parentView?.setValue(id)
        parentView?.setType(node.type)
        parentView?.setTag(R.id.card_text, node)
        if (!node.isLinkedNode) {
            leftView.setValue(left?.id ?: "+")
            leftView.setType(left?.type)
            leftView.setTag(R.id.card_text, left)
            rightView.setValue(right?.id ?: "+")
            rightView.setType(right?.type)
            rightView.setTag(R.id.card_text, right)
        } else {
            leftView.setValue("+")
            leftView.setType(null)
            leftView.setTag(R.id.card_text, null)
            rightView.setValue("+")
            rightView.setType(null)
            rightView.setTag(R.id.card_text, null)
        }
    }
}

private fun ViewGroup.getValue() = findViewById<TextView>(R.id.card_text).text.toString()
private fun ViewGroup.setValue(id: String) = run {
    this.findViewById<TextView>(R.id.card_text).text = id
}

private fun ViewGroup.setType(type: String?) = run {
    this.findViewById<TextView>(R.id.card_text).setTextColor(
        this.context.resources.getColor(
            if (type == OPTION_TYPE_LINK)
                R.color.current_indicator
            else
                R.color.white
        )
    )
}




private fun Episode.displayName(): String {
    return if (this.title.isEmpty()) "未命名" else this.title
}


private fun ViewGroup.mask(): View {
    return findViewById(R.id.mask)!!
}

private fun ViewGroup.canTounch(boolean: Boolean) =
    run { mask().visibility = if (boolean) View.GONE else View.VISIBLE }