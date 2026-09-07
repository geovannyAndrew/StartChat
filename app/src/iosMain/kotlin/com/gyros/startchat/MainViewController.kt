package com.gyros.startchat

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class)
fun createMainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController {
        StartChatMainScreen()
    }
}
