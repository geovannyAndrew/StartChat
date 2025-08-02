package com.gyros.startchat.common.extensions

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE

fun Context.getFromClipBoard(maxItems: Int = 3, regex: Regex? = null): List<String> {
    val clipBoardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val itemCount = clipBoardManager.primaryClip?.itemCount ?: 0
    val listItems = mutableListOf<String>()
    for (i in 0 until itemCount) {
        val item = clipBoardManager.primaryClip?.getItemAt(i)?.text
        regex?.let {
            if (it.matches(item.toString())) {
                listItems.add(item.toString())
            }
        } ?: run {
            if (listItems.size < maxItems) {
                listItems.add(item.toString())
            }
        }
        if (listItems.size >= maxItems) {
            break
        }

    }
    return listItems
}