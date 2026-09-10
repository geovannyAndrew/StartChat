package com.gyros.startchat.data

import platform.Foundation.NSBundle

class AppInfoImpl : AppInfo {

    override fun appVersion(): String {
        return NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String
            ?: ""
    }
}