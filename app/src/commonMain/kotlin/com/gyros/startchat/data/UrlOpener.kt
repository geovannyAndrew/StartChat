package com.gyros.startchat.data

/** Opens a URL via a platform-specific intent or action. */
interface UrlOpener {
    fun open(url: String)
}
