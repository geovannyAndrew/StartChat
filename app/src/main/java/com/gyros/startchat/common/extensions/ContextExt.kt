package com.gyros.startchat.common.extensions

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE

fun Context.getFromClipBoard(maxItems: Int = 3, regex: Regex? = null): List<String> {
    val clip = (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).primaryClip
        ?: return emptyList()
    val listItems = mutableListOf<String>()
    for (i in 0 until clip.itemCount) {
        val item = clip.getItemAt(i)?.text?.toString() ?: continue
        if (regex == null || regex.matches(item)) {
            listItems.add(item)
        }
        if (listItems.size >= maxItems) break
    }
    return listItems
}