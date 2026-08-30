package com.gyros.startchat.screens.startchat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.gyros.startchat.data.UrlOpener
import com.gyros.startchat.data.UrlOpenerImpl
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StartChatScreenWithKoin(
    modifier: Modifier = Modifier,
    onNavigationIconClick: () -> Unit = {}
) {
    val viewModel: StartChatViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val urlOpener: UrlOpener = remember { UrlOpenerImpl() }

    LaunchedEffect(viewModel) {
        viewModel.start(actionText = null)
        viewModel.events.collect { event ->
            when (event) {
                is StartChatViewModel.Events.StartIntentAction -> {
                    urlOpener.open(event.uri)
                }
            }
        }
    }

    StartChatScreen(
        modifier = modifier,
        state = state,
        onNavigationIconClick = onNavigationIconClick
    )
}