package com.gyros.startchat.screens.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.gyros.startchat.data.UrlOpenerImpl
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatHistoryScreenWithViewModel(
    modifier: Modifier = Modifier,
    onNavigationIconClick: () -> Unit = {}
) {
    val viewModel = koinViewModel<ChatHistoryViewModel>()
    val context = LocalContext.current
    val urlOpener = remember { UrlOpenerImpl(context) }

    LaunchedEffect(viewModel) {
        viewModel.load()
        viewModel.events.collect { event ->
            when (event) {
                is ChatHistoryViewModel.Events.OpenWhatsApp ->
                    urlOpener.open(event.uri)
            }
        }
    }

    val state by viewModel.state.collectAsState()
    ChatHistoryScreen(
        modifier = modifier,
        state = state,
        onNavigationIconClick = onNavigationIconClick
    )
}
