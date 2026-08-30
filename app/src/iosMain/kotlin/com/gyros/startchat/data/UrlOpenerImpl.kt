package com.gyros.startchat.data

import platform.UIKit.UIApplication
import platform.Foundation.NSURL

class UrlOpenerImpl : UrlOpener {
    override fun open(url: String) {
        val nsUrl = NSURL(string = url) ?: return
        UIApplication.shared.openURL(nsUrl)
    }
}