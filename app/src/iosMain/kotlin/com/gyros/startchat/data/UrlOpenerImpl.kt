package com.gyros.startchat.data

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

class UrlOpenerImpl : UrlOpener {
    override fun open(url: String) {
        val nsUrl = NSURL(string = url)
        dispatch_async(dispatch_get_main_queue()) {
            UIApplication.sharedApplication.openURL(
                nsUrl,
                options = emptyMap<Any?, Any>(),
                completionHandler = null
            )
        }
    }
}