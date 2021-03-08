package com.hlxh.interactivevideotool.ui.edit

import com.hlxh.interactivevideotool.model.ScriptDetail


/**
 * @author sunjingkai
 * @since 2020/7/2
 * @lastModified by sunjingkai on 2020/7/2
 */

fun ScriptDetail.removeNodeLinkedToTarget(parent: Node, target: Node) {
    parent.left?.also {
        removeNodeFromParent(it, target, true)
        removeNodeLinkedToTarget(it, target)
    }
    parent.right?.also {
        removeNodeFromParent(it, target, true)
        removeNodeLinkedToTarget(it, target)
    }
}

fun ScriptDetail.removeNodeFromParent(parent: Node, removed: Node, isLink: Boolean = false) {
    if (parent.left === removed || (isLink && parent.left?.id == removed.id)) {
        parent.left = null
        removeOptionLinkToTarget(parent, removed)
    } else if (parent.right === removed || (isLink && parent.right?.id == removed.id)) {
        parent.right = null
        removeOptionLinkToTarget(parent, removed)
    }
}

fun ScriptDetail.removeOptionLinkToTarget(
    parent: Node, node: Node
) {
    val parentEpisode = parent.episode ?: return
    val optionIndex = parentEpisode.interact?.optionList?.indexOfFirst { it.nextEpisodeId == node.episode?.id }
    if (optionIndex != null) {
        if (optionIndex < 0) return
    }
    episodeList.remove(node.episode)
    parentEpisode.interact?.apply {
        if (optionIndex != null) {
            optionList?.removeAt(optionIndex)
            optionTextList?.removeAt(optionIndex)
        }

    }
}