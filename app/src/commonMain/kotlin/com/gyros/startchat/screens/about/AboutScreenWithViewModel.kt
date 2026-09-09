package com.gyros.startchat.screens.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gyros.startchat.data.AppInfo
import org.koin.compose.koinInject

/** Injects [AppInfo] and passes the version to [AboutScreen]. */
@Composable
fun AboutScreenWithViewModel(
    onNavigationIconClick: () -> Unit = {}
) {
    val appInfo = koinInject<AppInfo>()
    val version = remember { appInfo.appVersion() }

    AboutScreen(
        version = version,
        onNavigationIconClick = onNavigationIconClick
    )
}
