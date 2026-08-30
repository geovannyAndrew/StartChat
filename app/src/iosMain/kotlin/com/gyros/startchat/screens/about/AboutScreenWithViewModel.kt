package com.gyros.startchat.screens.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gyros.startchat.data.AppInfo
import com.gyros.startchat.data.AppInfoImpl

@Composable
fun AboutScreenWithViewModel(
    onNavigationIconClick: () -> Unit = {}
) {
    val appInfo: AppInfo = remember { AppInfoImpl() }
    val version = remember { appInfo.appVersion() }

    AboutScreen(
        version = version,
        onNavigationIconClick = onNavigationIconClick
    )
}