package com.gyros.startchat.screens.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.gyros.startchat.data.AppInfo
import com.gyros.startchat.data.AppInfoImpl

@Composable
fun AboutScreenWithViewModel(
    modifier: Modifier = Modifier,
    onNavigationIconClick: () -> Unit = {}
) {
    val appInfo: AppInfo = remember { AppInfoImpl() }
    val version = remember { appInfo.appVersion() }

    AboutScreen(
        version = version,
        modifier = modifier,
        onNavigationIconClick = onNavigationIconClick
    )
}