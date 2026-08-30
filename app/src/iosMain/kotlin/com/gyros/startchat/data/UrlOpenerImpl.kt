package com.gyros.startchat.data

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class UrlOpenerImpl : UrlOpener {
    override fun open(url: String) {
        val nsUrl = NSURL(string = url)
        val app = UIApplication.sharedApplication
        app.openURL(nsUrl)
    }
}