package com.gyros.startchat.screens.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.gyros.startchat.data.AppInfoImpl

@Composable
fun AboutScreenWithViewModel(
    onNavigationIconClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val appInfo = remember { AppInfoImpl(context) }
    val version = remember { appInfo.appVersion() }

    AboutScreen(
        version = version,
        onNavigationIconClick = onNavigationIconClick
    )
}
