package com.hlxh.interactivevideotool.ui.edit

import com.hlxh.interactivevideotool.model.Episode
import com.hlxh.interactivevideotool.model.OPTION_TYPE_LINK
import com.hlxh.interactivevideotool.model.OPTION_TYPE_UNDEFINED

class Node(
    var id: String,
    var episode: Episode? = null,
    val type: String = OPTION_TYPE_UNDEFINED,
    var right: Node? = null,
    var left: Node? = null,
    var parent: Node? = null
) {
    val isLeftChild
        get() = parent?.left == this

    val isRightChild
        get() = parent?.right == this

    fun findNodeById(id: String): Node? {
        return if (this.id == id) this
        else this.right?.findNodeById(id) ?: this.left?.findNodeById(id)
    }

    val isLinkedNode: Boolean get() = type == OPTION_TYPE_LINK
}