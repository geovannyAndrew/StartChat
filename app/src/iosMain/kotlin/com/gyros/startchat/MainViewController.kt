package com.gyros.startchat

import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.platform.AccessibilitySyncOptions
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIPasteboard
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeApi::class)
fun createMainViewController(): UIViewController {
    initKoin()
    if (PlatformArgs.clearClipboardOnLaunch) {
        UIPasteboard.generalPasteboard().string = ""
    }
    return ComposeUIViewController(
        configure = {
            accessibilitySyncOptions = AccessibilitySyncOptions.Always(null)
        }
    ) {
        StartChatMainScreen()
    }
}

internal object PlatformArgs {
    private val args: List<*>
        get() = platform.Foundation.NSProcessInfo.processInfo().arguments

    val isUitesting: Boolean
        get() = args.any { it == "--uitesting" }

    val clearClipboardOnLaunch: Boolean
        get() = args.any { it == "--uitesting" } && !args.any { it == "--no-clipboard-clear" }
}
