package com.gyros.startchat.data

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log

class AppInfoImpl(
    private val context: Context
) : AppInfo {

    override fun appVersion(): String {
        return try {
            val packageInfo: PackageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("AppInfo", "Error getting app version", e)
            ""
        }
    }
}
