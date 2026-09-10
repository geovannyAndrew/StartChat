package com.gyros.startchat

import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.platform.AccessibilitySyncOptions
import androidx.compose.ui.window.ComposeUIViewController
import com.gyros.startchat.data.PendingSharedTextStoreInterface
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDate
import platform.Foundation.NSUserDefaults
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIPasteboard
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeApi::class)
fun createMainViewController(): UIViewController {
    initKoin()
    if (PlatformArgs.clearClipboardOnLaunch) {
        UIPasteboard.generalPasteboard().string = ""
    }
    val initialActionText = readPendingSharedText()
    return ComposeUIViewController(
        configure = {
            accessibilitySyncOptions = AccessibilitySyncOptions.Always(null)
        }
    ) {
        StartChatMainScreen(actionText = initialActionText)
    }
}

private fun readPendingSharedText(): String? {
    val defaults =
        NSUserDefaults(suiteName = PendingSharedTextStoreInterface.APP_GROUP_ID) ?: return null
    val text =
        defaults.stringForKey(PendingSharedTextStoreInterface.KEY_PENDING_TEXT) ?: return null
    val timestamp =
        defaults.objectForKey(PendingSharedTextStoreInterface.KEY_PENDING_TIMESTAMP) as? Long
            ?: return null
    val now = (NSDate().timeIntervalSince1970 * 1000).toLong()
    val age = now - timestamp
    defaults.removeObjectForKey(PendingSharedTextStoreInterface.KEY_PENDING_TEXT)
    defaults.removeObjectForKey(PendingSharedTextStoreInterface.KEY_PENDING_TIMESTAMP)
    return if (age <= PendingSharedTextStoreInterface.MAX_AGE_MS) text else null
}

internal object PlatformArgs {
    private val args: List<*>
        get() = platform.Foundation.NSProcessInfo.processInfo().arguments

    val isUitesting: Boolean
        get() = args.any { it == "--uitesting" }

    val clearClipboardOnLaunch: Boolean
        get() = args.any { it == "--uitesting" } && !args.any { it == "--no-clipboard-clear" }
}
