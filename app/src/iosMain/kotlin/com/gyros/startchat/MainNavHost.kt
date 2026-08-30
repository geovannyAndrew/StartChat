package com.gyros.startchat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gyros.startchat.screens.about.AboutScreenWithViewModel
import com.gyros.startchat.screens.history.ChatHistoryScreenWithViewModel
import com.gyros.startchat.screens.startchat.StartChatScreenWithKoin

enum class Screen {
    START_CHAT, HISTORY, ABOUT
}

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    onNavigationIconClick: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf(Screen.START_CHAT) }

    Box(modifier = modifier.fillMaxSize()) {
        when (currentScreen) {
            Screen.START_CHAT -> {
                StartChatScreenWithKoin(
                    onNavigationIconClick = onNavigationIconClick
                )
            }

            Screen.HISTORY -> {
                ChatHistoryScreenWithViewModel(
                    onNavigationIconClick = onNavigationIconClick
                )
            }

            Screen.ABOUT -> {
                AboutScreenWithViewModel(
                    onNavigationIconClick = onNavigationIconClick
                )
            }
        }
    }
}