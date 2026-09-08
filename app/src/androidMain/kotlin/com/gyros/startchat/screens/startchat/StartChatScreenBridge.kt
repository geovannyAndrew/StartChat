package com.gyros.startchat.screens.startchat

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StartChatScreenForShare(
    modifier: Modifier = Modifier,
    actionText: String
) {
    val activity = LocalActivity.current
    StartChatScreenWithViewModel(
        modifier = modifier,
        actionText = actionText,
        onStartChatLaunched = { activity?.finish() }
    )
}
