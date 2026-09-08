package com.gyros.startchat.screens.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.gyros.startchat.data.UrlOpener
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatHistoryScreenWithViewModel(
    modifier: Modifier = Modifier,
    onNavigationIconClick: () -> Unit = {}
) {
    val viewModel = koinViewModel<ChatHistoryViewModel>()
    val urlOpener = koinInject<UrlOpener>()

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
